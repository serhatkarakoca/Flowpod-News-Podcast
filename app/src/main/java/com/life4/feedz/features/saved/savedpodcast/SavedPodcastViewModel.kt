package com.life4.feedz.features.saved.savedpodcast

import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.room.SavedPodcast
import com.life4.feedz.room.podcast.PodcastDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SavedPodcastViewModel @Inject constructor(private val podcastDao: PodcastDao) :
    BaseViewModel() {

    fun getAllSavedPodcasts(): Flow<List<SavedPodcast>> {
        return podcastDao.getAllSavedPodcasts()
    }
}