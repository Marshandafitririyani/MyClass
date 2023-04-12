package com.maruchan.myclass.ui.home

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import com.maruchan.myclass.api.ApiService
import com.maruchan.myclass.base.BaseViewModel
import com.maruchan.myclass.data.list.ListFriends
import com.maruchan.myclass.data.list.ListSchool
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
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session

) : BaseViewModel(){
    private val _responseAPI = MutableSharedFlow<ApiResponse>()
    val responseAPI = _responseAPI.asSharedFlow()

    private val _responseSave = MutableSharedFlow<List<ListFriends>>()
    val responseSave = _responseSave.asSharedFlow()

    private val _saveListSekolah = MutableSharedFlow<List<ListSchool>>()
    val saveListSekolah = _saveListSekolah.asSharedFlow()

    fun getToken(
    ) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.getToken() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
//                    val status = response.getInt(ApiCode.STATUS)
                    val data = response.toObject<User>(gson)
                    session.saveUser(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            }
        )
    }
    fun getListFriend() = viewModelScope.launch {
        ApiObserver(
            { apiService.getListFriend() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray(ApiCode.DATA).toList<ListFriends>(gson)
                    _responseSave.emit(data)
                    Timber.d("cek api ${data.size}")
                }
            }
        )
    }
/*    fun getListSekolah() = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.getListSekolah() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<ListSekolah>(gson)

//                    val schooll = data.filter { it.sekolah_id == id}
//                    val school = data.last { it.sekolah_id == id }
                    _saveListSekolah.emit(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                }
            }
        )
    }*/

}