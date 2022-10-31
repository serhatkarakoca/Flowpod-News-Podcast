package com.life4.feedz.features.source

import com.life4.core.core.vm.BaseViewModel
import com.life4.feedz.remote.source.SourceRepository
import com.life4.feedz.room.source.SourceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SourceViewModel @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val sourceDao: SourceDao
) : BaseViewModel() {

}