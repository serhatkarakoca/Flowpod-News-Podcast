package com.life4.feedz.features.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.request.RssRequest
import com.life4.feedz.models.room.NewsRemoteKey
import com.life4.feedz.models.rss_.RssPagination
import com.life4.feedz.models.source.RssFeedResponse
import com.life4.feedz.other.Constant
import com.life4.feedz.remote.FeedzRepository
import com.life4.feedz.remote.source.SourceRepository
import com.life4.feedz.room.news.NewsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val newsDao: NewsDao,
    private val sourceRepository: SourceRepository,
    private val feedzRepository: FeedzRepository
) : BaseViewModel() {

    var cachedData = true

    private val _siteData = MutableLiveData<RssFeedResponse>()
    val siteData: LiveData<RssFeedResponse>
        get() = _siteData

    private val _siteDataList = MutableLiveData<RssPagination?>()
    val siteDataList: LiveData<RssPagination?>
        get() = _siteDataList

    fun getCachedData() {

        viewModelScope.launch {
            if (newsDao.getAllREmoteKey(Constant.HOME_NEWS.toString()) != null)
                cachedData = false
            else {
                getSources {
                    viewModelScope.launch {
                        getHomeNews {
                            cachedData = false
                        }
                    }

                }
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getHomeNews(onComplete: () -> Unit) {
        feedzRepository.getSiteData(RssRequest(siteData.value?.sourceList?.filter { it.categoryId != Constant.SPORT_NEWS }
            ?.mapNotNull { it.siteUrl } ?: listOf()))
            .handle(requestType = RequestType.CUSTOM, onComplete = {
                viewModelScope.launch {
                    val keys = it.mapNotNull {
                        NewsRemoteKey(
                            Constant.HOME_NEWS.toString(),
                            null,
                            2
                        )
                    }

                    it.forEach { it.pKey = Constant.HOME_NEWS.toString() }
                    newsDao.insertNews(it)
                    newsDao.insertAllRemoteKeys(keys)
                    onComplete.invoke()
                }
            }, onError = {
                onComplete.invoke()
            })
    }

    fun getSources(onComplete: () -> Unit) {
        sourceRepository.getHomePage().handle(requestType = RequestType.INIT, onComplete = {
            _siteData.value = it
            onComplete.invoke()
        }, onError = {
            onComplete.invoke()
        })
    }
}
