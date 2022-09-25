package com.life4.feedz.remote

import javax.inject.Inject

class FeedzRepository @Inject constructor(
    private val remoteDataSource: FeedzRemoteDataSource
) {
    fun getSiteData(url: String) = remoteDataSource.getSiteData(url)
}
