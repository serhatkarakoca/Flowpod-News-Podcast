package com.life4.feedz.remote

import com.life4.core.utils.performGetOperation
import javax.inject.Inject

class FeedzRepository @Inject constructor(
    private val remoteDataSource: FeedzRemoteDataSource
) {
    fun getSiteData(url: String) =
        performGetOperation(networkCall = { remoteDataSource.getSiteData(url) })
}
