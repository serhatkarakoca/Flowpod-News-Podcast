package com.life4.feedz.remote

import com.life4.core.data.remote.BaseDataSource
import com.life4.core.models.Resource
import com.life4.feedz.models.RssResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FeedzRemoteDataSource @Inject constructor(
    private val feedzService: ApiService
) : BaseDataSource() {


    fun getSiteData(url: String): Flow<Resource<RssResponse>> = flow {
        val res = getResult { feedzService.getSiteData(url) }
        emit(res)
    }
}
