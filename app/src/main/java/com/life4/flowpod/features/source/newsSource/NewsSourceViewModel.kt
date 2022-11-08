package com.life4.flowpod.features.source.newsSource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.flowpod.models.source.RssFeedResponse
import com.life4.flowpod.models.source.RssFeedResponseItem
import com.life4.flowpod.models.source.SourceModel
import com.life4.flowpod.remote.source.SourceRepository
import com.life4.flowpod.room.source.SourceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsSourceViewModel @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val sourceDao: SourceDao
) : BaseViewModel() {

    private val _siteDataList = MutableLiveData<List<RssFeedResponseItem>>()
    val siteDataList: LiveData<List<RssFeedResponseItem>>
        get() = _siteDataList

    private val _liveData = MutableLiveData<State>()
    val liveData: LiveData<State>
        get() = _liveData

    val sourcePref = arrayListOf<RssFeedResponseItem>()
    var allSources: MutableMap<Int, List<RssFeedResponseItem>?>? = null

    fun getNewsSource() {
        sourceRepository.getSources().handle(RequestType.ACTION, onComplete = {
            _siteDataList.value = it.sourceList
            _liveData.value = State.OnSourceReady(it.sourceList)
        })
    }

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
        data class OnSourceReady(val list: List<RssFeedResponseItem>) : State()
        object OnSourceAdded : State()
    }
}