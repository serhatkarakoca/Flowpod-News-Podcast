package com.life4.flowpod.remote.source

import com.life4.flowpod.models.source.RssFeedResponse
import retrofit2.Response
import retrofit2.http.GET

interface SourceApiService {

    @GET("rss_content_tr.json/")
    suspend fun getSourcesTR(): Response<RssFeedResponse>

    @GET("rss_content_en.json/")
    suspend fun getSourcesEN(): Response<RssFeedResponse>

    @GET("rss_tech.json/")
    suspend fun getTechNewsSource(): Response<RssFeedResponse>

    @GET("rss_sport.json/")
    suspend fun getSportNewsSource(): Response<RssFeedResponse>

    @GET("rss_home.json/")
    suspend fun getHomePage(): Response<RssFeedResponse>

    @GET("rss_home_eng.json/")
    suspend fun getHomePageEng(): Response<RssFeedResponse>
}