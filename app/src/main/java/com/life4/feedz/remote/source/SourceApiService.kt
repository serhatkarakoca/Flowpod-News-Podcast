package com.life4.feedz.remote.source

import com.life4.feedz.models.source.RssFeedResponse
import retrofit2.Response
import retrofit2.http.GET

interface SourceApiService {

    @GET("rss_feeds.json/")
    suspend fun getBreakingNewsSource(): Response<RssFeedResponse>

    @GET("rss_tech.json/")
    suspend fun getTechNewsSource(): Response<RssFeedResponse>

    @GET("rss_sport.json/")
    suspend fun getSportNewsSource(): Response<RssFeedResponse>

}