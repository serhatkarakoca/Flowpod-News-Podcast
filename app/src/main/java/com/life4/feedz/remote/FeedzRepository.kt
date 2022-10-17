package com.life4.feedz.remote

import com.life4.feedz.models.request.RssRequest
import javax.inject.Inject

class FeedzRepository @Inject constructor(
    private val remoteDataSource: FeedzRemoteDataSource
) {
    fun searchSite(url: String) = remoteDataSource.searchSite(url)
    fun getSiteData(siteList: RssRequest) = remoteDataSource.getSiteData(siteList)

}
