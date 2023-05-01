package com.maruchan.myclass.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.text
import com.crocodic.core.helper.ImagePreviewHelper
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

        //TODO: untuk getParcelableExtra mengambil data dari FRIENDS
        val data = intent.getParcelableExtra<ListFriends>(Const.LIST.FRIENDS)
        binding.detail = data
        friend = data

        //TODO: untuk tranformasi dari Sekolah_id ke nama sekolanya
        data?.sekolah_id?.let { getListSchool(it.toInt()) }

        //TODO: untuk getParcelableExtra mengambil data dari ID
        val userId = intent.getStringExtra(Const.ID)

        userId?.let {
            usersId?.let { it1 -> viewModel.getUserId(it1) }
        }

    }

    private fun initClick() {
        binding.imgProfileDetail.setOnClickListener {
            ImagePreviewHelper(this).show(binding.imgProfileDetail, binding.detail?.foto)
        }

        binding.ivBackDetail.setOnClickListener {
            finish()
        }

        binding.btnColek.setOnClickListener {
            getNotify()
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
                //TODO:untuk mengubah sekolah_id menjadi sekolah_nama
                launch {
                    viewModel.saveListSekolah.collect {
                        binding.tvSekolahDetail.text(it.sekolah)
                    }


                }
                //TODO: untuk collect like dan unlikenya
                launch {
                    viewModel.apiResponse.collect {
                        if (it.status == ApiStatus.LOADING) {
                        } else if (it.status == ApiStatus.SUCCESS) {
                            when (it.message) {
                                "liked" -> {
                                    binding.detail = friend?.copy(like_by_you = true)
                                    friend?.sekolah_id?.let { getListSchool(it.toInt()) }
                                    loadingDialog.dismiss()
                                }
                                "unLiked" -> {
                                    binding.detail = friend?.copy(like_by_you = false)
                                    friend?.sekolah_id?.let { getListSchool(it.toInt()) }
                                    loadingDialog.dismiss()
                                }
                            }
                        } else if (it.status == ApiStatus.ERROR) {
                            loadingDialog.setResponse(it.message ?: return@collect)

                        }
                    }
                }

            }
            // TODO: collect dari api getprofile
            launch {
                viewModel.getProfile.collect { getProfile ->
                    binding.detail = getProfile
                    usersId = getProfile.user_id
                }
            }
        }
    }

    private fun getNotify() {
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
    }

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
