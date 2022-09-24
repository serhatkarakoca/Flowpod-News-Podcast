package com.life4.feedz.features.home

import androidx.lifecycle.viewModelScope
import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.remote.FeedzRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedzRepository: FeedzRepository
) : BaseViewModel() {

    fun getSiteData(url: String) {
        viewModelScope.launch {
            feedzRepository.getSiteData(url)

        }
    }
}
