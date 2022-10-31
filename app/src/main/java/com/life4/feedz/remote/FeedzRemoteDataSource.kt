package com.life4.feedz.remote

import com.life4.core.data.remote.BaseDataSource
import com.life4.core.manager.language.MyLanguageManager
import com.life4.core.models.Resource
import com.life4.feedz.models.podcast.PodcastResponse
import com.life4.feedz.models.podcast.categories.PodcastCategories
import com.life4.feedz.models.request.RssRequest
import com.life4.feedz.models.rss_.RssPagination
import com.life4.feedz.models.rss_.RssResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedzRemoteDataSource @Inject constructor(
    private val feedzService: ApiService,
    private val languageManager: MyLanguageManager
) : BaseDataSource() {

    fun searchSite(url: String): Flow<Resource<RssResponse>> {
        return getResult { feedzService.searchSite(url) }
    }

    fun searchPodcast(query: String): Flow<Resource<PodcastResponse>> {
        return getResult { feedzService.searchPodcast(query) }
    }

    fun getPodcastFeed(): Flow<Resource<PodcastResponse>> {
        return getResult {
            feedzService.getPodcastFeed(
                languageManager.getCurrentLanguage().languageCode,
                75
            )
        }
    }

    fun getPodcastFeedByCategory(category: String): Flow<Resource<PodcastResponse>> {
        return getResult {
            feedzService.getPodcastFeedByCategory(
                languageManager.getCurrentLanguage().languageCode,
                75,
                category
            )
        }
    }

    fun getSiteData(siteList: RssRequest): Flow<Resource<RssPagination>> {
        return getResult { feedzService.getSiteData(siteList, 1, 20) }
    }

    fun getCategories(): Flow<Resource<PodcastCategories>> {
        return getResult { feedzService.getCategories() }
    }
}
