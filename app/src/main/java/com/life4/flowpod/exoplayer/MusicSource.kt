package com.life4.flowpod.exoplayer

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.life4.flowpod.exoplayer.State.*
import com.life4.flowpod.models.rss_.RssPaginationItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MusicSource @Inject constructor(
    @ApplicationContext val context: Context
) {
    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    var songs = emptyList<MediaMetadataCompat>()

    private val isReady: Boolean
        get() = state == STATE_INITIALIZED

    fun fetchMediaData(list: List<RssPaginationItem?>) {
        state = STATE_INITIALIZING
        list.let {
            songs = it.mapNotNull { song ->
                MediaMetadataCompat.Builder()
                    .putString(METADATA_KEY_ARTIST, song?.itunes?.author)
                    .putString(METADATA_KEY_MEDIA_ID, song?.enclosure?.url)
                    .putString(METADATA_KEY_TITLE, song?.title)
                    .putString(METADATA_KEY_DISPLAY_TITLE, song?.title)
                    .putString(METADATA_KEY_DISPLAY_ICON_URI, song?.itunes?.image)
                    .putString(METADATA_KEY_MEDIA_URI, song?.enclosure?.url)
                    .putString(METADATA_KEY_ALBUM_ART_URI, song?.enclosure?.url)
                    .putString(METADATA_KEY_DISPLAY_SUBTITLE, song?.itunes?.author)
                    .putString(METADATA_KEY_DISPLAY_DESCRIPTION, song?.contentSnippet)
                    .build()
            }
            state = STATE_INITIALIZED
        }
    }

    fun asMediaSource(dataSourceFactory: CacheDataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { mediaMetadataCompat ->
            var fileUri: Uri? = null
            val item = mediaMetadataCompat.getString(METADATA_KEY_MEDIA_URI)
            if (item.contains(context.packageName)) {
                val files = context.filesDir.listFiles()
                files?.filter { it.canRead() && it.isFile && it.name.endsWith(".mp3") }
                    ?.firstOrNull { it.absolutePath.contains(item.substringAfter("media/")) }?.let {
                        fileUri = it.toUri()
                    }
            }

            val mediaItem = MediaItem.fromUri(fileUri ?: item.toUri())

            val mediaSource = ProgressiveMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(mediaItem)

            concatenatingMediaSource.addMediaSource(mediaSource)


        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.description.mediaUri)
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
    }.toMutableList()

    fun refresh() {
        onReadyListeners.clear()
        state = STATE_CREATED
    }

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(isReady)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        if (state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListeners += action
            return false
        } else {
            action(isReady)
            return true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}