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
import com.crocodic.core.extension.tos
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
    private val friends = ArrayList<ListFriends?>()
    private var myUser: ListFriends? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        initClick()
//        initialButtonLike()


        val data = intent.getParcelableExtra<ListFriends>(Const.LIST.FRIENDS)
        binding.detail = data
        friend = data

        data?.sekolah_id?.let { getlistSekolah(it.toInt()) }

    }

    private fun initClick() {
        binding.imgProfileDetail.setOnClickListener {
            ImagePreviewHelper(this).show(binding.imgProfileDetail, myUser?.foto)
        }
        binding.ivBackDetail.setOnClickListener {
            finish()
        }
        binding.btnColek.setOnClickListener {
            getNotif()
            tos("colek")


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
        binding.btnLike.setOnClickListener {
            val friendId = friend?.user_id
            if (friendId != null) {
                if (friend?.like_by_you == true) {
                    viewModel.unLike(friendId)
                    setResult(Const.LIST.RELOAD)
                } else {
                    viewModel.like(friendId)
                    setResult(Const.LIST.RELOAD)

                }
            }


        }

        /* binding.btnFav.setOnClickListener {
             tos("Tour Liked")
             val friendId = friend?.id
             if (friendId != null) {
                 viewModel.like(friendId)
             }
         }

         binding.btnUnfav.setOnClickListener {
             tos("Tour Unliked")
             val friendId = friend?.id
             if (friendId != null) {
                 viewModel.like(friendId)
             }
         }*/
        /* binding.ivUnlike.setOnClickListener {
             val tourId = friend
              val friendId = friend?.id
             if (tourId != null) {
                 viewModel.like()
             }
         }*/
    }


    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.saveListSekolah.collect {
                        binding.tvSekolahDetail.text(it.sekolah)
                    }


                }
                launch {
                    viewModel.apiResponse.collect {
                        if (it.status == ApiStatus.LOADING) {
//                            loadingDialog.show(getString(R.string.label_must_fill))
                        } else if (it.status == ApiStatus.SUCCESS) {
                            when(it.message){
                                "liked"->{
                                    binding.detail = friend?.copy(like_by_you = true)
                                    friend?.sekolah_id?.let { getlistSekolah(it.toInt()) }
                                    loadingDialog.dismiss()
                                }
                                "unLiked"->{
                                    binding.detail = friend?.copy(like_by_you = false)
                                    friend?.sekolah_id?.let { getlistSekolah(it.toInt()) }
                                    loadingDialog.dismiss()
                                }
                            }
                            /*binding.detail = friend?.copy(like_by_you = true)
                            friend?.sekolah_id?.let { getlistSekolah(it.toInt()) }
                            loadingDialog.dismiss()*/

                        } else if (it.status == ApiStatus.ERROR) {
//                            val liked = it.dataAs<Boolean>()

//                            Log.d("like", "like:$liked")
                            loadingDialog.setResponse(it.message ?: return@collect)

                        }
                    }
                }


            }
        }
    }
    private fun getNotif(){
        friend?.device_token?.let { viewModel.getNotif(to= it,title = "Notification", body = "Hallo") }
    }

    private fun getlistSekolah(id: Int) {
        viewModel.getListSekolah(id)

    }

    private fun whatsapp(number: String) {
        val intentUri = Uri.parse("https://api.whatsapp.com/send?phone=" + number)
        val mapIntent = Intent(Intent.ACTION_VIEW)
        mapIntent.setData(intentUri)
        startActivity(mapIntent)
    }

   /* private fun getLike() {
        friend?.id?.let { viewModel.like(it) }

    }*/

    /* private fun initialButtonLike() {
         if (friend?.like!!.equals("true")) {
             binding.btnFav.visibility = View.GONE
             binding.btnUnfav.visibility = View.VISIBLE
         } else {
             binding.btnFav.visibility = View.VISIBLE
             binding.btnUnfav.visibility = View.GONE
         }
     }*/
}
