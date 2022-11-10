package com.life4.flowpod.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.life4.core.core.vm.BaseViewModel
import com.life4.flowpod.data.MyPreference
import com.life4.flowpod.models.request.RssRequest
import com.life4.flowpod.models.room.NewsRemoteKey
import com.life4.flowpod.models.rss_.RssPagination
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.remote.ApiService
import com.life4.flowpod.room.news.NewsDao
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class NewsRemoteMediator(
    private val mApi: ApiService,
    private val newsDao: NewsDao,
    private val siteList: RssRequest,
    private val newsType: String,
    private val pref: MyPreference,
    private val rssResponse: MutableLiveData<RssPagination?>,
    private val baseLiveData: MutableLiveData<BaseViewModel.State>? = null
) :
    RemoteMediator<Int, RssPaginationItem>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RssPaginationItem>
    ): MediatorResult {
        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        try {
            if (page == 1 && newsDao.getAllREmoteKey(newsType) == null) {
                baseLiveData?.value = BaseViewModel.State.ShowLoading()
            }
            val response =
                mApi.getSiteData(page = page, siteList = siteList, perPage = state.config.pageSize)

            if (response.isSuccessful) {
                val isEndOfList = (response.body()?.size ?: 0) < state.config.pageSize
                val responseBody = response.body()
                if (loadType == LoadType.REFRESH) {
                    newsDao.deleteRemoteKey(newsType)
                    newsDao.deleteNews(newsType)
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1

                val keys = responseBody?.mapNotNull {
                    NewsRemoteKey(newsType, prev = prevKey, next = nextKey)
                } ?: listOf()

                responseBody?.forEach { it.pKey = newsType }
                newsDao.insertNews(responseBody ?: listOf())
                newsDao.insertAllRemoteKeys(keys)
                rssResponse.value = responseBody

                return MediatorResult.Success(endOfPaginationReached = isEndOfList)
            } else {
                rssResponse.value = RssPagination()
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        } finally {
            baseLiveData?.value = BaseViewModel.State.ShowContent()
        }
    }

    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, RssPaginationItem>
    ): Any {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKeys(state)
                remoteKeys?.next?.minus(1) ?: 1
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.next
                return nextKey
                    ?: MediatorResult.Success(endOfPaginationReached = remoteKeys == null)
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                val prevKey = remoteKeys?.prev
                return prevKey
                    ?: MediatorResult.Success(endOfPaginationReached = remoteKeys != null)

            }
        }
    }

    private suspend fun getClosestRemoteKeys(state: PagingState<Int, RssPaginationItem>): NewsRemoteKey? {

        return state.anchorPosition?.let {
            state.closestItemToPosition(it)?.pKey?.let {
                newsDao.getAllREmoteKey(it)
            }
        }

    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, RssPaginationItem>): NewsRemoteKey? {
        return state.lastItemOrNull()?.pKey?.let {
            newsDao.getAllREmoteKey(it)
        }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, RssPaginationItem>): NewsRemoteKey? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.pKey?.let { key -> newsDao.getAllREmoteKey(key) }
    }
}





