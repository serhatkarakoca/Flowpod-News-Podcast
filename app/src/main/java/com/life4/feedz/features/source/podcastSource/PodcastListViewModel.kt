package com.life4.feedz.features.source.podcastSource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.podcast.PodcastResponse
import com.life4.feedz.remote.FeedzRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PodcastListViewModel @Inject constructor(private val repository: FeedzRepository) :
    BaseViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    fun getPodcastFeed(category: String) {
        repository.getPodcastFeedByCategory(category)
            .handle(requestType = RequestType.ACTION, onComplete = {
                _state.value = State.OnPodcastSuccess(it)
            })
    }

    sealed class State {
        data class OnPodcastSuccess(val source: PodcastResponse) : State()

    }
}