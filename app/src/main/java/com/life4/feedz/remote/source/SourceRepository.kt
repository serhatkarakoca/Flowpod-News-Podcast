package com.life4.feedz.remote.source

import javax.inject.Inject

class SourceRepository @Inject constructor(
    private val remoteDataSource: SourceRemoteDataSource
) {

    fun getBreakingNewsSource() = remoteDataSource.getBreakingNewsSource()
}
