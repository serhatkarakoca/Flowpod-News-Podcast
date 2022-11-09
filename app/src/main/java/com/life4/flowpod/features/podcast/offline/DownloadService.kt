package com.life4.flowpod.features.podcast.offline

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.life4.flowpod.R
import com.life4.flowpod.features.main.MainActivity
import com.life4.flowpod.models.room.SavedPodcast
import com.life4.flowpod.models.rss_.Enclosure
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.other.Constant.DOWNLOAD_CHANNEL_NAME
import com.life4.flowpod.other.Constant.DOWNLOAD_NOTIFICATION_ID
import com.life4.flowpod.room.podcast.PodcastDao
import com.life4.flowpod.utils.deserializeFromJson
import com.life4.flowpod.utils.saveFile
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

@HiltWorker
class DownloadService @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    val podcastDao: PodcastDao
) :
    CoroutineWorker(
        context,
        workerParams
    ) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // This dispatcher is optimized to perform disk or network I/O outside of the main thread.
        val url = inputData.getStringArray("url")
        val podcastItem = inputData.getString("podcastItem")
        val podcast = deserializeFromJson(podcastItem)
        var path: String? = null
        try {
            url?.map {
                async {
                    path = downloadImages(it, System.currentTimeMillis().toString() + ".mp3")
                }
            }?.awaitAll() //  awaitAll to wait for both network requests

            if (path != null) {
                podcast ?: Result.failure()
                addPodcastDownloaded(podcast!!, path!!)
                showNotification()
                Result.success(workDataOf("path" to path))
            } else {
                Result.failure()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun addPodcastDownloaded(item: RssPaginationItem, path: String) {
        val pdItem = item.copy(
            enclosure = Enclosure(
                length = null,
                type = null,
                url = path
            )
        )
        podcastDao.insertSavedPodcast(podcast = SavedPodcast(podcastItem = pdItem.copy(isDownloaded = true)))
    }

    private fun downloadImages(url: String, fileName: String): String? {
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

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return path
    }


    private fun showNotification() {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }


        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        val notification = NotificationCompat.Builder(
            applicationContext, DOWNLOAD_NOTIFICATION_ID.toString()
        ).setSmallIcon(R.drawable.flowpod_logo)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getText(R.string.episode_downloaded))
            .setPriority(NotificationCompat.PRIORITY_MAX).setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                DOWNLOAD_NOTIFICATION_ID.toString(),
                DOWNLOAD_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(DOWNLOAD_NOTIFICATION_ID, notification.build())
        }

    }
}