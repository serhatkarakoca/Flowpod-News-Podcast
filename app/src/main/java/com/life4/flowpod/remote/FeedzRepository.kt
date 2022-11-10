package com.life4.flowpod.remote

import com.life4.flowpod.models.request.RssRequest
import javax.inject.Inject

class FeedzRepository @Inject constructor(
    private val remoteDataSource: FeedzRemoteDataSource
) {
    fun searchSite(url: String) = remoteDataSource.searchSite(url)
    fun searchPodcast(query: String) = remoteDataSource.searchPodcast(query)
    fun getPodcastFeed() = remoteDataSource.getPodcastFeed()
    fun getPodcastFeedByCategory(category: String) =
        remoteDataSource.getPodcastFeedByCategory(category)

    fun getSiteData(siteList: RssRequest) = remoteDataSource.getSiteData(siteList)
    fun getFeedFull(siteList: RssRequest) = remoteDataSource.getFeedFull(siteList)
    fun getCategories() = remoteDataSource.getCategories()

}
