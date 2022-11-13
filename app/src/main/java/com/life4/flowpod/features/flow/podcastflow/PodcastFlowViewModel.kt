package com.life4.flowpod.features.flow.podcastflow

import androidx.lifecycle.MutableLiveData
import com.life4.core.core.vm.BaseViewModel
import com.life4.flowpod.models.room.FlowPodcast
import com.life4.flowpod.room.podcast.PodcastDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PodcastFlowViewModel @Inject constructor(private val podcastDao: PodcastDao) :
    BaseViewModel() {

    val flowPodcasts = MutableLiveData<List<FlowPodcast>>()

    fun getAllFlowPodcasts() = podcastDao.getAllFlowPodcasts()
}