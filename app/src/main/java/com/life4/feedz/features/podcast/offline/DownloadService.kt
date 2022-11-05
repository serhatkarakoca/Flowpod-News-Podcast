package com.life4.feedz.features.podcast.offline

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.life4.feedz.utils.saveFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


class DownloadService(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(
        context,
        workerParams
    ) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // This dispatcher is optimized to perform disk or network I/O outside of the main thread.
        val url = inputData.getStringArray("url")
        val fileName = inputData.getStringArray("fileName")
        var path: String? = null
        try {
            url?.map {
                async {
                    path = downloadImages(it, System.currentTimeMillis().toString() + ".mp3")
                }
            }?.awaitAll() //  awaitAll to wait for both network requests

            if (path != null)
                Result.success(workDataOf("path" to path))
            else
                Result.failure()

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }


    private fun downloadImages(url: String, fileName: String): String? {
        Log.d("WORK_MANAGER_SERVICE", "Downloading: " + url)
        var path: String? = null
        val client = OkHttpClient.Builder()
            .build()
        val request = Request.Builder()
            .url(url)
            .build()
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val inputStream = response.body?.byteStream() ?: return null
                path = saveFile(inputStream, fileName, context)
                Log.d("WORK_MANAGER_SERVICE", "PATH: " + path)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return path
    }
}