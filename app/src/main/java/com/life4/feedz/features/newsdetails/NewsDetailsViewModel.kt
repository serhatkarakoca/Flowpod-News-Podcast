package com.life4.feedz.features.newsdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.room.SavedNews
import com.life4.feedz.models.rss_.RssPaginationItem
import com.life4.feedz.room.news.NewsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor(private val newsDao: NewsDao) : BaseViewModel() {
    var args: NewsDetailsFragmentArgs? = null

    val savedNews = MutableLiveData<List<SavedNews>>()


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