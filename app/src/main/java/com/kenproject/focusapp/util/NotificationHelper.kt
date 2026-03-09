package com.kenproject.focusapp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kenproject.focusapp.R
import com.kenproject.focusapp.domain.model.DistractionType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles all notification logic.
 *
 * Throttling strategy: one notification per distraction type per 10 seconds.
 * This prevents spam while still alerting the user promptly.
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "focus_distraction_channel"
        private const val THROTTLE_MS = 10_000L
        private const val NOISE_NOTIFICATION_ID = 1001
        private const val MOVEMENT_NOTIFICATION_ID = 1002
    }

    private val lastNotificationTime = mutableMapOf<DistractionType, Long>()

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notif_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notif_channel_description)
            enableVibration(true)
            setShowBadge(true)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun notifyDistraction(type: DistractionType) {
        val now = System.currentTimeMillis()
        val last = lastNotificationTime[type] ?: 0L
        if (now - last < THROTTLE_MS) return

        lastNotificationTime[type] = now

        val (title, message, notifId) = when (type) {
            DistractionType.NOISE -> Triple(
                context.getString(R.string.notif_noise_title),
                context.getString(R.string.notif_noise_message),
                NOISE_NOTIFICATION_ID
            )

            DistractionType.MOVEMENT -> Triple(
                context.getString(R.string.notif_movement_title),
                context.getString(R.string.notif_movement_message),
                MOVEMENT_NOTIFICATION_ID
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notifId, notification)
        } catch (e: SecurityException) {
            //No needed for now.
        }
    }
}