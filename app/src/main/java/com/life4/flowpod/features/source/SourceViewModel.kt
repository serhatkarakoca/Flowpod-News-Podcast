package com.life4.flowpod.features.source

import com.life4.core.core.vm.BaseViewModel
import com.life4.flowpod.remote.source.SourceRepository
import com.life4.flowpod.room.source.SourceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SourceViewModel @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val sourceDao: SourceDao
) : BaseViewModel() {

}