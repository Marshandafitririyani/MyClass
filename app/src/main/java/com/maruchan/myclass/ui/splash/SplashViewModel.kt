package com.maruchan.myclass.ui.splash

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.maruchan.myclass.api.ApiService
import com.maruchan.myclass.base.BaseViewModel
import com.maruchan.myclass.data.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : BaseViewModel() {
    fun splash(done: () -> Unit) = viewModelScope.launch {
        delay(2000)
        done()
    }
}