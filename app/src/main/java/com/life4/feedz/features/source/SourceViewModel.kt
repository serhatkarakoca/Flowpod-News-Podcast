package com.life4.feedz.features.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.source.RssFeedResponseItem
import com.life4.feedz.remote.source.SourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SourceViewModel @Inject constructor(
    private val sourceRepository: SourceRepository
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


    fun getBreakingNewsSource() {
        sourceRepository.getBreakingNewsSource().handle(RequestType.ACTION, onComplete = {
            _siteDataListBreaking.value = it
            getTechNewsSource()
        }, onError = {})
    }

    fun getTechNewsSource() {
        sourceRepository.getTechNewsSource().handle(RequestType.ACTION, onComplete = {
            _siteDataListTech.value = it
            getSportNewsSource()
        }, onError = {})
    }

    fun getSportNewsSource() {
        sourceRepository.getSportNewsSource().handle(RequestType.ACTION, onComplete = {
            _siteDataListSports.value = it
            _liveData.value = State.OnSourceReady(
                mutableMapOf(
                    1 to _siteDataListBreaking.value,
                    2 to _siteDataListTech.value,
                    3 to _siteDataListSports.value,
                )
            )
        }, onError = {})
    }

    sealed class State {
        data class OnSourceReady(val listMap: MutableMap<Int, List<RssFeedResponseItem>?>) : State()
    }
}