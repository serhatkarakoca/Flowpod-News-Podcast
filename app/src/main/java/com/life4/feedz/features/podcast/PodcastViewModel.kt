package com.life4.feedz.features.podcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.room.FlowPodcast
import com.life4.feedz.models.rss_.RssResponse
import com.life4.feedz.remote.FeedzRepository
import com.life4.feedz.room.podcast.PodcastDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastViewModel @Inject constructor(
    private val feedzRepository: FeedzRepository,
    private val podcastDao: PodcastDao
) :
    BaseViewModel() {

    val flowPodcasts = MutableLiveData<List<FlowPodcast>>()
    var isSavedPodcast = false

    private val _podcastDetails = MutableLiveData<RssResponse>()
    val podcastDetails: LiveData<RssResponse>
        get() = _podcastDetails

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state


    fun getPodcastDetails(url: String) {
        feedzRepository.searchSite(url).handle(requestType = RequestType.ACTION, onComplete = {
            _podcastDetails.value = it
            _state.value = State.OnPodcastDetails(it)
        }, onError = {})
    }

    fun getSavedPodcasts() = podcastDao.getAllFlowPodcasts()

    fun addPodcastToFlow(podcast: RssResponse) {
        viewModelScope.launch {
            podcastDao.insertFlowPodcast(FlowPodcast(podcastItem = podcast))
            isSavedPodcast = true
        }
    }

    fun deletePodcastFromFlow() {
        val flowPodcast =
            flowPodcasts.value?.firstOrNull { podcastDetails.value?.feedUrl == it.podcastItem?.feedUrl }
        flowPodcast?.let {
            viewModelScope.launch {
                podcastDao.deleteFlowPodcast(it.id)
                isSavedPodcast = false
            }
        }
    }

    sealed class State {
        data class OnPodcastDetails(val details: RssResponse) : State()
    }
}