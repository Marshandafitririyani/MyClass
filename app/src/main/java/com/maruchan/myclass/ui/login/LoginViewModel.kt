package com.maruchan.myclass.ui.login

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import com.maruchan.myclass.api.ApiService
import com.maruchan.myclass.base.BaseViewModel
import com.maruchan.myclass.data.constant.Const
import com.maruchan.myclass.data.room.user.User
import com.maruchan.myclass.data.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session

) : BaseViewModel() {

    private val _responseAPI = MutableSharedFlow<ApiResponse>()
    val responseAPI = _responseAPI.asSharedFlow()

    fun login(phone: String, password: String, deviceToken: String) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.login(phone, password, deviceToken) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val token = response.getJSONObject("data").getJSONObject("token").getString("access_token")
                    session.setValue(Const.TOKEN.API_TOKEN, token)
                    _apiResponse.emit(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }

}