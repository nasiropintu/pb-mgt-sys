package com.nasirpintu.pblos.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.nasirpintu.pblos.MainActivity
import com.nasirpintu.pblos.data.LocalDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleAllPendingReminders(context)
            return
        }

        val taskId = intent.getStringExtra("extra_task_id") ?: return
        val taskTitle = intent.getStringExtra("extra_task_title") ?: "Task Due"
        val spaceName = intent.getStringExtra("extra_space_name") ?: "P&B MGT Sys"

        ReminderManager.createNotificationChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingTapIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, ReminderManager.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Reminder: $taskTitle")
            .setContentText("Space: $spaceName • Tap to view in P&B MGT Sys")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingTapIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(taskId.hashCode(), notification)
        } catch (_: SecurityException) {
        }
    }

    private fun rescheduleAllPendingReminders(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = LocalDatabase.getInstance(context)
                val tasks = db.captureDao().getAllTasksSync()
                val now = System.currentTimeMillis()
                for (task in tasks) {
                    if (task.status != "completed" && task.isDeleted == 0 && !task.dueAt.isNullOrBlank()) {
                        val dueMillis = ReminderManager.parseIsoToMillis(task.dueAt)
                        if (dueMillis != null && dueMillis > now) {
                            val space = task.spaceId?.let { db.captureDao().findSpaceById(it) }
                            ReminderManager.scheduleTaskReminder(
                                context,
                                task.id,
                                task.title,
                                space?.name,
                                dueMillis,
                            )
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }
    }
}
