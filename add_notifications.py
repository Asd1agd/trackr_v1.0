import os

manifest_path = 'app/src/main/AndroidManifest.xml'
with open(manifest_path, 'r') as f:
    manifest_content = f.read()

# Add permissions
perms = """    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.USE_EXACT_ALARM" />
"""

if "POST_NOTIFICATIONS" not in manifest_content:
    manifest_content = manifest_content.replace('<application', perms + '\n    <application')

with open(manifest_path, 'w') as f:
    f.write(manifest_content)

# Setup Worker
worker_code = """package com.example.financetracker.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.financetracker.R

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Finance Alert"
        val message = inputData.getString("message") ?: "Check your budget!"
        showNotification(title, message)
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "finance_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Finance Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
"""

os.makedirs('app/src/main/java/com/example/financetracker/worker', exist_ok=True)
with open('app/src/main/java/com/example/financetracker/worker/ReminderWorker.kt', 'w') as f:
    f.write(worker_code)

