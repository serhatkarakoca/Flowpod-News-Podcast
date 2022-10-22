package com.life4.feedz.features.podcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.rss_.RssResponse
import com.life4.feedz.remote.FeedzRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PodcastViewModel @Inject constructor(
    private val feedzRepository: FeedzRepository
) :
    BaseViewModel() {

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
        })
    }

    sealed class State {
        data class OnPodcastDetails(val details: RssResponse) : State()
    }
}