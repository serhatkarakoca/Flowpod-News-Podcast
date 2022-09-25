package com.life4.feedz.features.home

import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.remote.FeedzRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedzRepository: FeedzRepository
) : BaseViewModel() {

    fun getSiteData(url: String) {
            feedzRepository.getSiteData(url).handle(requestType = RequestType.ACTION, onComplete = {

            })
    }
}
