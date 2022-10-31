package com.life4.feedz.remote

import com.life4.feedz.BuildConfig
import com.life4.feedz.models.podcast.PodcastResponse
import com.life4.feedz.models.podcast.categories.PodcastCategories
import com.life4.feedz.models.request.RssRequest
import com.life4.feedz.models.rss_.RssPagination
import com.life4.feedz.models.rss_.RssResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/search")
    suspend fun searchSite(@Query("url") url: String): Response<RssResponse>

    @POST("api/feedPagination")
    suspend fun getSiteData(
        @Body siteList: RssRequest,
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int? = 20
    ): Response<RssPagination>

    @Headers("api_key: ${BuildConfig.API_KEY}", "api_secret: ${BuildConfig.API_SECRET}")
    @GET("api/searchPodcast")
    suspend fun searchPodcast(@Query("query") query: String): Response<PodcastResponse>

    @Headers("api_key: ${BuildConfig.API_KEY}", "api_secret: ${BuildConfig.API_SECRET}")
    @GET("/api/podcastFeed")
    suspend fun getPodcastFeed(
        @Query("lang") language: String,
        @Query("max") maxSize: Int
    ): Response<PodcastResponse>

    @Headers("api_key: ${BuildConfig.API_KEY}", "api_secret: ${BuildConfig.API_SECRET}")
    @GET("/api/categories")
    suspend fun getCategories(
    ): Response<PodcastCategories>

    @Headers("api_key: ${BuildConfig.API_KEY}", "api_secret: ${BuildConfig.API_SECRET}")
    @GET("/api/podcastFeedByCat")
    suspend fun getPodcastFeedByCategory(
        @Query("lang") language: String,
        @Query("max") maxSize: Int,
        @Query("category") category: String
    ): Response<PodcastResponse>
}