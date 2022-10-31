package com.life4.feedz.features.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.podcast.PodcastResponse
import com.life4.feedz.models.rss_.RssResponse
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

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    var indexUrlPath = 0
    var confirmedUrl: String? = null
    var progressStatus = MutableLiveData(false)
    var searchingBaseUrl: String? = null


    fun searchPodcast(query: String) {
        feedzRepository.searchPodcast(query).handle(RequestType.ACTION, onComplete = {
            _state.value = State.OnPodcastSearchSuccess(it)
        })
    }

    fun tryToGetSite(url: String) {
        val mUrl = getFormattedUrl(searchingBaseUrl ?: "", indexUrlPath)
        getSite(mUrl, indexUrlPath, mUrl != url)
    }

    private fun getSite(url: String, index: Int, tryToUrls: Boolean = false) {
        if (progressStatus.value != true)
            progressStatus.value = true
        feedzRepository.searchSite(url).handle(requestType = RequestType.CUSTOM, onComplete = {
            if (it.link != null && it.items.isNullOrEmpty().not()) {
                _siteData.value = it
                confirmedUrl = url
                progressStatus.value = false
            } else {
                if (!tryToUrls) {
                    _siteData.value = RssResponse(
                        description = null,
                        image = null,
                        items = listOf(),
                        link = null,
                        title = "empty",
                        feedUrl = null,
                        language = null,
                        lastBuildDate = null,
                        itunes = null
                    )
                    indexUrlPath = 0
                    progressStatus.value = false
                } else if (tryToUrls && index == Constant.END_PREFIX.lastIndex) {
                    _siteData.value = RssResponse(
                        description = null,
                        image = null,
                        items = listOf(),
                        link = null,
                        title = "empty",
                        feedUrl = null,
                        language = null,
                        lastBuildDate = null,
                        itunes = null
                    )
                    indexUrlPath = 0
                    progressStatus.value = false
                } else {
                    indexUrlPath++
                    tryToGetSite(url)
                }

            }

        }, onError = {
            if (!tryToUrls) {
                _siteData.value = RssResponse(
                    description = null,
                    image = null,
                    items = listOf(),
                    link = null,
                    title = "empty",
                    feedUrl = null,
                    language = null,
                    lastBuildDate = null,
                    itunes = null
                )
                indexUrlPath = 0
                progressStatus.value = false
            } else if (tryToUrls && index == Constant.END_PREFIX.lastIndex) {
                _siteData.value = RssResponse(
                    description = null,
                    image = null,
                    items = listOf(),
                    link = null,
                    title = "empty",
                    feedUrl = null,
                    language = null,
                    lastBuildDate = null,
                    itunes = null
                )
                indexUrlPath = 0
                progressStatus.value = false
            } else {
                indexUrlPath++
                tryToGetSite(url)
            }

        })
    }

    private fun getSiteUrl(url: String): String {
        return if (url.startsWith("http://") || url.startsWith("https://"))
            url
        else
            "https://$url"
    }

    private fun getFormattedUrl(url: String, index: Int): String {
        val mUrl = getSiteUrl(url)
        return mUrl + Constant.END_PREFIX.get(index)

    }

    sealed class State {
        data class OnPodcastSearchSuccess(val podcastResult: PodcastResponse) : State()
    }
}