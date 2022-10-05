package com.life4.feedz.features.newsdetails

import com.life4.core.core.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor() : BaseViewModel() {
    var args: NewsDetailsFragmentArgs? = null
}