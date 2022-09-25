package com.life4.feedz.remote

import com.life4.feedz.models.RssResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("convert")
    suspend fun getSiteData(@Query("url") url: String): Response<RssResponse>
}