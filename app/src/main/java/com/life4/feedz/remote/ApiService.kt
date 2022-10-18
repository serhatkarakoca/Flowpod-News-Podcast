package com.life4.feedz.remote

import com.life4.feedz.models.request.RssRequest
import com.life4.feedz.models.rss_.RssPagination
import com.life4.feedz.models.rss_.RssResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("api/search")
    suspend fun searchSite(@Query("url") url: String): Response<RssResponse>

    @POST("api/getFeed")
    suspend fun getSiteData(
        @Body siteList: RssRequest,
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int? = 20
    ): Response<RssPagination>
}