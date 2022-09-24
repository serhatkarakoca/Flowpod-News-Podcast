package com.life4.core.manager.notification

import android.app.PendingIntent

data class NotificationCancelModel(
    val cancelIconRes: Int = 0,
    val cancel: String,
    val cancelIntent: PendingIntent,
)
