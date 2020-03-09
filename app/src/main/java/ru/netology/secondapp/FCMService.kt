package ru.netology.secondapp

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        when (message.data["type"]) {
            "welcome" -> NotificationHelper.welcomeNotification(applicationContext, message)
            "likeAdd" -> NotificationHelper.likeAddNotification(applicationContext, message)
        }
    }
}