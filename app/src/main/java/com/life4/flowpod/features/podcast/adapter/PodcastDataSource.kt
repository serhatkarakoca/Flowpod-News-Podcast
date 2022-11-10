package com.life4.flowpod.features.podcast.adapter


import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.life4.core.core.vm.BaseViewModel
import com.life4.flowpod.models.request.RssRequest
import com.life4.flowpod.models.rss_.Itunes
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.models.rss_.RssResponse
import com.life4.flowpod.remote.ApiService
import okio.IOException
import retrofit2.HttpException


class PodcastDataSource(
    private val mApi: ApiService,
    private val page: MutableLiveData<RssResponse?>? = null,
    private val mediaItems: MutableLiveData<ArrayList<RssPaginationItem>>,
    private val body: RssRequest,
    private val baseLiveData: MutableLiveData<BaseViewModel.State>? = null,
    private val perPage: Int = 20
) : PagingSource<Int, RssPaginationItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RssPaginationItem> {
        val position = params.key ?: FIRST_PAGE_INDEX
        return try {
            if (position == 1)
                baseLiveData?.value = BaseViewModel.State.ShowLoading()

            val list = mApi.getFeedFull(
                siteList = body,
                page = position,
                perPage = perPage
            ).body()
            list?.items?.mapNotNull { it }?.let {
                if (it.isEmpty().not()) {
                    page?.value = list
                }
            }
            val items = arrayListOf<RssPaginationItem>()

            list?.items?.mapNotNull { it }?.forEach {
                if (it.itunes?.image.isNullOrEmpty()) {
                    val copyItem = it.copy(
                        itunes = Itunes(
                            image = list.itunes?.image,
                            author = list.title,
                            explicit = null,
                            owner = list.itunes?.owner,
                            summary = null,
                            duration = it.itunes?.duration
                        )
                    )
                    items.add(copyItem)
                } else if (it.itunes?.author == null) {
                    val copyItem = it.copy(
                        itunes = Itunes(
                            image = it.itunes?.image,
                            author = list.title,
                            explicit = null,
                            owner = list.itunes?.owner,
                            summary = null,
                            duration = it.itunes?.duration
                        )
                    )
                    items.add(copyItem)
                } else {
                    items.add(it)
                }
            }
            mediaItems.value?.addAll(items)
            LoadResult.Page(
                data = items,
                prevKey = if (position == FIRST_PAGE_INDEX) null else position - 1,
                nextKey = if (list?.items.isNullOrEmpty() || (list?.items?.size
                        ?: 0) < perPage
                ) null else position + 1
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } finally {
            baseLiveData?.value = BaseViewModel.State.ShowContent()
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