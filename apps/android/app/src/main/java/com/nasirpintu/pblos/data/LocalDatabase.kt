package com.nasirpintu.pblos.data

import androidx.room.ColumnInfo
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    @ColumnInfo(name = "hardware_description", defaultValue = "''") val hardwareDescription: String = "",
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "last_seen_at") val lastSeenAt: String?,
    @ColumnInfo(name = "is_active", defaultValue = "1") val isActive: Int = 1,
)

@Entity(
    tableName = "inbox_items",
    foreignKeys = [ForeignKey(entity = DeviceEntity::class, parentColumns = ["id"], childColumns = ["source_device_id"])],
    indices = [
        Index(value = ["source_device_id"], name = "idx_inbox_source_device"),
        Index(value = ["status", "captured_at"], name = "idx_inbox_status_captured"),
    ],
)
data class InboxItemEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "capture_type") val captureType: String,
    @ColumnInfo(name = "source_device_id") val sourceDeviceId: String,
    @ColumnInfo(name = "captured_at") val capturedAt: String,
    @ColumnInfo(name = "raw_content") val rawContent: String?,
    val status: String,
    @ColumnInfo(name = "retry_count", defaultValue = "0") val retryCount: Int = 0,
    @ColumnInfo(name = "last_error") val lastError: String?,
    @ColumnInfo(name = "processing_metadata_json") val processingMetadataJson: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Int = 0,
    @ColumnInfo(name = "version", defaultValue = "1") val version: Int = 1,
)

@Entity(
    tableName = "attachments",
    foreignKeys = [ForeignKey(entity = InboxItemEntity::class, parentColumns = ["id"], childColumns = ["inbox_item_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["inbox_item_id"], name = "idx_attachments_inbox")],
)
data class AttachmentEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "inbox_item_id") val inboxItemId: String,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "relative_path") val relativePath: String,
    @ColumnInfo(name = "mime_type") val mimeType: String,
    @ColumnInfo(name = "byte_size") val byteSize: Long,
    @ColumnInfo(name = "sha256_hash") val sha256Hash: String,
    @ColumnInfo(name = "duration_ms") val durationMs: Long?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Int = 0,
    @ColumnInfo(name = "version", defaultValue = "1") val version: Int = 1,
)

data class InboxWithAttachments(
    @Embedded val item: InboxItemEntity,
    @Relation(parentColumn = "id", entityColumn = "inbox_item_id")
    val attachments: List<AttachmentEntity>,
)

@Dao
interface CaptureDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDeviceIfAbsent(device: DeviceEntity)

    @Query("UPDATE devices SET last_seen_at = :lastSeenAt, is_active = 1 WHERE id = :id")
    suspend fun updateDeviceLastSeen(id: String, lastSeenAt: String)

    @Insert
    suspend fun insertInboxItem(item: InboxItemEntity)

    @Insert
    suspend fun insertAttachment(attachment: AttachmentEntity)

    @Transaction
    @Query("SELECT * FROM inbox_items WHERE is_deleted = 0 ORDER BY captured_at DESC")
    fun observeInbox(): Flow<List<InboxWithAttachments>>

    @Query("SELECT * FROM inbox_items WHERE capture_type = 'voice' AND status = 'processing' AND is_deleted = 0")
    suspend fun findInterruptedVoiceCaptures(): List<InboxItemEntity>

    @Query("SELECT * FROM attachments WHERE inbox_item_id = :inboxItemId AND is_deleted = 0 LIMIT 1")
    suspend fun findAttachmentForInboxItem(inboxItemId: String): AttachmentEntity?

    @Query("UPDATE inbox_items SET status = :status, last_error = :error, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateCaptureState(id: String, status: String, error: String?, updatedAt: String)

    @Query("UPDATE inbox_items SET status = 'saved', last_error = NULL, updated_at = :updatedAt WHERE id = :id")
    suspend fun markCaptureSaved(id: String, updatedAt: String)

    @Query("UPDATE attachments SET byte_size = :byteSize, sha256_hash = :sha256, duration_ms = :durationMs, updated_at = :updatedAt WHERE inbox_item_id = :inboxItemId")
    suspend fun finalizeAttachment(inboxItemId: String, byteSize: Long, sha256: String, durationMs: Long, updatedAt: String)
}

@Database(entities = [DeviceEntity::class, InboxItemEntity::class, AttachmentEntity::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun captureDao(): CaptureDao
}
