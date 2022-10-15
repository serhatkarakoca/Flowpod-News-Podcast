package com.life4.feedz.features.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.features.home.adapter.NewsDataSource
import com.life4.feedz.models.request.RssRequest
import com.life4.feedz.models.rss_.RssResponse
import com.life4.feedz.models.source.RssFeedResponse
import com.life4.feedz.models.source.RssFeedResponseItem
import com.life4.feedz.models.source.SourceModel
import com.life4.feedz.remote.ApiService
import com.life4.feedz.room.source.SourceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val sourceDao: SourceDao,
    private val mApi: ApiService
) : BaseViewModel() {

    var siteUrl: String? = null
    var rssResponse: RssResponse? = null
    val userSources = MutableLiveData<SourceModel>()
    var siteAdded = MutableLiveData(false)

    fun addSiteToBookmark() {
        deleteSourceDao()
        viewModelScope.launch {
            val newSource = arrayListOf<RssFeedResponseItem>()
            userSources.value?.sourceList?.sourceList?.let {
                newSource.addAll(it)
            }
            newSource.add(
                RssFeedResponseItem(
                    category = null,
                    categoryId = null,
                    language = null,
                    siteLogo = rssResponse?.getImageSite(),
                    siteName = rssResponse?.getSiteName(),
                    siteUrl = siteUrl,
                    description = rssResponse?.description,
                    isSelected = false
                )
            )

            sourceDao.insertSource(SourceModel(sourceList = RssFeedResponse(newSource)))
            siteAdded.value = true
        }
    }

    private fun deleteSourceDao() {
        viewModelScope.launch {
            sourceDao.deleteSources()
        }
    }

    fun deleteSource() {
        deleteSourceDao()
        viewModelScope.launch {
            val newSource = arrayListOf<RssFeedResponseItem>()
            userSources.value?.sourceList?.sourceList?.let {
                newSource.addAll(it)
            }
            newSource.remove(
                newSource.firstOrNull { it.siteUrl == siteUrl }
            )

            sourceDao.insertSource(SourceModel(sourceList = RssFeedResponse(newSource)))
            siteAdded.value = false
        }
    }

    fun getSources(): LiveData<SourceModel> {
        return sourceDao.getSources()
    }

    fun getSiteData() = Pager(
        config = PagingConfig(20, enablePlaceholders = false),
        pagingSourceFactory = {
            NewsDataSource(
                mApi,
                body = RssRequest(listOf(siteUrl ?: ""))
            )
        }
    ).flow.cachedIn(viewModelScope)
}