package com.life4.feedz.features.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.RssResponse
import com.life4.feedz.other.Constant
import com.life4.feedz.remote.FeedzRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val feedzRepository: FeedzRepository
) : BaseViewModel() {

    private val _siteData = MutableLiveData<RssResponse>()
    val siteData: LiveData<RssResponse>
        get() = _siteData

    var indexUrlPath = 0
    var confirmedUrl: String? = null
    var progressStatus = MutableLiveData(false)

    fun tryToGetSite(url: String) {
        val mUrl = getFormattedUrl(url, indexUrlPath)
        getSite(mUrl, indexUrlPath, mUrl != url)
    }

    private fun getSite(url: String, index: Int, tryToUrls: Boolean = false) {
        if (progressStatus.value != true)
            progressStatus.value = true
        feedzRepository.getSiteData(url).handle(requestType = RequestType.CUSTOM, onComplete = {
            if (it.homePageUrl != null && it.items.isNullOrEmpty().not()) {
                _siteData.value = it
                confirmedUrl = url
                progressStatus.value = false
            } else {
                if (!tryToUrls) {
                    _siteData.value = RssResponse(
                        description = null,
                        homePageUrl = null,
                        items = listOf(),
                        title = "empty",
                        version = null
                    )
                    indexUrlPath = 0
                    progressStatus.value = false
                } else if (tryToUrls && index == Constant.END_PREFIX.lastIndex) {
                    _siteData.value = RssResponse(
                        description = null,
                        homePageUrl = null,
                        items = listOf(),
                        title = "empty",
                        version = null
                    )
                    indexUrlPath = 0
                    progressStatus.value = false
                } else {
                    indexUrlPath++
                    tryToGetSite(url)
                }

            }

        }, onError = {
            indexUrlPath = 0
            _siteData.postValue(
                RssResponse(
                    description = null,
                    homePageUrl = null,
                    items = listOf(),
                    title = "empty",
                    version = null
                )
            )
            progressStatus.value = false
        })
    }

    private fun getSiteUrl(url: String): String {
        return if (url.startsWith("http://") || url.startsWith("https://"))
            url
        else
            "https://$url"
    }

    private fun getFormattedUrl(url: String, index: Int): String {
        var mUrl = getSiteUrl(url)
        val endPrefix = Constant.END_PREFIX.firstOrNull { mUrl.endsWith(it) }
        if (endPrefix != null) {
            return mUrl
        } else {
            return mUrl + Constant.END_PREFIX.get(index)
        }
    }
}