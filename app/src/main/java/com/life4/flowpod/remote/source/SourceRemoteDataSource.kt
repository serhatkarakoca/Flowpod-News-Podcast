package com.life4.flowpod.remote.source

import com.life4.core.data.remote.BaseDataSource
import com.life4.core.manager.language.MyLanguageManager
import com.life4.core.models.Resource
import com.life4.flowpod.models.source.RssFeedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SourceRemoteDataSource @Inject constructor(
    private val apiService: SourceApiService,
    private val myLanguageManager: MyLanguageManager
) : BaseDataSource() {

    fun getContentSources(): Flow<Resource<RssFeedResponse>> {
        return getResult { if (myLanguageManager.getCurrentLanguage().languageCode == "tr") apiService.getSourcesTR() else apiService.getSourcesEN() }
    }

    fun getTechNewsSource(): Flow<Resource<RssFeedResponse>> {
        return getResult { apiService.getTechNewsSource() }
    }

    fun getSportNewsSource(): Flow<Resource<RssFeedResponse>> {
        return getResult { apiService.getSportNewsSource() }
    }

    fun getHomePage(): Flow<Resource<RssFeedResponse>> {
        return getResult { if (myLanguageManager.getCurrentLanguage().languageCode == "tr") apiService.getHomePage() else apiService.getHomePageEng() }
    }
}
