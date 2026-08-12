package com.ritm.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ritm.app.R
import com.ritm.core.model.DayFinalizeSummary

/** Канал и текст пуш-уведомления с итогами дня по ограничениям. */
object RitmNotifications {

    private const val CHANNEL_ID = "ritm_daily_summary"
    private const val NOTIFICATION_ID = 1001

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Итоги дня",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Автоматический итог по ограничениям в конце дня"
        }
        manager.createNotificationChannel(channel)
    }

    fun showDailySummary(context: Context, summary: DayFinalizeSummary) {
        if (summary.isEmpty) return
        ensureChannel(context)

        val title = if (summary.exceededCount == 0) "День закрыт: все ограничения соблюдены" else "День закрыт"
        val text = buildString {
            append("Соблюдено: ${summary.keptCount}")
            if (summary.exceededCount > 0) {
                append(", превышено: ${summary.exceededCount} (${summary.exceededNames.joinToString(", ")})")
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        }
    }
}
