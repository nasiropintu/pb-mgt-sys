package com.nasirpintu.pblos.data

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.StatFs
import androidx.room.Room
import androidx.room.withTransaction
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class RecordingSession(val id: String, val startedAtElapsedMs: Long)

class CaptureRepository private constructor(private val context: Context, private val database: LocalDatabase) {
    private val dao = database.captureDao()
    private val deviceId: String by lazy {
        val preferences = context.getSharedPreferences("pblos_local_identity", Context.MODE_PRIVATE)
        preferences.getString("device_id", null) ?: UUID.randomUUID().toString().also {
            check(preferences.edit().putString("device_id", it).commit()) { "Could not save this device's local identity." }
        }
    }

    private var recorder: MediaRecorder? = null
    private var activeCaptureId: String? = null
    private var activeFile: File? = null
    private var startedAtElapsedMs: Long = 0L

    fun observeInbox(): Flow<List<InboxWithAttachments>> = dao.observeInbox()
    fun observeLatestCapture(): Flow<InboxItemEntity?> = dao.observeLatestCapture()
    fun observeSpaces(): Flow<List<SpaceEntity>> = dao.observeSpaces()
    fun observeTasks(): Flow<List<TaskWithSpace>> = dao.observeTasks()
    fun observeFocusTasks(): Flow<List<TaskWithSpace>> = dao.observeFocusTasks()
    fun observePendingTasks(): Flow<List<TaskWithSpace>> = dao.observePendingTasks()
    fun observeTasksBySpace(spaceId: String): Flow<List<TaskWithSpace>> = dao.observeTasksBySpace(spaceId)
    fun searchTasks(query: String): Flow<List<TaskWithSpace>> = dao.searchTasks(query)
    fun searchInbox(query: String): Flow<List<InboxWithAttachments>> = dao.searchInbox(query)

    suspend fun initialize() = withContext(Dispatchers.IO) {
        val now = isoUtcNow()
        dao.insertDeviceIfAbsent(
            DeviceEntity(
                id = deviceId,
                name = "${Build.MANUFACTURER.orEmpty()} ${Build.MODEL.orEmpty()}".trim().ifBlank { "This phone" },
                role = "primary_mobile",
                hardwareDescription = "${Build.MANUFACTURER.orEmpty()} ${Build.MODEL.orEmpty()}".trim(),
                createdAt = now,
                lastSeenAt = now,
            ),
        )
        dao.updateDeviceLastSeen(deviceId, now)

        // Seed default spaces if not present
        dao.insertSpaceIfAbsent(
            SpaceEntity(
                id = "space_shop",
                name = "Shop",
                slug = "shop",
                description = "Shop orders, inventory, daily records",
                colorHex = "#2F7569",
                sortOrder = 1,
                isArchived = 0,
                createdAt = now,
                updatedAt = now,
            )
        )
        dao.insertSpaceIfAbsent(
            SpaceEntity(
                id = "space_personal",
                name = "Personal",
                slug = "personal",
                description = "Personal life, family, commitments",
                colorHex = "#2858A5",
                sortOrder = 2,
                isArchived = 0,
                createdAt = now,
                updatedAt = now,
            )
        )
        dao.insertSpaceIfAbsent(
            SpaceEntity(
                id = "space_app_dev",
                name = "App Development",
                slug = "app-development",
                description = "P&B MGT Sys, learning, features",
                colorHex = "#7B1FA2",
                sortOrder = 3,
                isArchived = 0,
                createdAt = now,
                updatedAt = now,
            )
        )

        // Preserve each pre-created recording file and attachment row after process death.
        dao.findInterruptedVoiceCaptures().forEach { item ->
            val attachment = dao.findAttachmentForInboxItem(item.id)
            val file = attachment?.let { safeFile(it.relativePath) }
            val exists = file?.isFile == true
            val size = if (exists) file!!.length() else 0L
            val hash = if (exists && size > 0L) sha256(file!!) else ""
            if (attachment != null) {
                dao.finalizeAttachment(item.id, size, hash, 0L, now)
            }
            dao.updateCaptureState(
                item.id,
                "failed",
                if (exists && size > 0L) "Recording was interrupted; the partial audio file has been kept." else "Recording was interrupted before a playable audio file was saved.",
                now,
            )
        }
    }

    suspend fun createTask(
        title: String,
        description: String? = null,
        spaceId: String? = null,
        priority: String = "normal",
        dueAt: String? = null,
        isFocusToday: Boolean = false,
        sourceInboxItemId: String? = null,
    ): String = withContext(Dispatchers.IO) {
        val cleanTitle = title.trim()
        require(cleanTitle.isNotEmpty()) { "Please enter a task title." }
        val now = isoUtcNow()
        val id = UUID.randomUUID().toString()
        val task = TaskEntity(
            id = id,
            spaceId = spaceId?.takeIf { it.isNotBlank() },
            projectId = null,
            sourceInboxItemId = sourceInboxItemId,
            title = cleanTitle,
            description = description?.trim()?.takeIf { it.isNotBlank() },
            status = "approved",
            priority = priority,
            dueAt = dueAt,
            dateExpression = null,
            estimatedMinutes = null,
            isFocusToday = if (isFocusToday) 1 else 0,
            completedAt = null,
            createdAt = now,
            updatedAt = now,
        )
        dao.insertTask(task)
        id
    }

    suspend fun updateTaskDetails(
        id: String,
        title: String,
        description: String? = null,
        spaceId: String? = null,
        priority: String = "normal",
        isFocusToday: Boolean = false,
    ) = withContext(Dispatchers.IO) {
        val cleanTitle = title.trim()
        require(cleanTitle.isNotEmpty()) { "Please enter a task title." }
        val now = isoUtcNow()
        dao.findTaskById(id)?.let { existing ->
            dao.updateTask(
                existing.copy(
                    title = cleanTitle,
                    description = description?.trim()?.takeIf { it.isNotBlank() },
                    spaceId = spaceId?.takeIf { it.isNotBlank() },
                    priority = priority,
                    isFocusToday = if (isFocusToday) 1 else 0,
                    updatedAt = now,
                )
            )
        }
    }

    suspend fun deleteTask(id: String) = withContext(Dispatchers.IO) {
        dao.softDeleteTask(id, isoUtcNow())
    }

    suspend fun toggleTaskCompletion(id: String, isCurrentlyCompleted: Boolean) = withContext(Dispatchers.IO) {
        val now = isoUtcNow()
        val newStatus = if (isCurrentlyCompleted) "approved" else "completed"
        val completedAt = if (isCurrentlyCompleted) null else now
        dao.setTaskCompletion(id, newStatus, completedAt, now)
    }

    suspend fun toggleTaskFocus(id: String, isCurrentlyFocused: Boolean): Boolean = withContext(Dispatchers.IO) {
        val now = isoUtcNow()
        if (!isCurrentlyFocused) {
            val count = dao.getFocusTaskCount()
            if (count >= 3) return@withContext false
            dao.setTaskFocus(id, 1, now)
            true
        } else {
            dao.setTaskFocus(id, 0, now)
            true
        }
    }

    suspend fun saveText(text: String): String = withContext(Dispatchers.IO) {
        val normalized = text.trim()
        require(normalized.isNotEmpty()) { "Write something before saving." }
        val now = isoUtcNow()
        val id = UUID.randomUUID().toString()
        dao.insertInboxItem(
            InboxItemEntity(
                id = id,
                captureType = "text",
                sourceDeviceId = deviceId,
                capturedAt = now,
                rawContent = normalized,
                status = "saved",
                lastError = null,
                processingMetadataJson = null,
                createdAt = now,
                updatedAt = now,
            ),
        )
        id
    }

    suspend fun startVoiceCapture(): RecordingSession = withContext(Dispatchers.IO) {
        check(activeCaptureId == null) { "A recording is already in progress." }
        val stat = StatFs(context.filesDir.path)
        if (stat.availableBytes < MIN_FREE_BYTES) throw IOException("Free up some phone storage before recording.")

        val id = UUID.randomUUID().toString()
        val now = isoUtcNow()
        val recordingsDir = File(context.filesDir, RECORDINGS_DIRECTORY)
        if (!recordingsDir.exists() && !recordingsDir.mkdirs()) throw IOException("Could not create the private recordings folder.")
        val file = File(recordingsDir, "$id.m4a")
        if (!file.createNewFile()) throw IOException("Could not create a recording file.")

        val relativePath = "$RECORDINGS_DIRECTORY/${file.name}"
        val attachmentId = UUID.randomUUID().toString()
        database.withTransaction {
            dao.insertInboxItem(
                InboxItemEntity(
                    id = id,
                    captureType = "voice",
                    sourceDeviceId = deviceId,
                    capturedAt = now,
                    rawContent = null,
                    status = "processing",
                    lastError = null,
                    processingMetadataJson = null,
                    createdAt = now,
                    updatedAt = now,
                ),
            )
            dao.insertAttachment(
                AttachmentEntity(
                    id = attachmentId,
                    inboxItemId = id,
                    fileName = file.name,
                    relativePath = relativePath,
                    mimeType = "audio/mp4",
                    byteSize = 0L,
                    sha256Hash = "",
                    durationMs = null,
                    createdAt = now,
                    updatedAt = now,
                ),
            )
        }

        @Suppress("DEPRECATION")
        val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context) else MediaRecorder()
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder.setAudioSamplingRate(44_100)
            mediaRecorder.setAudioEncodingBitRate(96_000)
            mediaRecorder.setOutputFile(file.absolutePath)
            mediaRecorder.prepare()
            mediaRecorder.start()
            recorder = mediaRecorder
            activeCaptureId = id
            activeFile = file
            startedAtElapsedMs = android.os.SystemClock.elapsedRealtime()
            RecordingSession(id, startedAtElapsedMs)
        } catch (error: Exception) {
            runCatching { mediaRecorder.release() }
            val size = file.length()
            val hash = if (size > 0L) sha256(file) else ""
            dao.finalizeAttachment(id, size, hash, 0L, isoUtcNow())
            dao.updateCaptureState(id, "failed", "Recording could not start: ${error.message ?: "microphone unavailable"}", isoUtcNow())
            throw error
        }
    }

    suspend fun stopVoiceCapture(id: String) = withContext(Dispatchers.IO) {
        if (activeCaptureId != id) return@withContext
        val currentRecorder = recorder
        val file = activeFile
        val duration = (android.os.SystemClock.elapsedRealtime() - startedAtElapsedMs).coerceAtLeast(0L)
        var stopError: Exception? = null
        try {
            currentRecorder?.stop()
        } catch (error: Exception) {
            stopError = error
        } finally {
            runCatching { currentRecorder?.release() }
            recorder = null
            activeCaptureId = null
            activeFile = null
            startedAtElapsedMs = 0L
        }

        val actualFile = file
        val size = actualFile?.takeIf { it.isFile }?.length() ?: 0L
        val hash = if (actualFile != null && size > 0L) sha256(actualFile) else ""
        val now = isoUtcNow()
        database.withTransaction {
            dao.finalizeAttachment(id, size, hash, duration, now)
            if (stopError == null && size > 0L) {
                dao.markCaptureSaved(id, now)
            } else {
                dao.updateCaptureState(id, "failed", "Recording ended unexpectedly; any partial audio file has been kept.", now)
            }
        }
    }

    fun fileFor(attachment: AttachmentEntity): File? = safeFile(attachment.relativePath).takeIf { it.isFile && it.length() > 0L }

    private fun safeFile(relativePath: String): File {
        val root = context.filesDir.canonicalFile
        val file = File(root, relativePath).canonicalFile
        require(file.path.startsWith(root.path + File.separator)) { "Invalid local attachment path." }
        return file
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().buffered().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun isoUtcNow(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date())

    companion object {
        private const val DATABASE_NAME = "pblos-local.db"
        private const val RECORDINGS_DIRECTORY = "recordings"
        private const val MIN_FREE_BYTES = 8L * 1024 * 1024

        @Volatile private var instance: CaptureRepository? = null

        fun get(context: Context): CaptureRepository = instance ?: synchronized(this) {
            instance ?: CaptureRepository(
                context.applicationContext,
                Room.databaseBuilder(context.applicationContext, LocalDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build(),
            ).also { instance = it }
        }
    }
}
