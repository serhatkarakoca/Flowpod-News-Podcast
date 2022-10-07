package com.life4.feedz.features.source

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
class SourceViewModel @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val sourceDao: SourceDao
) : BaseViewModel() {

    private val _siteDataListBreaking = MutableLiveData<List<RssFeedResponseItem>>()
    val siteDataListBreaking: LiveData<List<RssFeedResponseItem>>
        get() = _siteDataListBreaking

    private val _siteDataListTech = MutableLiveData<List<RssFeedResponseItem>>()
    val siteDataListTech: LiveData<List<RssFeedResponseItem>>
        get() = _siteDataListTech

    private val _siteDataListSports = MutableLiveData<List<RssFeedResponseItem>>()
    val siteDataListSports: LiveData<List<RssFeedResponseItem>>
        get() = _siteDataListSports

    private val _liveData = MutableLiveData<State>()
    val liveData: LiveData<State>
        get() = _liveData

    val sourcePref = arrayListOf<RssFeedResponseItem>()
    var allSources: MutableMap<Int, List<RssFeedResponseItem>?>? = null

    fun getBreakingNewsSource() {
        baseLiveData.value = BaseViewModel.State.ShowLoading()
        sourceRepository.getBreakingNewsSource().handle(RequestType.CUSTOM, onComplete = {
            _siteDataListBreaking.value = it.sourceList
            getTechNewsSource()
        }, onError = {
            baseLiveData.value = BaseViewModel.State.ShowContent()
        })
    }

    private fun getTechNewsSource() {
        sourceRepository.getTechNewsSource().handle(RequestType.CUSTOM, onComplete = {
            _siteDataListTech.value = it.sourceList
            getSportNewsSource()
        }, onError = {
            baseLiveData.value = BaseViewModel.State.ShowContent()
        })
    }

    private fun getSportNewsSource() {
        sourceRepository.getSportNewsSource().handle(RequestType.CUSTOM, onComplete = {
            _siteDataListSports.value = it.sourceList
            allSources = mutableMapOf(
                1 to _siteDataListBreaking.value,
                2 to _siteDataListTech.value,
                3 to _siteDataListSports.value
            )
            baseLiveData.value = BaseViewModel.State.ShowContent()
            _liveData.value = State.OnSourceReady(allSources ?: mutableMapOf())
        }, onError = {
            baseLiveData.value = BaseViewModel.State.ShowContent()
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
        data class OnSourceReady(val listMap: MutableMap<Int, List<RssFeedResponseItem>?>) : State()
        object OnSourceAdded : State()
    }
}