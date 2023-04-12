package com.maruchan.myclass.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.openActivity
import com.maruchan.myclass.R
import com.maruchan.myclass.base.BaseActivity
import com.maruchan.myclass.data.constant.Const
import com.maruchan.myclass.data.list.ListFriends
import com.maruchan.myclass.data.list.ListSchool
import com.maruchan.myclass.databinding.ActivityHomeBinding
import com.maruchan.myclass.databinding.ItemFriendsBinding
import com.maruchan.myclass.ui.detail.DetailFriendsActivity
import com.maruchan.myclass.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    private val tokenll = Const.TOKEN.API_TOKEN
    private val friends = ArrayList<ListFriends?>()
    private val procuctAll = ArrayList<ListFriends?>()
    private val listSchool = ArrayList<ListSchool>()

    private val adapterFriends by lazy {
        ReactiveListAdapter<ItemFriendsBinding, ListFriends>(R.layout.item_friends).initItem { position, data ->
            val detailIntent = Intent(this, DetailFriendsActivity::class.java).apply {
                putExtra(Const.LIST.FRIENDS, data)
            }
            startActivity(detailIntent)
        }
    }

    /*private val adapterFriends by lazy {
        object : ReactiveListAdapter<ItemFriendsBinding, ListFriends>(R.layout.item_friends){
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemFriendsBinding, ListFriends>,
                position: Int
            ) {
                val data = friends[position]
                holder.binding.data = data
                val school = friends.last { it?.sekolah_id == data?.sekolah_id }

                holder.binding.tvSchoolItemFriends.text= school.toString()

            }
        }.initItem()
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        getlistSekolah()
        getToken()
        adapter()
        getListFriend()
        observe()


        val user = session.getUser()
        if (user != null) {
            binding.data = user
        }

        /*getlistSekolah(user?.user_id)*/

        binding.profileHome.setOnClickListener {
            openActivity<ProfileActivity>()
        }
        /*binding.ivMyClass.setOnClickListener {
            openActivity<DetailFriendsActivity>()
        }*/

    }

    private fun adapter() {
        binding.recyclerViewHome.adapter = adapterFriends
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                binding.data = session.getUser()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
                launch {
                    viewModel.responseSave.collect { Friends ->
                        Log.d("data produk", "cek ${Friends}")
                        adapterFriends.submitList(Friends)
                    }
                }
                /* launch {
                     viewModel.saveListSekolah.collect{ listSekolah ->
                         listSchool.addAll(listSekolah)
                     }
                 }*/
            }
        }
    }

    private fun getToken() {
        viewModel.getToken()
    }

    private fun getListFriend() {
        viewModel.getListFriend()
    }
    /* private fun getlistSekolah() {
             viewModel.getListSekolah()
         }*/
}
