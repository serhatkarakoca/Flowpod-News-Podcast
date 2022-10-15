package com.life4.feedz.remote

import com.life4.core.data.remote.BaseDataSource
import com.life4.core.models.Resource
import com.life4.feedz.models.rss_.RssResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedzRemoteDataSource @Inject constructor(
    private val feedzService: ApiService
) : BaseDataSource() {

    fun searchSite(url: String): Flow<Resource<RssResponse>> {
        return getResult { feedzService.searchSite(url) }
    }
}
