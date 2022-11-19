package com.life4.flowpod.features.login

import androidx.lifecycle.MutableLiveData
import com.life4.core.core.vm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {
    val signInMode = MutableLiveData<Boolean>(true)

}