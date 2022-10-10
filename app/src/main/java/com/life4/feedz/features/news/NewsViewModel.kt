package com.life4.feedz.features.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.RssResponse
import com.life4.feedz.models.source.RssFeedResponse
import com.life4.feedz.models.source.RssFeedResponseItem
import com.life4.feedz.models.source.SourceModel
import com.life4.feedz.room.source.SourceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val sourceDao: SourceDao
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
                    siteLogo = null,
                    siteName = rssResponse?.title,
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
                RssFeedResponseItem(
                    category = null,
                    categoryId = null,
                    language = null,
                    siteLogo = null,
                    siteName = null,
                    siteUrl = siteUrl,
                    description = rssResponse?.description,
                    isSelected = false
                )
            )

            sourceDao.insertSource(SourceModel(sourceList = RssFeedResponse(newSource)))
            siteAdded.value = false
        }
    }

    fun getSources(): LiveData<SourceModel> {
        return sourceDao.getSources()
    }
}