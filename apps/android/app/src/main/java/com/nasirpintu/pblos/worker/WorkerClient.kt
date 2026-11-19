package com.nasirpintu.pblos.worker

import android.content.Context
import android.util.Log
import com.nasirpintu.pblos.data.InboxItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * PBLOS Windows Worker HTTP Client
 * Sends audio captures to the local Windows worker for transcription + extraction.
 * All communication is LAN-only (127.x.x.x or local IP). Zero cloud.
 *
 * Crafted with ❤️ by NasirPintu
 */
object WorkerClient {

    private const val TAG = "PBLOS.WorkerClient"
    private const val CONNECT_TIMEOUT_MS = 3_000
    private const val READ_TIMEOUT_MS = 10_000

    data class WorkerConfig(
        val baseUrl: String,       // e.g. "http://192.168.1.5:7474"
        val deviceId: String,
    )

    data class JobSubmitResult(
        val jobId: String,
        val status: String,
        val message: String,
    )

    data class JobStatusResult(
        val jobId: String,
        val inboxItemId: String,
        val status: String,       // queued | running | completed | failed | cancelled
        val transcript: String?,
        val extractionJson: String?,
        val errorCode: String?,
        val errorMessage: String?,
        val modelVersion: String?,
    )

    data class WorkerStatus(
        val ready: Boolean,
        val whisperAvailable: Boolean,
        val ollamaAvailable: Boolean,
        val ollamaModel: String?,
        val whisperModel: String?,
        val queueLength: Int,
        val processedToday: Int,
    )

    // ─── Ping / health ───────────────────────────────────────────────────────

    suspend fun ping(config: WorkerConfig): WorkerStatus? = withContext(Dispatchers.IO) {
        try {
            val url = URL("${config.baseUrl}/api/status")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = CONNECT_TIMEOUT_MS
            conn.readTimeout = READ_TIMEOUT_MS
            conn.requestMethod = "GET"
            if (conn.responseCode != 200) return@withContext null
            val body = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(body)
            WorkerStatus(
                ready              = json.optBoolean("ready"),
                whisperAvailable   = json.optBoolean("whisperAvailable"),
                ollamaAvailable    = json.optBoolean("ollamaAvailable"),
                ollamaModel        = json.optString("ollamaModel").takeIf { it.isNotBlank() },
                whisperModel       = json.optString("whisperModel").takeIf { it.isNotBlank() },
                queueLength        = json.optInt("queueLength"),
                processedToday     = json.optInt("processedToday"),
            )
        } catch (e: Exception) {
            Log.w(TAG, "Ping failed: ${e.message}")
            null
        }
    }

    // ─── Submit job ──────────────────────────────────────────────────────────

    suspend fun submitAudioJob(
        config: WorkerConfig,
        inboxItemId: String,
        audioFile: File,
    ): Result<JobSubmitResult> = withContext(Dispatchers.IO) {
        try {
            val boundary = "PblosAudioBoundary_${System.currentTimeMillis()}"
            val url = URL("${config.baseUrl}/api/jobs")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = CONNECT_TIMEOUT_MS
            conn.readTimeout = 60_000 // uploading audio may take longer
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

            conn.outputStream.use { out ->
                fun part(name: String, value: String) {
                    out.write("--$boundary\r\nContent-Disposition: form-data; name=\"$name\"\r\n\r\n$value\r\n".toByteArray())
                }
                part("inboxItemId", inboxItemId)
                part("deviceId", config.deviceId)
                // Audio file part
                out.write("--$boundary\r\nContent-Disposition: form-data; name=\"audio\"; filename=\"${audioFile.name}\"\r\nContent-Type: audio/mp4\r\n\r\n".toByteArray())
                audioFile.inputStream().use { it.copyTo(out) }
                out.write("\r\n--$boundary--\r\n".toByteArray())
            }

            if (conn.responseCode !in 200..299) {
                val err = conn.errorStream?.bufferedReader()?.readText() ?: "HTTP ${conn.responseCode}"
                return@withContext Result.failure(Exception(err))
            }
            val body = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(body)
            Result.success(
                JobSubmitResult(
                    jobId   = json.getString("jobId"),
                    status  = json.getString("status"),
                    message = json.optString("message"),
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Submit failed for $inboxItemId: ${e.message}")
            Result.failure(e)
        }
    }

    // ─── Poll job status ─────────────────────────────────────────────────────

    suspend fun getJobStatus(
        config: WorkerConfig,
        jobId: String,
    ): Result<JobStatusResult> = withContext(Dispatchers.IO) {
        try {
            val url = URL("${config.baseUrl}/api/jobs/$jobId")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = CONNECT_TIMEOUT_MS
            conn.readTimeout = READ_TIMEOUT_MS
            conn.requestMethod = "GET"
            if (conn.responseCode != 200) {
                return@withContext Result.failure(Exception("HTTP ${conn.responseCode}"))
            }
            val body = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(body)
            val extractionJson = if (json.has("extraction") && !json.isNull("extraction"))
                json.getJSONObject("extraction").toString() else null
            Result.success(
                JobStatusResult(
                    jobId          = json.getString("jobId"),
                    inboxItemId    = json.getString("inboxItemId"),
                    status         = json.getString("status"),
                    transcript     = json.optString("transcript").takeIf { it.isNotBlank() },
                    extractionJson = extractionJson,
                    errorCode      = json.optString("errorCode").takeIf { it.isNotBlank() },
                    errorMessage   = json.optString("errorMessage").takeIf { it.isNotBlank() },
                    modelVersion   = json.optString("modelVersion").takeIf { it.isNotBlank() },
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Poll failed for $jobId: ${e.message}")
            Result.failure(e)
        }
    }

    // ─── Retry failed job ────────────────────────────────────────────────────

    suspend fun retryJob(config: WorkerConfig, jobId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("${config.baseUrl}/api/jobs/$jobId/retry")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = CONNECT_TIMEOUT_MS
            conn.readTimeout = READ_TIMEOUT_MS
            conn.requestMethod = "POST"
            conn.responseCode in 200..299
        } catch (e: Exception) {
            false
        }
    }

    // ─── Preferences ─────────────────────────────────────────────────────────

    fun saveWorkerUrl(context: Context, url: String) {
        context.getSharedPreferences("pblos_worker", Context.MODE_PRIVATE)
            .edit().putString("worker_url", url.trimEnd('/')).apply()
    }

    fun loadWorkerUrl(context: Context): String? {
        return context.getSharedPreferences("pblos_worker", Context.MODE_PRIVATE)
            .getString("worker_url", null)?.trimEnd('/')
    }

    fun saveDeviceId(context: Context, deviceId: String) {
        context.getSharedPreferences("pblos_local_identity", Context.MODE_PRIVATE)
            .edit().putString("device_id", deviceId).apply()
    }

    fun loadDeviceId(context: Context): String? {
        return context.getSharedPreferences("pblos_local_identity", Context.MODE_PRIVATE)
            .getString("device_id", null)
    }
}
