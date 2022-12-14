package com.life4.flowpod.features.podcastdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.flowpod.data.MyPreference
import com.life4.flowpod.exoplayer.service.MusicService
import com.life4.flowpod.exoplayer.service.MusicServiceConnection
import com.life4.flowpod.exoplayer.service.currentPlaybackPosition
import com.life4.flowpod.models.room.SavedPodcast
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.other.Constant.UPDATE_PLAYER_POSITION_INTERVAL
import com.life4.flowpod.room.podcast.PodcastDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastDetailsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val podcastDao: PodcastDao,
    private val pref: MyPreference
) :
    BaseViewModel() {

    val podcastEpisode = MutableLiveData<RssPaginationItem>()
    var isDownloaded: Boolean = false
    var isError = MutableLiveData<Boolean>(false)
    var isDownloading = MutableLiveData<Boolean>(false)

    val downloadedPodcasts = MutableLiveData<List<SavedPodcast>>()

    private val playbackState = musicServiceConnection.playbackState
    val timerState = musicServiceConnection.countDownTimer

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    private val _curPlayerPosition = MutableLiveData<Long>()
    val curPlayerPosition: LiveData<Long> = _curPlayerPosition

    private val currentEpisodeDuration: Long
        get() = MusicService.currentDuration

    val currentEpisodeProgress: Float
        get() {
            if (currentEpisodeDuration > 0) {
                return curPlayerPosition.value?.toFloat()?.div(currentEpisodeDuration) ?: 0f
            }
            return 0f
        }

    init {
        updateCurrentPlayerPosition()
    }

    fun isLogin(): Boolean {
        return pref.getUsername() != null
    }

    private fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            while (true) {
                val pos = playbackState.value?.currentPlaybackPosition
                if (curPlayerPosition.value != pos) {
                    _curPlayerPosition.postValue(pos ?: 0L)
                    _curSongDuration.postValue(MusicService.currentDuration)
                }
                delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }

    fun setTimer(timer: String) {
        musicServiceConnection.setTimer(timer)
    }

    fun cancelTimer() {
        musicServiceConnection.cancelTimer()
    }

    fun getDownloadedPodcasts(): Flow<List<SavedPodcast>> {
        return podcastDao.getAllSavedPodcasts()
    }

    fun deleteDownloadedPodcast(item: RssPaginationItem) {
        downloadedPodcasts.value?.firstOrNull { item.title == it.podcastItem?.title }
            ?.let {
                viewModelScope.launch {
                    podcastDao.deleteSavedPodcast(it.id)
                }
            }
        isDownloaded = false
        isError.value = false
    }
}