package com.life4.flowpod.features.flow.newsflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.life4.core.core.vm.BaseViewModel
import com.life4.flowpod.data.MyPreference
import com.life4.flowpod.models.request.RssRequest
import com.life4.flowpod.models.rss_.RssPagination
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.models.source.RssFeedResponseItem
import com.life4.flowpod.models.source.SourceModel
import com.life4.flowpod.other.Constant
import com.life4.flowpod.paging.NewsRemoteMediator
import com.life4.flowpod.remote.ApiService
import com.life4.flowpod.remote.FeedzRepository
import com.life4.flowpod.room.news.NewsDao
import com.life4.flowpod.room.source.SourceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NewsFlowViewModel @Inject constructor(
    private val feedzRepository: FeedzRepository,
    private val sourceDao: SourceDao,
    private val mApi: ApiService,
    private val newsDao: NewsDao,
    private val pref: MyPreference
) : BaseViewModel() {

    init {
        pref.saveTime(0)
    }

    val selectedCategory = MutableLiveData(0)

    val userSources = MutableLiveData<SourceModel>()

    val siteDataList = MutableLiveData<RssPagination?>()

    private val _liveData = MutableLiveData<State>()
    val liveData: LiveData<State>
        get() = _liveData

    val selectedUserList = MutableLiveData<List<RssFeedResponseItem>>()
    var fromFilter: Boolean = false

    fun getSources(): LiveData<SourceModel> {
        return sourceDao.getSources()
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getFilteredNews(): Flow<PagingData<RssPaginationItem>> {
        val filteredList =
            userSources.value?.sourceList?.sourceList?.filter { if (selectedCategory.value != 0) it.categoryId == selectedCategory.value else true }
                ?: listOf()
        if (!fromFilter) {
            filteredList.forEach { it.isSelected = true }
            selectedUserList.value = filteredList
        }
        val paging = Pager(
            config = PagingConfig(10, enablePlaceholders = true),
            remoteMediator =
            NewsRemoteMediator(
                mApi,
                newsDao,
                RssRequest(selectedUserList.value?.filter { if (fromFilter) it.isSelected else true }
                    ?.mapNotNull { it.siteUrl }),
                Constant.FLOW.toString(),
                pref,
                siteDataList,
                baseLiveData
            )

        ) {
            newsDao.getAllNews(Constant.FLOW.toString())
        }.flow

        return paging
    }

    sealed class State {
        data class OnNewsFetch(val news: List<RssPaginationItem?>) : State()
    }
}