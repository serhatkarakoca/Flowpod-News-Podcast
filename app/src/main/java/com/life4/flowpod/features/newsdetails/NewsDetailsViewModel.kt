package com.life4.flowpod.features.newsdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.flowpod.data.MyPreference
import com.life4.flowpod.models.room.SavedNews
import com.life4.flowpod.models.rss_.RssPaginationItem
import com.life4.flowpod.room.news.NewsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor(
    private val newsDao: NewsDao,
    private val myPref: MyPreference
) : BaseViewModel() {
    var args: NewsDetailsFragmentArgs? = null

    val savedNews = MutableLiveData<List<SavedNews>>()

    fun isLogin(): Boolean {
        return myPref.getUsername() != null
    }

    fun saveNews(item: RssPaginationItem, onComplete: (Boolean) -> Unit) {
        val news = SavedNews(newsItem = item)
        val daoItem = savedNews.value?.firstOrNull { it.newsItem?.title == item.title }
        viewModelScope.launch {
            if (daoItem != null && !item.isFavorite) {
                newsDao.deleteSavedNews(daoItem.id)
                onComplete.invoke(false)
            } else if (item.isFavorite && daoItem == null) {
                newsDao.insertSavedNews(news)
                onComplete.invoke(true)
            } else onComplete.invoke(true)
        }
    }

    fun getAllSavedNews(): Flow<List<SavedNews>> {
        return newsDao.getAllSavedNews()
    }
}