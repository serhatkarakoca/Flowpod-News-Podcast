package com.life4.feedz.features.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.Item
import com.life4.feedz.models.RssResponse
import com.life4.feedz.remote.FeedzRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedzRepository: FeedzRepository
) : BaseViewModel() {

    private val _siteData = MutableLiveData<RssResponse>()
    val siteData: LiveData<RssResponse>
        get() = _siteData

    private val _siteDataList = MutableLiveData<List<Item?>>()
    val siteDataList: LiveData<List<Item?>>
        get() = _siteDataList

    private val _siteDataListArray = arrayListOf<Item?>()

    private val breakingNews = arrayListOf(
        "https://www.ntv.com.tr/son-dakika.rss",
        "https://www.ensonhaber.com/rss/ensonhaber.xml"
    )

    var index = 0
    val url = breakingNews[index]

    fun getSiteData() {
        _siteDataList.value = listOf()
        _siteDataListArray.clear()

        feedzRepository.getSiteData(url).handle(requestType = RequestType.ACTION, onComplete = {
            _siteDataListArray.addAll(it.items ?: listOf())
            if (index == breakingNews.lastIndex) {
                index = 0
                _siteDataList.value = _siteDataListArray
            } else {
                index++
                getSiteData()
            }
        })
    }
}
