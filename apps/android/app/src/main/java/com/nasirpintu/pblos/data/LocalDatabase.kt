package com.nasirpintu.pblos.data

import androidx.room.ColumnInfo
import androidx.room.Dao
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
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.nasirpintu.pblos.worker.AiJobDao
import com.nasirpintu.pblos.worker.AiJobEntity

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

@Entity(tableName = "spaces")
data class SpaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val slug: String,
    val description: String?,
    @ColumnInfo(name = "color_hex") val colorHex: String?,
    @ColumnInfo(name = "sort_order", defaultValue = "0") val sortOrder: Int = 0,
    @ColumnInfo(name = "is_archived", defaultValue = "0") val isArchived: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
    @ColumnInfo(name = "version", defaultValue = "1") val version: Int = 1,
)

@Entity(
    tableName = "projects",
    foreignKeys = [ForeignKey(entity = SpaceEntity::class, parentColumns = ["id"], childColumns = ["space_id"])],
    indices = [Index(value = ["space_id"], name = "idx_projects_space")],
)
data class ProjectEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "space_id") val spaceId: String,
    val name: String,
    val objective: String?,
    val status: String,
    @ColumnInfo(name = "next_action") val nextAction: String?,
    @ColumnInfo(name = "source_inbox_item_id") val sourceInboxItemId: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
    @ColumnInfo(name = "is_deleted", defaultValue = "0") val isDeleted: Int = 0,
    @ColumnInfo(name = "version", defaultValue = "1") val version: Int = 1,
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

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["status", "due_at"], name = "idx_tasks_status_due"),
        Index(value = ["space_id"], name = "idx_tasks_space"),
    ],
)
data class TaskEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "space_id") val spaceId: String?,
    @ColumnInfo(name = "project_id") val projectId: String?,
    @ColumnInfo(name = "source_inbox_item_id") val sourceInboxItemId: String?,
    val title: String,
    val description: String?,
    val status: String = "approved", // 'approved', 'in_progress', 'completed', 'archived'
    val priority: String = "normal", // 'low', 'normal', 'high', 'urgent'
    @ColumnInfo(name = "due_at") val dueAt: String?,
    @ColumnInfo(name = "date_expression") val dateExpression: String?,
    @ColumnInfo(name = "estimated_minutes") val estimatedMinutes: Int?,
    @ColumnInfo(name = "is_focus_today", defaultValue = "0") val isFocusToday: Int = 0,
    @ColumnInfo(name = "completed_at") val completedAt: String?,
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

data class TaskWithSpace(
    @Embedded val task: TaskEntity,
    @Relation(parentColumn = "space_id", entityColumn = "id")
    val space: SpaceEntity?,
)

@Dao
interface CaptureDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDeviceIfAbsent(device: DeviceEntity)

    @Query("UPDATE devices SET last_seen_at = :lastSeenAt, is_active = 1 WHERE id = :id")
    suspend fun updateDeviceLastSeen(id: String, lastSeenAt: String)

    // Inbox & Captures
    @Insert
    suspend fun insertInboxItem(item: InboxItemEntity)

    @Insert
    suspend fun insertAttachment(attachment: AttachmentEntity)

    @Transaction
    @Query("SELECT * FROM inbox_items WHERE is_deleted = 0 ORDER BY captured_at DESC")
    fun observeInbox(): Flow<List<InboxWithAttachments>>

    @Query("SELECT * FROM inbox_items WHERE is_deleted = 0 ORDER BY captured_at DESC LIMIT 1")
    fun observeLatestCapture(): Flow<InboxItemEntity?>

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

    // Spaces
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSpaceIfAbsent(space: SpaceEntity)

    @Query("SELECT * FROM spaces WHERE is_archived = 0 ORDER BY sort_order ASC, name ASC")
    fun observeSpaces(): Flow<List<SpaceEntity>>

    @Query("SELECT * FROM spaces WHERE id = :id LIMIT 1")
    suspend fun findSpaceById(id: String): SpaceEntity?

    // Tasks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun findTaskById(id: String): TaskEntity?

    @Query("UPDATE tasks SET is_deleted = 1, updated_at = :updatedAt WHERE id = :id")
    suspend fun softDeleteTask(id: String, updatedAt: String)

    @Transaction
    @Query("SELECT * FROM tasks WHERE is_deleted = 0 ORDER BY is_focus_today DESC, CASE priority WHEN 'urgent' THEN 1 WHEN 'high' THEN 2 WHEN 'normal' THEN 3 ELSE 4 END ASC, created_at DESC")
    fun observeTasks(): Flow<List<TaskWithSpace>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE is_deleted = 0 AND status != 'completed' AND is_focus_today = 1 ORDER BY CASE priority WHEN 'urgent' THEN 1 WHEN 'high' THEN 2 WHEN 'normal' THEN 3 ELSE 4 END ASC LIMIT 3")
    fun observeFocusTasks(): Flow<List<TaskWithSpace>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE is_deleted = 0 AND status != 'completed' ORDER BY created_at DESC")
    fun observePendingTasks(): Flow<List<TaskWithSpace>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE is_deleted = 0 AND space_id = :spaceId ORDER BY created_at DESC")
    fun observeTasksBySpace(spaceId: String): Flow<List<TaskWithSpace>>

    @Query("UPDATE tasks SET status = :status, completed_at = :completedAt, updated_at = :updatedAt WHERE id = :id")
    suspend fun setTaskCompletion(id: String, status: String, completedAt: String?, updatedAt: String)

    @Query("UPDATE tasks SET is_focus_today = :isFocus, updated_at = :updatedAt WHERE id = :id")
    suspend fun setTaskFocus(id: String, isFocus: Int, updatedAt: String)

    @Query("SELECT COUNT(*) FROM tasks WHERE is_deleted = 0 AND status != 'completed' AND is_focus_today = 1")
    suspend fun getFocusTaskCount(): Int

    // Search
    @Transaction
    @Query("SELECT * FROM tasks WHERE is_deleted = 0 AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') ORDER BY updated_at DESC")
    fun searchTasks(query: String): Flow<List<TaskWithSpace>>

    @Transaction
    @Query("SELECT * FROM inbox_items WHERE is_deleted = 0 AND raw_content LIKE '%' || :query || '%' ORDER BY captured_at DESC")
    fun searchInbox(query: String): Flow<List<InboxWithAttachments>>

    // Synchronous queries for backup & restore
    @Query("SELECT * FROM devices")
    fun getAllDevicesSync(): List<DeviceEntity>

    @Query("SELECT * FROM spaces")
    fun getAllSpacesSync(): List<SpaceEntity>

    @Query("SELECT * FROM tasks")
    fun getAllTasksSync(): List<TaskEntity>

    @Query("SELECT * FROM inbox_items")
    fun getAllInboxSync(): List<InboxItemEntity>

    @Query("SELECT * FROM attachments")
    fun getAllAttachmentsSync(): List<AttachmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDevicesSync(devices: List<DeviceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSpacesSync(spaces: List<SpaceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTasksSync(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInboxSync(items: List<InboxItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAttachmentsSync(attachments: List<AttachmentEntity>)

    @Query("DELETE FROM devices")
    fun clearAllDevices()

    @Query("DELETE FROM spaces")
    fun clearAllSpaces()

    @Query("DELETE FROM tasks")
    fun clearAllTasks()

    @Query("DELETE FROM inbox_items")
    fun clearAllInbox()

    @Query("DELETE FROM attachments")
    fun clearAllAttachments()
}

@Database(
    entities = [
        DeviceEntity::class,
        InboxItemEntity::class,
        AttachmentEntity::class,
        SpaceEntity::class,
        ProjectEntity::class,
        TaskEntity::class,
        AiJobEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun captureDao(): CaptureDao
    abstract fun aiJobDao(): AiJobDao

    companion object {
        private const val DATABASE_NAME = "pblos-local.db"
        @Volatile private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

