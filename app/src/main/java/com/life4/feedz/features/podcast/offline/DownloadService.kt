package com.life4.feedz.features.podcast.offline

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.life4.feedz.R
import com.life4.feedz.models.room.SavedPodcast
import com.life4.feedz.models.rss_.Enclosure
import com.life4.feedz.models.rss_.RssPaginationItem
import com.life4.feedz.room.podcast.PodcastDao
import com.life4.feedz.utils.deserializeFromJson
import com.life4.feedz.utils.saveFile
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

                Toast.makeText(
                    context,
                    context.getString(R.string.episode_downloaded),
                    Toast.LENGTH_SHORT
                ).show()
                Result.success(workDataOf("path" to path))
            } else
                Result.failure()

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