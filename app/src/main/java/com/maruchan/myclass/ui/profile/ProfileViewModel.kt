package com.maruchan.myclass.ui.profile

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toList
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import com.maruchan.myclass.api.ApiService
import com.maruchan.myclass.base.BaseViewModel
import com.maruchan.myclass.data.list.ListSchool
import com.maruchan.myclass.data.list.ListSchoolTwo
import com.maruchan.myclass.data.room.user.User
import com.maruchan.myclass.data.session.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session

) : BaseViewModel() {
    private val _user = kotlinx.coroutines.channels.Channel<List<User>>()
    val user = _user.receiveAsFlow()
    val getUser = session.getUser()

    private val _saveListSchool = MutableSharedFlow<ListSchool>()
    val saveListSekolah = _saveListSchool.asSharedFlow()

    private val _saveListSekolahPopup = MutableSharedFlow<List<ListSchoolTwo>>()
    val saveListSekolahPopup = _saveListSekolahPopup.asSharedFlow()

    private val _responseAPI = MutableSharedFlow<ApiResponse>()
    val responseAPI = _responseAPI.asSharedFlow()

    private val _editProfile = MutableSharedFlow<ApiResponse>()
    val editProfile = _editProfile.asSharedFlow()

    private val _editProfileWithPhoto = MutableSharedFlow<ApiResponse>()
    val editProfileWithPhoto = _editProfileWithPhoto.asSharedFlow()

    private val _saveUserGetProfile = MutableSharedFlow<User>()
    val saveUserGetProfile = _saveUserGetProfile.asSharedFlow()


    fun getUser(
    ) = viewModelScope.launch {
        ApiObserver(
            { apiService.getUserToken() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    session.saveUser(data)
                    _saveUserGetProfile.emit(data)
                }
            }
        )
    }

    fun getListSekolah(id: Int) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.getListSchool() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<ListSchool>(gson)

                    val school = data.last { it.sekolah_id == id }
                    _saveListSchool.emit(school)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                }
            }
        )
    }

    fun getListSchoolEdit() = viewModelScope.launch {
        ApiObserver({ apiService.getListSchool() }, false, object : ApiObserver.ResponseListener {
            override suspend fun onSuccess(response: JSONObject) {
                val status = response.getInt(ApiCode.STATUS)
                if (status == ApiCode.SUCCESS) {

                    val data = response.getJSONArray(ApiCode.DATA).toList<ListSchoolTwo>(gson)
                    _saveListSekolahPopup.emit(data)

                } else {
                    val message = response.getString(ApiCode.MESSAGE)
                }
            }
        })
    }


    fun editProfile(name: String, schoolId: Int?) = viewModelScope.launch {
        _editProfile.emit(ApiResponse().responseLoading())
        ApiObserver({
            apiService.editProfile(name, schoolId)
        }, false, object : ApiObserver.ResponseListener {
            override suspend fun onSuccess(response: JSONObject) {
                val status = response.getInt(ApiCode.STATUS)
                val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                _editProfile.emit(ApiResponse().responseSuccess("Profile Updated"))
                session.saveUser(data)
                if (status == ApiCode.SUCCESS) {

                }
            }

            override suspend fun onError(response: ApiResponse) {
                super.onError(response)
                _editProfile.emit(ApiResponse().responseError())
            }
        })
    }

    fun editWithPhoto(name: String, schoolId: Int?, photo: File) =
        viewModelScope.launch {
            val fileBody = photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("foto", photo.name, fileBody)
            _apiResponse.emit(ApiResponse().responseLoading())
            ApiObserver({ apiService.editProfileWithPhoto(name, schoolId, filePart) },
                false, object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        _editProfileWithPhoto.emit(ApiResponse().responseSuccess("Profile Updated"))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _editProfileWithPhoto.emit(ApiResponse().responseError())

                    }
                })
        }

    fun logout() = viewModelScope.launch {
        ApiObserver({ apiService.logout() },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _apiResponse.emit(ApiResponse().responseSuccess("Logout Success"))
                    session.clearAll()
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            }
        )
    }

    fun editPassword(current_password: String?, new_password: String?) = viewModelScope.launch {
        ApiObserver({ apiService.editPassword(current_password, new_password) },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    _editProfile.emit(ApiResponse().responseSuccess("Profile Updated"))
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            }
        )
    }

}