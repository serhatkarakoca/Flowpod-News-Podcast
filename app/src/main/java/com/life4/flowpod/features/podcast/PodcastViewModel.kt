package com.life4.flowpod.features.podcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.life4.core.core.vm.BaseViewModel
import com.life4.flowpod.features.podcast.adapter.PodcastDataSource
import com.life4.flowpod.models.request.RssRequest
import com.life4.flowpod.models.room.FlowPodcast
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.models.rss_.RssResponse
import com.life4.flowpod.remote.ApiService
import com.life4.flowpod.remote.FeedzRepository
import com.life4.flowpod.room.podcast.PodcastDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastViewModel @Inject constructor(
    private val feedzRepository: FeedzRepository,
    private val podcastDao: PodcastDao,
    private val mApi: ApiService
) :
    BaseViewModel() {

    val flowPodcasts = MutableLiveData<List<FlowPodcast>>()
    var isSavedPodcast = false

    private val _podcastDetails = MutableLiveData<RssResponse?>()
    val podcastDetails: LiveData<RssResponse?>
        get() = _podcastDetails

    val mediaItems = MutableLiveData<ArrayList<RssPaginationItem>>(arrayListOf())

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    init {
        _podcastDetails.observeForever(::onPodcastDetails)
    }

    private fun onPodcastDetails(item: RssResponse?) {
        item?.let {
            _state.value = State.OnPodcastDetails(it)
        }
    }

    fun getPodcastDetails(url: String): Flow<PagingData<RssPaginationItem>> {
        return Pager(
            config = PagingConfig(20, enablePlaceholders = false),
            pagingSourceFactory = {
                PodcastDataSource(
                    mApi,
                    page = _podcastDetails,
                    mediaItems = mediaItems,
                    baseLiveData = baseLiveData,
                    body = RssRequest(listOf(url))
                )
            }
        ).flow.cachedIn(viewModelScope)
        //_state.value = State.OnPodcastDetails(it)
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

    override fun onCleared() {
        _podcastDetails.removeObserver(::onPodcastDetails)
        super.onCleared()
    }

    sealed class State {
        data class OnPodcastDetails(val details: RssResponse) : State()
    }
}