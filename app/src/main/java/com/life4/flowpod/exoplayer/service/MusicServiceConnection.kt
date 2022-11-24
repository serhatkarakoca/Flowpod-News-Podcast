package com.life4.flowpod.exoplayer.service

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.life4.core.models.Resource
import com.life4.flowpod.exoplayer.MusicSource
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.other.Constant
import com.life4.flowpod.other.Constant.NETWORK_ERROR
import com.life4.flowpod.other.Event
import java.util.concurrent.TimeUnit

class MusicServiceConnection(
    context: Context,
    private val mediaSource: MusicSource,
) {
    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _curPlayingSong = MutableLiveData<MediaMetadataCompat?>()
    val curPlayingSong: LiveData<MediaMetadataCompat?> = _curPlayingSong

    lateinit var mediaController: MediaControllerCompat

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private var countdown: CountDownTimer? = null

    private val _countDownTimer = MutableLiveData<String?>()
    val countDownTimer: LiveData<String?> = _countDownTimer

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply { connect() }

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    fun playPodcast(episodes: List<RssPaginationItem?>) {
        mediaSource.fetchMediaData(episodes)
        mediaBrowser.sendCustomAction(Constant.START_MEDIA_PLAYBACK_ACTION, null, null)
    }

    fun fastForward(seconds: Int = 10) {
        playbackState.value?.currentPlaybackPosition?.let { currentPosition ->
            transportControls.seekTo(currentPosition + seconds * 1000)
        }
    }

    fun rewind(seconds: Int = 10) {
        playbackState.value?.currentPlaybackPosition?.let { currentPosition ->
            transportControls.seekTo(currentPosition - seconds * 1000)
        }
    }

    fun refreshMediaBrowserChildren() {
        mediaBrowser.sendCustomAction(Constant.REFRESH_MEDIA_BROWSER_CHILDREN, null, null)
    }

    fun setTimer(time: String) {
        countdown?.cancel()
        val minute = time.substringAfter(":").toInt()
        val hours = time.substringBefore(":").toInt()
        val totalSeconds = ((minute * 60) + (hours * 3600)) * 1000
        countdown = object : CountDownTimer(totalSeconds.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var hms = String.format(
                    "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(
                        1
                    ),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(
                        1
                    )
                )
                if (hms.startsWith("00:"))
                    hms = hms.substringAfter("00:")
                _countDownTimer.value = hms
            }

            override fun onFinish() {
                _countDownTimer.value = "0"
                transportControls.pause()
                cancel()
            }

        }.start()

    }

    fun cancelTimer() {
        countdown?.cancel()
        _countDownTimer.value = null
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            Log.d("MusicServiceConnection", "CONNECTED")
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaContollerCallback())
            }
            _isConnected.postValue(Event(Resource.success(true)))
        }

        override fun onConnectionSuspended() {
            Log.d("MusicServiceConnection", "SUSPENDED")

            _isConnected.postValue(
                Event(
                    Resource.error(
                        Throwable("The connection was suspended"), false
                    )
                )
            )
        }

        override fun onConnectionFailed() {
            Log.d("MusicServiceConnection", "FAILED")

            _isConnected.postValue(
                Event(
                    Resource.error(
                        Throwable("Couldn't connect to media browser"), false
                    )
                )
            )
        }
    }

    private inner class MediaContollerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            _playbackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            _curPlayingSong.value = metadata?.let {
                mediaSource.songs.find {
                    it.description.mediaId == metadata.description?.mediaId
                }
            }
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when (event) {
                NETWORK_ERROR -> _networkError.postValue(
                    Event(
                        Resource.error(
                            Throwable("Couldn't connect to the server. Please check your internet connection."),
                            null
                        )
                    )
                )
            }
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}
