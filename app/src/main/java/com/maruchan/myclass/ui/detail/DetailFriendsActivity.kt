package com.maruchan.myclass.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.text
import com.crocodic.core.extension.tos
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        val data = intent.getParcelableExtra<ListFriends>(Const.LIST.FRIENDS)
        binding.detail = data

        data?.sekolah_id?.let { getlistSekolah(it.toInt()) }

        binding.ivBackDetail.setOnClickListener {
            finish()
        }
        binding.btnColek.setOnClickListener {
            tos("colek")
        }
        binding.btnChatWhatsapp.setOnClickListener {
            tos("wa")
        }

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
                    viewModel.saveListSekolah.collect {
                        binding.tvSekolahDetail.text(it.sekolah)
                    }

                }


            }
        }
    }

    private fun getlistSekolah(id: Int) {

            viewModel.getListSekolah(id)

    }
}