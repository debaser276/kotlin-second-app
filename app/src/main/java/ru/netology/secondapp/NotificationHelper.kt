package ru.netology.secondapp

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.RemoteMessage
import ru.netology.secondapp.dto.MediaType
import java.util.*

object NotificationHelper {

    private const val UPLOAD_CHANNEL_ID = "upload_channel_id"
    private const val MAIN_ACTIVITY_REQUEST = 1
    private const val POST_ACTIVITY_REQUEST = 2
    private const val INTENT_POST_ID = "intent-post-id"
    private var channelCreated = false
    private var lastNotificationId: Int? = null

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Media uploading"
            val descriptionText = "Notifies when media upload during post creation"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(UPLOAD_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun comebackNotification(context: Context) {
        createNotificationChannelIfNotCreated(context)
        val title = "Понравилось ли вам у нас?"
        val content = "Дорогой пользователь, возвращайтесь к нам скорее"
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createBuilder(context, title, content, NotificationManager.IMPORTANCE_HIGH)
        } else {
            createBuilder(context, title, content)
        }
        showNotification(context, builder)
    }

    fun mediaUploaded(type: MediaType, context: Context) {
        createNotificationChannelIfNotCreated(context)
        val title = "Media uploaded"
        val content = "your ${type.name.toLowerCase()} successfully uploaded"
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createBuilder(context, title, content, NotificationManager.IMPORTANCE_HIGH)
        } else {
            createBuilder(context, title, content)
        }
        showNotification(context, builder)
    }

    fun remindNotification(context: Context) {
        createNotificationChannelIfNotCreated(context)
        val title = "Where are you?"
        val content = "Дорогой пользователь, возвращайтесь к нам скорее"
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createBuilder(context, title, content, NotificationManager.IMPORTANCE_HIGH)
        } else {
            createBuilder(context, title, content)
        }.setContentIntent(PendingIntent.getActivity(
            context,
            MAIN_ACTIVITY_REQUEST,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT))
            .setAutoCancel(true)
        showNotification(context, builder)
    }

    fun welcomeNotification(context: Context, message: RemoteMessage) {
        createNotificationChannelIfNotCreated(context)
        val title = message.data["title"] ?: return
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createBuilder(context, title, "", NotificationManager.IMPORTANCE_HIGH)
        } else {
            createBuilder(context, title, "")
        }
        showNotification(context, builder)
    }

    fun likeAddNotification(context: Context, message: RemoteMessage) {
        createNotificationChannelIfNotCreated(context)
        val title = message.data["title"] ?: return
        val content = message.data["content"] ?: return
        val recipientPostId = message.data["recipientPostId"]?.toInt()
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createBuilder(context, title, content, NotificationManager.IMPORTANCE_HIGH)
        } else {
            createBuilder(context, title, content)
        }.setContentIntent(PendingIntent.getActivity(
            context,
            POST_ACTIVITY_REQUEST,
            Intent(context, PostActivity::class.java).apply { putExtra(INTENT_POST_ID, recipientPostId) },
            PendingIntent.FLAG_UPDATE_CURRENT))
            .setAutoCancel(true)
        showNotification(context, builder)
    }

    private fun showNotification(
        context: Context,
        builder: NotificationCompat.Builder
    ) {
        with(NotificationManagerCompat.from(context)) {
            val notificationId = Random().nextInt(100000)
            lastNotificationId = notificationId
            notify(notificationId, builder.build())
        }
    }

    @TargetApi(24)
    private fun createBuilder(
        context: Context,
        title: String,
        content: String,
        priority: Int
    ): NotificationCompat.Builder {
        val builder = createBuilder(context, title, content)
        builder.priority = priority
        return builder
    }

    private fun createBuilder(
        context: Context,
        title: String,
        content: String
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, UPLOAD_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        return builder
    }

    private fun createNotificationChannelIfNotCreated(context: Context) {
        if (!channelCreated) {
            createNotificationChannel(context)
            channelCreated = true
        }
    }
}