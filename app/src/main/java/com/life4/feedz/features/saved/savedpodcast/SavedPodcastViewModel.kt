package com.life4.feedz.features.saved.savedpodcast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.room.SavedPodcast
import com.life4.feedz.models.rss_.RssPaginationItem
import com.life4.feedz.room.podcast.PodcastDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPodcastViewModel @Inject constructor(private val podcastDao: PodcastDao) :
    BaseViewModel() {

    val downloadedPodcasts = MutableLiveData<List<SavedPodcast>>()

    fun getAllSavedPodcasts(): Flow<List<SavedPodcast>> {
        return podcastDao.getAllSavedPodcasts()
    }

    fun deleteDownloadedPodcast(item: RssPaginationItem) {
        downloadedPodcasts.value?.firstOrNull { item.enclosure?.url == it.podcastItem?.enclosure?.url }
            ?.let {
                viewModelScope.launch {
                    podcastDao.deleteSavedPodcast(it.id)
                }
            }
    }
}