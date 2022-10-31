package com.life4.feedz.features.source.sourcesList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.source.RssFeedResponse
import com.life4.feedz.models.source.RssFeedResponseItem
import com.life4.feedz.models.source.SourceModel
import com.life4.feedz.remote.source.SourceRepository
import com.life4.feedz.room.source.SourceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourcesListViewModel @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val sourceDao: SourceDao
) : BaseViewModel() {

    private val _siteDataListSports = MutableLiveData<List<RssFeedResponseItem>>()
    val siteDataListSports: LiveData<List<RssFeedResponseItem>>
        get() = _siteDataListSports

    private val _liveData = MutableLiveData<State>()
    val liveData: LiveData<State>
        get() = _liveData

    val sourcePref = arrayListOf<RssFeedResponseItem>()

    fun deleteSavedSource() {
        viewModelScope.launch {
            sourceDao.deleteSources()
        }
    }

    fun getSavedSource(): LiveData<SourceModel> {
        return sourceDao.getSources()
    }

    fun insertSourceToRoom() {
        viewModelScope.launch {
            sourceDao.insertSource(SourceModel(sourceList = RssFeedResponse(sourcePref)))
            _liveData.value = State.OnSourceAdded
        }
    }

    sealed class State {
        object OnSourceAdded : State()
    }
}