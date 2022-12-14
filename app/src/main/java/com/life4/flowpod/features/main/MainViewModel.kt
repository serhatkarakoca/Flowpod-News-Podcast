package com.life4.flowpod.features.main

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.core.models.Resource
import com.life4.flowpod.exoplayer.service.MusicServiceConnection
import com.life4.flowpod.exoplayer.service.isPlayEnabled
import com.life4.flowpod.exoplayer.service.isPlaying
import com.life4.flowpod.exoplayer.service.isPrepared
import com.life4.flowpod.models.request.RssRequest
import com.life4.flowpod.models.room.NewsRemoteKey
import com.life4.flowpod.models.rss_.RssPagination
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.models.source.RssFeedResponse
import com.life4.flowpod.other.Constant
import com.life4.flowpod.remote.FeedzRepository
import com.life4.flowpod.remote.source.SourceRepository
import com.life4.flowpod.room.news.NewsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val newsDao: NewsDao,
    private val sourceRepository: SourceRepository,
    private val feedzRepository: FeedzRepository,
    private val musicServiceConnection: MusicServiceConnection
) : BaseViewModel() {

    var cachedData = true

    private val _mediaItems = MutableLiveData<Resource<List<RssPaginationItem>>>()
    val mediaItems: LiveData<Resource<List<RssPaginationItem>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState
    val countDownTimer = musicServiceConnection.countDownTimer

    val bottomPlayBackVisibility = MutableLiveData<Boolean>(true)

    private val _siteData = MutableLiveData<RssFeedResponse>()
    val siteData: LiveData<RssFeedResponse>
        get() = _siteData

    private val _siteDataList = MutableLiveData<RssPagination?>()
    val siteDataList: LiveData<RssPagination?>
        get() = _siteDataList

    fun setMediaItems(items: List<RssPaginationItem>) {
        _mediaItems.postValue(Resource.success(items))
    }

    fun getCachedData() {

        viewModelScope.launch {
            if (newsDao.getAllREmoteKey(Constant.HOME_NEWS.toString()) != null)
                cachedData = false
            else {
                getSources {
                    viewModelScope.launch {
                        getHomeNews {
                            cachedData = false
                        }
                    }

                }
            }
        }
    }

    private fun getHomeNews(onComplete: () -> Unit) {
        feedzRepository.getSiteData(RssRequest(siteData.value?.sourceList?.filter { it.categoryId != Constant.SPORT_NEWS }
            ?.mapNotNull { it.siteUrl } ?: listOf()))
            .handle(requestType = RequestType.CUSTOM, onComplete = {
                viewModelScope.launch {
                    val keys = it.mapNotNull {
                        NewsRemoteKey(
                            Constant.HOME_NEWS.toString(),
                            null,
                            2
                        )
                    }

                    it.forEach { it.pKey = Constant.HOME_NEWS.toString() }
                    newsDao.insertNews(it)
                    newsDao.insertAllRemoteKeys(keys)
                    onComplete.invoke()
                }
            }, onError = {
                onComplete.invoke()
            })
    }

    fun getSources(onComplete: () -> Unit) {
        sourceRepository.getHomePage().handle(requestType = RequestType.INIT, onComplete = {
            _siteData.value = it
            onComplete.invoke()
        }, onError = {
            onComplete.invoke()
        })
    }


    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPreviousSong() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(pos: Long) {
        musicServiceConnection.transportControls.seekTo(pos)
    }

    fun fastForward() {
        musicServiceConnection.fastForward()
    }

    fun rewind() {
        musicServiceConnection.rewind()
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(
            Constant.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }

    fun playOrToggleSong(mediaItem: RssPaginationItem, toggle: Boolean = false) {
        musicServiceConnection.playPodcast(mediaItems.value?.data ?: listOf())
        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.enclosure?.url ==
            curPlayingSong.value?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)
        ) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if (toggle) musicServiceConnection.transportControls.pause()
                    playbackState.isPrepared -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.enclosure?.url, null)
        }
    }

    fun togglePlaybackState() {
        when {
            playbackState.value?.isPlaying == true -> {
                musicServiceConnection.transportControls.pause()
            }

            playbackState.value?.isPlayEnabled == true -> {
                musicServiceConnection.transportControls.play()
            }
        }
    }
}
