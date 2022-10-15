package com.life4.feedz.remote

import javax.inject.Inject

class FeedzRepository @Inject constructor(
    private val remoteDataSource: FeedzRemoteDataSource
) {
    fun searchSite(url: String) = remoteDataSource.searchSite(url)

}
