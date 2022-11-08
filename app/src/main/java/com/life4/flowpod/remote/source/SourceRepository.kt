package com.life4.flowpod.remote.source

import javax.inject.Inject

class SourceRepository @Inject constructor(
    private val remoteDataSource: SourceRemoteDataSource
) {
    fun getSources() = remoteDataSource.getContentSources()
    fun getTechNewsSource() = remoteDataSource.getTechNewsSource()
    fun getSportNewsSource() = remoteDataSource.getSportNewsSource()
    fun getHomePage() = remoteDataSource.getHomePage()
}
