package com.maruchan.myclass.ui.detail

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
class DetailFriendsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session

) : BaseViewModel() {
    private val _saveListSekolah = MutableSharedFlow<ListSchool>()
    val saveListSekolah = _saveListSekolah.asSharedFlow()

    private val _getNotifSave = MutableSharedFlow<ApiResponse>()
    val getNotifSave = _getNotifSave.asSharedFlow()

    private var _getProfile = MutableSharedFlow<ListFriends>()
    var getProfile = _getProfile.asSharedFlow()


    fun getListSchool(id: Int) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.getListSchool() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<ListSchool>(gson)

                    val school = data.last { it.sekolah_id == id }
                    _saveListSekolah.emit(school)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                }
            }
        )
    }

    fun liked(id: Int) = viewModelScope.launch {
        ApiObserver(
            { apiService.liked(id) },
            true,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess("liked"))
                    Timber.d("cek api like $response")
                }
            }
        )

    }

    fun unLiked(id: Int) = viewModelScope.launch {
        ApiObserver(
            { apiService.unLiked(id) },
            true,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess("unLiked"))
                    Timber.d("cek api like $response")
                }
            }
        )

    }

    fun getNotify(userId: Int) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.getNotify(userId) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess())
                }

            })
    }

    fun getUserId(
        id: Int
    ) = viewModelScope.launch {
        ApiObserver(
            { apiService.getUserId(id) },
            false,
            object : ApiObserver.ResponseListener {

                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<ListFriends>(gson)
                    _getProfile.emit(data)
                    _apiResponse.emit(ApiResponse().responseSuccess())

                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())

                }

            }
        )
    }
}