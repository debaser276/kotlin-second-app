package ru.netology.secondapp

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class UserNotHereWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    override fun doWork(): Result {
        val lastVisit = applicationContext.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).getLong(
            LAST_TIME_VISIT_SHARED_KEY, System.currentTimeMillis())
        if (System.currentTimeMillis() - lastVisit > SHOW_NOTIFICATION_AFTER_UNVISITED_MS) {
            NotificationHelper.remindNotification(applicationContext)
        }
        return Result.success()
    }
}