package com.maruchan.myclass.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.text
import com.crocodic.core.extension.toJson
import com.crocodic.core.helper.ImagePreviewHelper
import com.google.gson.Gson
import com.maruchan.myclass.R
import com.maruchan.myclass.base.BaseActivity
import com.maruchan.myclass.data.constant.Const
import com.maruchan.myclass.data.list.ListFriends
import com.maruchan.myclass.databinding.ActivityDetailFriendsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFriendsActivity :
    BaseActivity<ActivityDetailFriendsBinding, DetailFriendsViewModel>(R.layout.activity_detail_friends) {
    private var friend: ListFriends? = null
    private var usersId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        initClick()

//        val data = intent.getParcelableExtra<ListFriends>(Const.LIST.FRIENDS)
//        binding.detail = data
//        friend = data
//        usersId = data?.user_id


        usersId = intent.getIntExtra(Const.ID, 0)
        viewModel.getUserId(usersId ?: return)


    }

    private fun initClick() {
        binding.imgProfileDetail.setOnClickListener {
            ImagePreviewHelper(this).show(binding.imgProfileDetail, binding.user?.foto)
        }

        binding.ivBackDetail.setOnClickListener {
            finish()
        }

        binding.btnColek.setOnClickListener {
            usersId?.let { it1 -> viewModel.getNotify(it1) }
        }

        binding.btnLike.setOnClickListener {
            getLiked()
        }

        binding.btnChatWhatsapp.setOnClickListener {
            val nomorHp = binding.tvPhoneDetail.text.toString().substring(1)
            if (nomorHp.isEmpty()) {
                Toast.makeText(
                    this@DetailFriendsActivity, getString(R.string.label_must_fill),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                whatsapp("+62$nomorHp")
            }
        }

    }


    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.saveListSekolah.collect {
                        binding.tvSchoolDetail.text(it.sekolah)
                    }

                }
                launch {
                    viewModel.apiResponse.collect {
                        if (it.status == ApiStatus.LOADING) {
                        } else if (it.status == ApiStatus.SUCCESS) {
                            friend?.like_by_you = it.message == "liked"
                            binding.user = friend
                            Log.d("cek friends", "cek friends : $friend")
                            friend?.sekolah_id?.let { getListSchool(it.toInt()) }

                        } else if (it.status == ApiStatus.ERROR) {
                            loadingDialog.setResponse(it.message ?: return@collect)

                        }
                    }
                }
                launch {
                    viewModel.getProfile.collect { friends ->
                        friend = friends
                        binding.user = friend
                        Log.d("cek data", "cek data : $friends")

                        friend?.sekolah_id?.let { getListSchool(it.toInt()) }
                    }
                }

            }

        }
    }

    private fun getNotify() {
    }

    /* private fun getNotify() {
         friend?.device_token?.let {
             val listFriends = session.getUser()
             listFriends?.nama?.let { nameFriend ->
                 viewModel.getNotify(
                     to = it,
                     title = nameFriend,
                     body = "Telah mencolek anda",
                     userId = session.getUser()?.user_id.toString()
                 )
             }
         }
     }*/

    private fun getListSchool(id: Int) {
        viewModel.getListSchool(id)

    }

    private fun getLiked() {
        val friendId = friend?.user_id
        if (friendId != null) {
            if (friend?.like_by_you == true) {
                viewModel.unLiked(friendId)
                setResult(Const.LIST.RELOAD)
            } else {
                viewModel.liked(friendId)
                setResult(Const.LIST.RELOAD)
            }
        }
    }

    private fun whatsapp(number: String) {
        val intentUri = Uri.parse("https://api.whatsapp.com/send?phone=" + number)
        val waIntent = Intent(Intent.ACTION_VIEW)
        waIntent.setData(intentUri)
        startActivity(waIntent)
    }
}
