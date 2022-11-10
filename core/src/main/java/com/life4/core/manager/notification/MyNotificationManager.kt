package com.life4.core.manager.notification

import android.app.Notification

interface MyNotificationManager {
    fun createNotification(notificationModel: NotificationModel): Notification
}
