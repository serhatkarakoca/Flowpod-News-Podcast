package com.life4.feedz.features.flow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.models.Item
import com.life4.feedz.models.source.SourceModel
import com.life4.feedz.remote.FeedzRepository
import com.life4.feedz.room.source.SourceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlowViewModel @Inject constructor(
    private val feedzRepository: FeedzRepository,
    private val sourceDao: SourceDao
) : BaseViewModel() {

    val selectedCategory = MutableLiveData(0)

    val userSources = MutableLiveData<SourceModel>()

    private val siteDataList = arrayListOf<Item?>()

    private val _liveData = MutableLiveData<State>()
    val liveData: LiveData<State>
        get() = _liveData

    fun getSources(): LiveData<SourceModel> {
        return sourceDao.getSources()
    }

    init {
        selectedCategory.observeForever {
            getAndSetNews(it)
        }
    }

    fun getAndSetNews(category: Int? = 0) {
        siteDataList.clear()
        _liveData.value = State.OnNewsFetch(listOf())
        getNews(category)
    }

    fun getNews(category: Int? = 0) {
        viewModelScope.launch {
            if (category == 0) {
                userSources.value?.sourceList?.sourceList?.let { list ->
                    repeat(list.size) { index ->
                        getSiteData(list.get(index).siteUrl ?: "", list.lastIndex == index)
                    }
                }
            } else {
                userSources.value?.sourceList?.sourceList?.let { list ->
                    val filteredList = list.filter { it.categoryId == category }
                    if (filteredList.isEmpty())
                        _liveData.value = State.OnNewsFetch(listOf())
                    repeat(filteredList.size) { index ->
                        getSiteData(
                            filteredList.get(index).siteUrl ?: "",
                            filteredList.lastIndex == index
                        )
                    }
                }
            }
        }
    }

    private fun getSiteData(url: String, isLast: Boolean) {
        feedzRepository.getSiteData(url).handle(requestType = RequestType.ACTION, onComplete = {
            it.items?.let {
                siteDataList.addAll(it)
            }
            if (isLast)
                _liveData.value = State.OnNewsFetch(this@FlowViewModel.siteDataList ?: listOf())

        })
    }

    sealed class State {
        data class OnNewsFetch(val news: List<Item?>) : State()
    }
}