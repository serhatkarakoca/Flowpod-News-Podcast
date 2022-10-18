package com.life4.feedz.features.saved

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.room.SavedNews
import com.life4.feedz.models.rss_.RssPaginationItem
import com.life4.feedz.room.news.NewsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedNewsViewModel @Inject constructor(val newsDao: NewsDao) : BaseViewModel() {

    private var job: Job? = null

    private val _newsList = MutableLiveData<List<RssPaginationItem>>()
    val newsList: LiveData<List<RssPaginationItem>>
        get() = _newsList

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    val savedNews = MutableLiveData<List<SavedNews>>()

    fun getAllSavedNews() {
        val itemList = savedNews.value?.mapNotNull { it.newsItem } ?: listOf()
        _newsList.value = itemList
        savedNews.value = savedNews.value
        _state.value = State.OnNewsFetched(itemList)
    }

    fun deleteSavedNews(item: RssPaginationItem) {
        _newsList.value?.firstOrNull { it == item }?.let {
            val itemList = arrayListOf<RssPaginationItem>()
            itemList.addAll(newsList.value ?: listOf())
            itemList.remove(it)
            _newsList.value = itemList
            _state.value = State.OnNewsFetched(itemList)
        }

        savedNews.value?.firstOrNull { it.newsItem == item }?.let {
            viewModelScope.launch {
                newsDao.deleteSavedNews(it.id)
            }
        }
    }

    sealed class State {
        data class OnNewsFetched(val list: List<RssPaginationItem>) : State()
    }
}