package com.life4.flowpod.features.source.podcastSource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.life4.core.core.vm.BaseViewModel
import com.life4.flowpod.models.podcast.PodcastResponse
import com.life4.flowpod.models.podcast.categories.PodcastCategories
import com.life4.flowpod.remote.FeedzRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PodcastSourceViewModel @Inject constructor(private val repository: FeedzRepository) :
    BaseViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    val categoryList = listOf<String>(
        "arts",
        "books",
        "design",
        "fashion",
        "beauty",
        "food",
        "business",
        "careers",
        "investing",
        "management",
        "marketing",
        "comedy",
        "education",
        "language",
        "self_improvement",
        "history",
        "health",
        "stories",
        "manga",
        "automotive",
        "games",
        "hobbies",
        "music",
        "entertainment",
        "science",
        "astronomy",
        "sports",
        "technology",
        "film",
        "cryptocurrency"
    )

    fun getPodcastFeed() {
        repository.getPodcastFeed().handle(requestType = RequestType.ACTION, onComplete = {
            _state.value = State.OnPodcastSuccess(it)
        })
    }

    fun getPodcastCategories() {
        repository.getCategories().handle(requestType = RequestType.ACTION, onComplete = {
            _state.value = State.OnPodcastCategorySuccess(it)
        })
    }

    sealed class State {
        data class OnPodcastSuccess(val source: PodcastResponse) : State()
        data class OnPodcastCategorySuccess(val source: PodcastCategories) : State()
    }
}