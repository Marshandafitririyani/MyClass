package com.maruchan.myclass.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.crocodic.core.extension.createIntent
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.tos
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
import timber.log.Timber

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    private val tokenll = Const.TOKEN.API_TOKEN
    private val friends = ArrayList<ListFriends?>()
    private val friendsAll = ArrayList<ListFriends?>()
    private val listSchool = ArrayList<ListSchool>()

    private val adapterFriends by lazy {
        ReactiveListAdapter<ItemFriendsBinding, ListFriends>(R.layout.item_friends).initItem { position, data ->
            activityLauncher.launch(createIntent<DetailFriendsActivity> { // TODO: pakai activitylauncher untuk memeriksa result code dari activity yang akan di open
                putExtra(Const.LIST.FRIENDS, data)
            }) {
                if (it.resultCode == Const.LIST.RELOAD) { // TODO: cek result code apakah harus reload
                   viewModel.getListFriend()
                }
            }


           /* val detailIntent = Intent(this, DetailFriendsActivity::class.java).apply {
                putExtra(Const.LIST.FRIENDS, data)
            }
            startActivity(detailIntent)*/
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

        search()
        getToken()
        adapter()
        getListFriend()
        askNotificationPermission()
        observe()


        val user = session.getUser()
        if (user != null) {
            binding.data = user
        }


        /*getlistSekolah(user?.user_id)*/


        binding.profileHome.setOnClickListener {
            openActivity<ProfileActivity>()
        }
        /*      binding.ivMyClass.setOnClickListener {

              }
      */

        binding.swipeRefreshLayout.setOnRefreshListener {
            getListFriend()
        }

    }

    private fun search() {
        binding.etSearchHome.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                val filter = friendsAll.filter { it?.nama?.contains("$text", true) == true }
                adapterFriends.submitList(filter)

                if (filter.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                }
            } else {
                adapterFriends.submitList(friendsAll)
                binding.tvEmpty.visibility = View.GONE
            }

        }
    }

    /* private fun search() {
         binding.etSearchHome.doOnTextChanged { text, start, before, count ->
             if (text.isNullOrEmpty()) {
                 friends.clear()
                 binding.recyclerViewHome.adapter?.notifyDataSetChanged()
                 friends.addAll(friendsAll)
                 binding.recyclerViewHome.adapter?.notifyItemInserted(0)
             } else {
                 val filter = friendsAll.filter { it?.nama?.contains("$text", true) == true }
                 friends.clear()
                 filter.forEach {
                     friends.add(it)
                 }
                 binding.recyclerViewHome.adapter?.notifyDataSetChanged()
                 binding.recyclerViewHome.adapter?.notifyItemInserted(0)
             }
            *//* if (friends.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.tvEmpty.visibility = View.GONE
            }*//*
        }
    }*/


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
                        binding.swipeRefreshLayout.isRefreshing = false
                        Log.d("data produk", "cek ${Friends}")
                        friends.clear()
                        friendsAll.clear()
//                        adapterFriends.submitList(friends)
                        friendsAll.addAll(Friends)
                        friends.addAll(Friends)
                        adapterFriends.submitList(friends)
//                        adapterFriends.submitList(Friends)
                        if (friends.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                        }

                    }


                }
                 /*launch {
                     viewModel.responseAPI.collect {
                         friends.clear()
                         friends.addAll(it)
                         adapterFriends.submitList(friendsAll)
//                         binding.swipeRefreshLayout.isRefreshing = false


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

    //todo:notofication
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            tos("Permission Granted")
        } else {
            // TODO: Inform user that that your app will not show notifications.
            tos("Permission Denied")
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
/* private fun getlistSekolah() {
         viewModel.getListSekolah()
     }*/
}
