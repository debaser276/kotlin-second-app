package ru.netology.secondapp

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class UserNotHereWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    override fun doWork(): Result {

        return Result.success()
    }
}