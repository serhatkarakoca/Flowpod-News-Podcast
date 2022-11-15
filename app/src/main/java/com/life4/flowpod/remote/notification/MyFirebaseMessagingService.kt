package com.life4.flowpod.remote.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.life4.core.manager.language.MyLanguageManager
import com.life4.flowpod.R
import com.life4.flowpod.features.main.MainActivity
import com.life4.flowpod.other.Constant.APP_NOTIFICATION_CHANNEL_ID
import com.life4.flowpod.other.Constant.APP_NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var languageManager: MyLanguageManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("register Token", token)
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (languageManager.getCurrentLanguage().languageCode == "tr") {
                putExtra("title", message.data.get("titleTR").toString())
                putExtra("link", message.data.get("linkTR").toString())
                putExtra("htmlContent", message.data.get("htmlContentTR").toString())
            } else {
                putExtra("title", message.data.get("titleEn").toString())
                putExtra("link", message.data.get("linkEn").toString())
                putExtra("htmlContent", message.data.get("htmlContentEn").toString())
            }
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val title =
            if (languageManager.getCurrentLanguage().languageCode == "tr")
                message.data.get("titleTR").toString()
            else message.data.get("titleEn").toString()


        val notification = NotificationCompat.Builder(
            applicationContext, APP_NOTIFICATION_ID.toString()
        ).setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(getString(R.string.breaking_news_title))
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_MAX).setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                APP_NOTIFICATION_ID.toString(),
                APP_NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(APP_NOTIFICATION_ID, notification.build())
        }
    }

}