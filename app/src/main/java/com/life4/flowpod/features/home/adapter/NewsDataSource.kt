package com.life4.flowpod.features.home.adapter


import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.life4.flowpod.models.request.RssRequest
import com.life4.flowpod.models.rss_.RssPagination
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.remote.ApiService
import okio.IOException
import retrofit2.HttpException


class NewsDataSource(
    private val mApi: ApiService,
    private val page: MutableLiveData<RssPagination?>? = null,
    private val body: RssRequest,
    private val perPage: Int? = 20
) : PagingSource<Int, RssPaginationItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RssPaginationItem> {
        val position = params.key ?: FIRST_PAGE_INDEX
        return try {

            val list = mApi.getSiteData(
                siteList = body,
                page = position,
                perPage = perPage
            ).body()
            page?.value = list

            LoadResult.Page(
                data = list ?: listOf(),
                prevKey = if (position == FIRST_PAGE_INDEX) null else position - 1,
                nextKey = if (list.isNullOrEmpty() || perPage == 10) null else position + 1
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, RssPaginationItem>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}