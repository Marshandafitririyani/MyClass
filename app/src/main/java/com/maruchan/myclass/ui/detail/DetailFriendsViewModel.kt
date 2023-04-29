package com.maruchan.myclass.ui.detail

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.google.gson.Gson
import com.maruchan.myclass.api.ApiService
import com.maruchan.myclass.base.BaseViewModel
import com.maruchan.myclass.data.list.ListSchool
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
    //TODO: untuk list sekolah
    private val _saveListSekolah = MutableSharedFlow<ListSchool>()
    val saveListSekolah = _saveListSekolah.asSharedFlow()

    //TODO: untuk notifikasi
    private val _getNotifSave = MutableSharedFlow<ApiResponse>()
    val getNotifSave = _getNotifSave.asSharedFlow()

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

    fun getNotify(to: String, title: String, body: String) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.getNotify(to, title, body) },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _getNotifSave.emit(ApiResponse().responseSuccess())
                }

            })
    }
}