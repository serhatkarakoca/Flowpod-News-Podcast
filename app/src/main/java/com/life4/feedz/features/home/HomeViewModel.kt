package com.life4.feedz.features.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.features.home.adapter.NewsDataSource
import com.life4.feedz.models.request.RssRequest
import com.life4.feedz.models.rss_.RssPagination
import com.life4.feedz.models.source.RssFeedResponse
import com.life4.feedz.other.Constant
import com.life4.feedz.paging.NewsRemoteMediator
import com.life4.feedz.remote.ApiService
import com.life4.feedz.remote.FeedzRepository
import com.life4.feedz.remote.source.SourceRepository
import com.life4.feedz.room.news.NewsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedzRepository: FeedzRepository,
    private val sourceRepository: SourceRepository,
    private val mApi: ApiService,
    private val newsDao: NewsDao
) : BaseViewModel() {

    private val _siteData = MutableLiveData<RssFeedResponse>()
    val siteData: LiveData<RssFeedResponse>
        get() = _siteData

    private val _siteDataList = MutableLiveData<RssPagination?>()
    val siteDataList: LiveData<RssPagination?>
        get() = _siteDataList


    fun getSources(onComplete: () -> Unit) {
        sourceRepository.getHomePage().handle(requestType = RequestType.INIT, onComplete = {
            _siteData.value = it
            onComplete.invoke()
        }, onError = {
            onComplete.invoke()
        })
    }

    fun getSiteData() = Pager(
        config = PagingConfig(20, enablePlaceholders = false),
        pagingSourceFactory = {
            NewsDataSource(
                mApi,
                _siteDataList,
                RssRequest(siteData.value?.sourceList?.mapNotNull { it.siteUrl } ?: listOf())
            )
        }
    ).flow.cachedIn(viewModelScope)

    /*
    fun getFilteredNews(categoryId: Int) = Pager(
        config = PagingConfig(10, enablePlaceholders = false),
        pagingSourceFactory = {
            NewsDataSource(
                mApi,
                _siteDataList,
                RssRequest(siteData.value?.sourceList?.filter { it.categoryId == categoryId }
                    ?.mapNotNull { it.siteUrl } ?: listOf()),
                10
            )
        }
    ).flow.cachedIn(viewModelScope)

     */


    fun getFilteredNews(categoryId: Int) = Pager(
        config = PagingConfig(10, enablePlaceholders = true),
        remoteMediator = NewsRemoteMediator(
            mApi,
            newsDao,
            RssRequest(siteData.value?.sourceList?.filter { it.categoryId == categoryId }
                ?.mapNotNull { it.siteUrl } ?: listOf()),
            Constant.BREAKING_NEWS.toString()
        )

    ) {
        newsDao.getAllNews(Constant.BREAKING_NEWS.toString())
    }.flow


    fun getTechNews(categoryId: Int) = Pager(
        config = PagingConfig(10, enablePlaceholders = true),
        remoteMediator = NewsRemoteMediator(
            mApi,
            newsDao,
            RssRequest(siteData.value?.sourceList?.filter { it.categoryId == categoryId }
                ?.mapNotNull { it.siteUrl } ?: listOf()),
            Constant.TECH_NEWS.toString()
        )

    ) {
        newsDao.getAllNews(Constant.TECH_NEWS.toString())
    }.flow


    fun getSportNews(categoryId: Int) = Pager(
        config = PagingConfig(10, enablePlaceholders = true),
        remoteMediator = NewsRemoteMediator(
            mApi,
            newsDao,
            RssRequest(siteData.value?.sourceList?.filter { it.categoryId == categoryId }
                ?.mapNotNull { it.siteUrl } ?: listOf()),
            Constant.SPORT_NEWS.toString()
        )

    ) {
        newsDao.getAllNews(Constant.SPORT_NEWS.toString())
    }.flow

    sealed class State {

    }
}
