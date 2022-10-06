package com.life4.feedz.features.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.Item
import com.life4.feedz.models.RssResponse
import com.life4.feedz.remote.FeedzRepository
import com.life4.feedz.remote.source.SourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedzRepository: FeedzRepository,
    private val sourceRepository: SourceRepository
) : BaseViewModel() {

    private val _siteData = MutableLiveData<RssResponse>()
    val siteData: LiveData<RssResponse>
        get() = _siteData

    private val _siteDataList = MutableLiveData<List<Item?>>()
    val siteDataList: LiveData<List<Item?>>
        get() = _siteDataList

    val siteDataListArray = arrayListOf<Item?>()

    private val breakingNews = arrayListOf(
        "https://www.ntv.com.tr/son-dakika.rss",
        "https://www.ensonhaber.com/rss/ensonhaber.xml"
    )

    fun getBreakingNews() {
        siteDataListArray.clear()
        //_siteDataList.value = arrayListOf()
        viewModelScope.launch {
            (0..breakingNews.lastIndex).map {
                async(Dispatchers.IO) { getSiteData(it) }
            }.awaitAll()
        }
    }

    private fun getSiteData(index: Int) {
        feedzRepository.getSiteData(breakingNews.get(index))
            .handle(requestType = RequestType.ACTION, onComplete = {
                siteDataListArray.addAll(it.items ?: listOf())
                if (index == breakingNews.lastIndex)
                    _siteDataList.value = siteDataListArray
            }, onError = {
                _siteDataList.value = siteDataListArray
            })
    }
}
