package com.life4.feedz.remote.source

import com.life4.core.data.remote.BaseDataSource
import com.life4.core.models.Resource
import com.life4.feedz.models.source.RssFeedResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SourceRemoteDataSource @Inject constructor(
    private val apiService: SourceApiService
) : BaseDataSource() {

    fun getBreakingNewsSource(): Flow<Resource<RssFeedResponse>> = flow {
        val res = getResult { apiService.getBreakingNewsSource() }
        emit(res)
    }
}
