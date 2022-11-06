package com.life4.feedz.features.flow

import androidx.lifecycle.MutableLiveData
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.room.FlowPodcast
import com.life4.feedz.room.podcast.PodcastDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PodcastFlowViewModel @Inject constructor(private val podcastDao: PodcastDao) :
    BaseViewModel() {

    val flowPodcasts = MutableLiveData<List<FlowPodcast>>()

    fun getAllFlowPodcasts() = podcastDao.getAllFlowPodcasts()
}