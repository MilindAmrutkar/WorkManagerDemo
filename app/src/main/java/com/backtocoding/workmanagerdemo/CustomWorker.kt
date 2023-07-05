package com.backtocoding.workmanagerdemo

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.net.UnknownHostException

@HiltWorker
class CustomWorker @AssistedInject constructor(
    @Assisted private val api: DemoApi,
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    companion object {
        private const val TAG = "CustomWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val response = api.getPost()
            if (response.isSuccessful) {
                Log.d(TAG, "Success!")
                Log.d(TAG, "Id: ${response.body()?.id} Title: ${response.body()?.title}")
                Result.success()
            } else {
                Log.d(TAG, "Retrying...")
                Result.retry()
            }
        } catch (e: Exception) {
            if (e is UnknownHostException) {
                Log.d(TAG, "Retrying...")
                Result.retry()
            } else {
                Log.d(TAG, "Error!")
                Result.failure(Data.Builder().putString("error", e.toString()).build())
            }
        }
    }
}