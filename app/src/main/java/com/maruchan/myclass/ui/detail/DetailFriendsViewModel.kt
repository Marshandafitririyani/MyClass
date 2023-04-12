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
import javax.inject.Inject

@HiltViewModel
class DetailFriendsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session

) : BaseViewModel(){
    private val _saveListSekolah = MutableSharedFlow<ListSchool>()
    val saveListSekolah = _saveListSekolah.asSharedFlow()

    fun getListSekolah(id:Int) = viewModelScope.launch {
        _apiResponse.emit(ApiResponse().responseLoading())
        ApiObserver(
            { apiService.getListSekolah() },
            false,
            object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONArray("data").toList<ListSchool>(gson)

//                    val school = data.filter { it.sekolah_id == id}
                    val school = data.last { it.sekolah_id == id }
                    _saveListSekolah.emit(school)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                }
            }
        )
    }
}