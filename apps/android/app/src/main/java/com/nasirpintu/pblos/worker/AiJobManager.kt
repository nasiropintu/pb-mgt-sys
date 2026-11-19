package com.nasirpintu.pblos.worker

import android.content.Context
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.nasirpintu.pblos.data.LocalDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * AiJobManager — Android-side durable AI job tracking.
 * Records which inbox items have been submitted to the Windows worker,
 * stores poll results, and provides Flow-based observation for the UI.
 *
 * Crafted with ❤️ by NasirPintu
 */

// ─── Room Entity ──────────────────────────────────────────────────────────────

@Entity(tableName = "ai_jobs")
data class AiJobEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "inbox_item_id") val inboxItemId: String,
    @ColumnInfo(name = "worker_job_id") val workerJobId: String?,   // jobId from the Windows worker
    @ColumnInfo(name = "status") val status: String,                 // queued|running|completed|failed|cancelled
    @ColumnInfo(name = "transcript") val transcript: String?,
    @ColumnInfo(name = "extraction_json") val extractionJson: String?,
    @ColumnInfo(name = "error_message") val errorMessage: String?,
    @ColumnInfo(name = "model_version") val modelVersion: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
)

// ─── DAO ──────────────────────────────────────────────────────────────────────

@Dao
interface AiJobDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(job: AiJobEntity)

    @Query("SELECT * FROM ai_jobs WHERE inbox_item_id = :inboxItemId LIMIT 1")
    fun findByInboxItemId(inboxItemId: String): AiJobEntity?

    @Query("SELECT * FROM ai_jobs WHERE id = :id LIMIT 1")
    fun findById(id: String): AiJobEntity?

    @Query("SELECT * FROM ai_jobs ORDER BY created_at DESC LIMIT 100")
    fun observeAll(): Flow<List<AiJobEntity>>

    @Query("SELECT * FROM ai_jobs WHERE status IN ('queued','running') ORDER BY created_at ASC")
    fun getPendingJobs(): List<AiJobEntity>

    @Query("UPDATE ai_jobs SET status=:status, worker_job_id=:workerJobId, updated_at=:now WHERE id=:id")
    fun updateSubmitted(id: String, workerJobId: String, status: String, now: String)

    @Query("UPDATE ai_jobs SET status=:status, transcript=:transcript, extraction_json=:extractionJson, model_version=:modelVersion, updated_at=:now WHERE id=:id")
    fun updateCompleted(id: String, status: String, transcript: String?, extractionJson: String?, modelVersion: String?, now: String)

    @Query("UPDATE ai_jobs SET status=:status, error_message=:errorMessage, updated_at=:now WHERE id=:id")
    fun updateFailed(id: String, status: String, errorMessage: String?, now: String)

    @Query("SELECT COUNT(*) FROM ai_jobs WHERE status IN ('queued','running')")
    fun getPendingCount(): Int
}

// ─── Manager ──────────────────────────────────────────────────────────────────

class AiJobManager private constructor(private val context: Context) {

    private val TAG = "PBLOS.AiJobManager"

    private fun dao(): AiJobDao = LocalDatabase.getInstance(context).aiJobDao()

    private fun isoNow() = java.text.SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US
    ).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }.format(java.util.Date())

    fun observeJobs(): Flow<List<AiJobEntity>> = dao().observeAll()

    suspend fun enqueueJob(inboxItemId: String): AiJobEntity = withContext(Dispatchers.IO) {
        val existing = dao().findByInboxItemId(inboxItemId)
        if (existing != null && existing.status !in listOf("failed", "cancelled")) return@withContext existing

        val now = isoNow()
        val job = AiJobEntity(
            id              = UUID.randomUUID().toString(),
            inboxItemId     = inboxItemId,
            workerJobId     = null,
            status          = "queued",
            transcript      = null,
            extractionJson  = null,
            errorMessage    = null,
            modelVersion    = null,
            createdAt       = now,
            updatedAt       = now,
        )
        dao().upsert(job)
        job
    }

    suspend fun submitToWorker(
        job: AiJobEntity,
        config: WorkerClient.WorkerConfig,
        audioFile: java.io.File,
    ): Boolean = withContext(Dispatchers.IO) {
        val result = WorkerClient.submitAudioJob(config, job.inboxItemId, audioFile)
        result.onSuccess { submit ->
            dao().updateSubmitted(job.id, submit.jobId, "queued", isoNow())
            Log.i(TAG, "Job submitted: ${submit.jobId} for inbox ${job.inboxItemId}")
        }.onFailure { err ->
            dao().updateFailed(job.id, "failed", err.message, isoNow())
            Log.e(TAG, "Submit failed for ${job.inboxItemId}: ${err.message}")
        }
        result.isSuccess
    }

    suspend fun pollJob(job: AiJobEntity, config: WorkerClient.WorkerConfig): Boolean = withContext(Dispatchers.IO) {
        val workerJobId = job.workerJobId ?: return@withContext false
        val result = WorkerClient.getJobStatus(config, workerJobId)
        result.onSuccess { status ->
            when (status.status) {
                "completed" -> dao().updateCompleted(job.id, "completed", status.transcript, status.extractionJson, status.modelVersion, isoNow())
                "failed"    -> dao().updateFailed(job.id, "failed", status.errorMessage ?: status.errorCode, isoNow())
                "cancelled" -> dao().updateFailed(job.id, "cancelled", "Cancelled on worker", isoNow())
                else -> {} // still queued/running — no update
            }
        }.onFailure {
            Log.w(TAG, "Poll failed for $workerJobId: ${it.message}")
        }
        result.isSuccess
    }

    suspend fun pollAllPending(config: WorkerClient.WorkerConfig) = withContext(Dispatchers.IO) {
        val pending = dao().getPendingJobs().filter { it.workerJobId != null }
        for (job in pending) pollJob(job, config)
    }

    fun getJobForInboxItem(inboxItemId: String): AiJobEntity? = dao().findByInboxItemId(inboxItemId)

    data class ParsedExtractionItem(
        val kind: String,
        val title: String,
        val body: String?,
        val spaceHint: String?,
        val dateExpression: String?,
        val confidence: String,
    )

    companion object {
        fun parseExtractionJson(jsonStr: String?): List<ParsedExtractionItem> {
            if (jsonStr.isNullOrBlank()) return emptyList()
            return try {
                val root = org.json.JSONObject(jsonStr)
                val itemsArr = root.optJSONArray("items") ?: return emptyList()
                val result = mutableListOf<ParsedExtractionItem>()
                for (i in 0 until itemsArr.length()) {
                    val obj = itemsArr.getJSONObject(i)
                    val title = obj.optString("title", "").trim()
                    if (title.isNotEmpty()) {
                        result.add(
                            ParsedExtractionItem(
                                kind = obj.optString("kind", "note"),
                                title = title,
                                body = obj.optString("body").takeIf { it.isNotBlank() && it != "null" },
                                spaceHint = obj.optString("spaceHint").takeIf { it.isNotBlank() && it != "null" },
                                dateExpression = obj.optString("dateExpression").takeIf { it.isNotBlank() && it != "null" },
                                confidence = obj.optString("confidence", "medium"),
                            )
                        )
                    }
                }
                result
            } catch (e: Exception) {
                emptyList()
            }
        }

        @Volatile private var instance: AiJobManager? = null
        fun get(context: Context) = instance ?: synchronized(this) {
            instance ?: AiJobManager(context.applicationContext).also { instance = it }
        }
    }
}
