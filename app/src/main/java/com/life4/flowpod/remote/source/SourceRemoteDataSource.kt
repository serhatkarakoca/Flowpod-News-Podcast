package com.life4.flowpod.remote.source

import com.life4.core.data.remote.BaseDataSource
import com.life4.core.models.Resource
import com.life4.flowpod.models.source.RssFeedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SourceRemoteDataSource @Inject constructor(
    private val apiService: SourceApiService
) : BaseDataSource() {

    fun getBreakingNewsSource(): Flow<Resource<RssFeedResponse>> {
        return getResult { apiService.getBreakingNewsSource() }
    }

    fun getTechNewsSource(): Flow<Resource<RssFeedResponse>> {
        return getResult { apiService.getTechNewsSource() }
    }

    fun getSportNewsSource(): Flow<Resource<RssFeedResponse>> {
        return getResult { apiService.getSportNewsSource() }
    }

    fun getHomePage(): Flow<Resource<RssFeedResponse>> {
        return getResult { apiService.getHomePage() }
    }
}
