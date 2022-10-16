package com.life4.feedz.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.life4.feedz.data.MyPreference
import com.life4.feedz.models.request.RssRequest
import com.life4.feedz.models.rss_.RssPagination
import com.life4.feedz.models.rss_.RssPaginationItem
import com.life4.feedz.remote.ApiService
import com.life4.feedz.room.news.NewsDao
import com.life4.feedz.room.news.NewsRemoteKey
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@ExperimentalPagingApi
class NewsRemoteMediator(
    private val mApi: ApiService,
    private val newsDao: NewsDao,
    private val siteList: RssRequest,
    private val newsType: String,
    private val pref: MyPreference,
    private val rssResponse: MutableLiveData<RssPagination?>
) :
    RemoteMediator<Int, RssPaginationItem>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)
        return if (System.currentTimeMillis() - pref.getTime() >= cacheTimeout && pref.getTime() != 0L)
            InitializeAction.SKIP_INITIAL_REFRESH
        else
            InitializeAction.LAUNCH_INITIAL_REFRESH

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
                pref.saveTime(System.currentTimeMillis())
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





