package com.maruchan.myclass.ui.register

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.toList
import com.google.gson.Gson
import com.maruchan.myclass.api.ApiService
import com.maruchan.myclass.base.BaseViewModel
import com.maruchan.myclass.data.list.ListSchool
import com.maruchan.myclass.data.list.ListSchoolTwo
import com.maruchan.myclass.data.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session

) : BaseViewModel(){
    private val _responseAPI = MutableSharedFlow<ApiResponse>()
    val responseAPI = _responseAPI.asSharedFlow()

    private val _saveListSekolah = MutableSharedFlow<List<ListSchoolTwo>>()
    val saveListSekolah = _saveListSekolah.asSharedFlow()

        fun register(name: String, phone: String, school: Int?, password: String) = viewModelScope.launch {
            _apiResponse.emit(ApiResponse().responseLoading())
            ApiObserver(
                { apiService.register(name, phone,school, password) },
                false,
                object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        _apiResponse.emit(ApiResponse().responseSuccess())
                        val message = response.getString(ApiCode.MESSAGE)
                        _apiResponse.emit(ApiResponse(status = ApiStatus.SUCCESS, message = message))
                    }

                })
        }

/*    fun getListSekolah() = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.getListSekolah() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<ListSchool>(gson)
                    _saveListSekolah.emit(data)

//                    val school = data.filter { it.sekolah_id == id}
                *//*    val school = data.last { it.sekolah_id == id }
                    _saveListSekolah.emit(school)*//*
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                }
            }
        )
    }*/
    fun getListSekolah() = viewModelScope.launch {
        ApiObserver({ apiService.getListSekolah() }, false, object : ApiObserver.ResponseListener {
            override suspend fun onSuccess(response: JSONObject) {
                val status = response.getInt(ApiCode.STATUS)
                if (status == ApiCode.SUCCESS) {

                    val data = response.getJSONArray(ApiCode.DATA).toList<ListSchoolTwo>(gson)
                    _saveListSekolah.emit(data)

                } else {
                    val message = response.getString(ApiCode.MESSAGE)
                }
            }
        })
    }
}