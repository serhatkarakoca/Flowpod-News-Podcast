package com.life4.feedz.remote

import com.life4.core.data.remote.BaseDataSource
import javax.inject.Inject

class FeedzRemoteDataSource @Inject constructor(
    private val feedzService: ApiService
) : BaseDataSource() {

    suspend fun getSiteData(url: String) = getResult { feedzService.getSiteData(url) }
}
