package com.life4.flowpod.features.usersources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.flowpod.models.source.RssFeedResponse
import com.life4.flowpod.models.source.RssFeedResponseItem
import com.life4.flowpod.models.source.SourceModel
import com.life4.flowpod.room.source.SourceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSourcesViewModel @Inject constructor(private val sourceDao: SourceDao) : BaseViewModel() {

    val userSources = MutableLiveData<SourceModel>()

    private val _liveData = MutableLiveData<State>()
    val liveData: LiveData<State>
        get() = _liveData

    fun getUserResources(): LiveData<SourceModel> {
        return sourceDao.getSources()
    }

    fun deleteSource(item: RssFeedResponseItem) {
        val list = arrayListOf<RssFeedResponseItem>()
        userSources.value?.sourceList?.sourceList?.let { list.addAll(it) }
        list.remove(item)
        viewModelScope.launch {
            sourceDao.deleteSources()
            sourceDao.insertSource(SourceModel(sourceList = RssFeedResponse(list)))
            _liveData.value = State.OnItemRemoved(item)
        }
    }

    sealed class State {
        data class OnItemRemoved(val item: RssFeedResponseItem) : State()
    }
}