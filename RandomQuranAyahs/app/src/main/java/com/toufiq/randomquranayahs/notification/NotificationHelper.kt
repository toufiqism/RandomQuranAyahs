package com.toufiq.randomquranayahs.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.toufiq.randomquranayahs.MainActivity
import com.toufiq.randomquranayahs.R
import com.toufiq.randomquranayahs.data.model.QuranData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "quran_ayahs_channel"
        const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Quran Ayahs"
            val descriptionText = "Notifications for random Quran ayahs"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showNotification(quranData: QuranData) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Surah ${quranData.surah.englishName} - Ayah ${quranData.numberInSurah}")
            .setContentText(quranData.text)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${quranData.text}\n\nTranslation: ${quranData.edition.englishName}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                try {
                    notify(NOTIFICATION_ID, notification)
                } catch (e: SecurityException) {
                    // Handle permission not granted
                }
            } else {
                notify(NOTIFICATION_ID, notification)
            }
        }
    }
} 