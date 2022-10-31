package com.life4.feedz.features.podcastdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.exoplayer.service.MusicService
import com.life4.feedz.exoplayer.service.MusicServiceConnection
import com.life4.feedz.exoplayer.service.currentPlaybackPosition
import com.life4.feedz.other.Constant.UPDATE_PLAYER_POSITION_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastDetailsViewModel @Inject constructor(private val musicServiceConnection: MusicServiceConnection) :
    BaseViewModel() {
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
}