package com.nasirpintu.pblos.data

import android.content.Context
import com.google.gson.Gson
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

data class BackupPayload(
    val version: Int = 1,
    val exportedAt: String = "",
    val devices: List<DeviceEntity> = emptyList(),
    val spaces: List<SpaceEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val inbox: List<InboxItemEntity> = emptyList(),
    val attachments: List<AttachmentEntity> = emptyList(),
)

data class BackupResult(
    val success: Boolean,
    val message: String,
    val restoredDevices: Int = 0,
    val restoredSpaces: Int = 0,
    val restoredTasks: Int = 0,
    val restoredInbox: Int = 0,
)

/**
 * Backup manager that exports and imports the entire Room database as JSON,
 * encrypts it with AES-GCM (256-bit) using a key derived from a user-provided passphrase
 * via PBKDF2-SHA-256 (100,000 iterations, 16-byte salt).
 *
 * File wire format:
 *   [16-byte salt][12-byte IV][AES-GCM ciphertext + 16-byte tag][32-byte SHA-256 hash of plaintext]
 */
class BackupManager(private val context: Context) {
    private val gson = Gson()
    private val secureRandom = SecureRandom()
    private val iterations = 100_000
    private val keyLenBits = 256
    private val ivSize = 12 // GCM standard
    private val tagSizeBits = 128
    private val saltSize = 16

    /** Export all tables as an encrypted binary backup file */
    fun exportEncryptedBackup(passphrase: String): File {
        val db = LocalDatabase.getInstance(context)
        val dao = db.captureDao()
        val payload = BackupPayload(
            version = 1,
            exportedAt = isoUtcNow(),
            devices = dao.getAllDevicesSync(),
            spaces = dao.getAllSpacesSync(),
            tasks = dao.getAllTasksSync(),
            inbox = dao.getAllInboxSync(),
            attachments = dao.getAllAttachmentsSync(),
        )
        val plainJson = gson.toJson(payload)

        val salt = ByteArray(saltSize).also { secureRandom.nextBytes(it) }
        val key = deriveKey(passphrase, salt)
        val iv = ByteArray(ivSize).also { secureRandom.nextBytes(it) }

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(tagSizeBits, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        val cipherOut = ByteArrayOutputStream()
        CipherOutputStream(cipherOut, cipher).use { it.write(plainJson.toByteArray(Charsets.UTF_8)) }
        val ciphertext = cipherOut.toByteArray()

        val hash = MessageDigest.getInstance("SHA-256").digest(plainJson.toByteArray(Charsets.UTF_8))

        val outFile = File(context.cacheDir, "pblos_backup_${System.currentTimeMillis()}.bin")
        FileOutputStream(outFile).use { fos ->
            fos.write(salt)
            fos.write(iv)
            fos.write(ciphertext)
            fos.write(hash)
        }
        return outFile
    }

    /** Import an encrypted backup file, decrypt with passphrase, verify SHA-256 integrity, and restore Room entities */
    fun importEncryptedBackup(backupFile: File, passphrase: String): BackupResult {
        val allBytes = backupFile.readBytes()
        if (allBytes.size < saltSize + ivSize + tagSizeBits / 8 + 32) {
            return BackupResult(false, "File is too small or corrupted.")
        }
        var offset = 0
        val salt = allBytes.sliceArray(offset until offset + saltSize).also { offset += saltSize }
        val iv = allBytes.sliceArray(offset until offset + ivSize).also { offset += ivSize }
        val hash = allBytes.sliceArray(allBytes.size - 32 until allBytes.size)
        val cipherText = allBytes.sliceArray(offset until allBytes.size - 32)

        val plainBytes = try {
            val key = deriveKey(passphrase, salt)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(tagSizeBits, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            CipherInputStream(ByteArrayInputStream(cipherText), cipher).readBytes()
        } catch (e: Exception) {
            return BackupResult(false, "Decryption failed. Incorrect passphrase or modified file.")
        }

        val computedHash = MessageDigest.getInstance("SHA-256").digest(plainBytes)
        if (!hash.contentEquals(computedHash)) {
            return BackupResult(false, "Integrity check failed. Backup data is corrupted.")
        }

        val payload = try {
            gson.fromJson(String(plainBytes, Charsets.UTF_8), BackupPayload::class.java)
        } catch (e: Exception) {
            return BackupResult(false, "Failed to parse backup payload: ${e.message}")
        }

        val db = LocalDatabase.getInstance(context)
        val dao = db.captureDao()
        db.runInTransaction {
            dao.clearAllAttachments()
            dao.clearAllInbox()
            dao.clearAllTasks()
            dao.clearAllSpaces()
            dao.clearAllDevices()

            if (payload.devices.isNotEmpty()) dao.insertDevicesSync(payload.devices)
            if (payload.spaces.isNotEmpty()) dao.insertSpacesSync(payload.spaces)
            if (payload.tasks.isNotEmpty()) dao.insertTasksSync(payload.tasks)
            if (payload.inbox.isNotEmpty()) dao.insertInboxSync(payload.inbox)
            if (payload.attachments.isNotEmpty()) dao.insertAttachmentsSync(payload.attachments)
        }

        return BackupResult(
            success = true,
            message = "Restored ${payload.tasks.size} tasks, ${payload.spaces.size} spaces, and ${payload.inbox.size} inbox items.",
            restoredDevices = payload.devices.size,
            restoredSpaces = payload.spaces.size,
            restoredTasks = payload.tasks.size,
            restoredInbox = payload.inbox.size,
        )
    }

    private fun deriveKey(passphrase: String, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, iterations, keyLenBits)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    private fun isoUtcNow(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date())
}
