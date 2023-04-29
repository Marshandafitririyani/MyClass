package com.maruchan.myclass.ui.home

import android.Manifest
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
import com.crocodic.core.extension.tos
import com.maruchan.myclass.R
import com.maruchan.myclass.base.BaseActivity
import com.maruchan.myclass.data.constant.Const
import com.maruchan.myclass.data.list.ListFriends
import com.maruchan.myclass.databinding.ActivityHomeBinding
import com.maruchan.myclass.databinding.ItemFriendsBinding
import com.maruchan.myclass.ui.detail.DetailFriendsActivity
import com.maruchan.myclass.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {


    private val friends = ArrayList<ListFriends?>()
    private val friendsAll = ArrayList<ListFriends?>()

    private val adapterFriends by lazy {
        ReactiveListAdapter<ItemFriendsBinding, ListFriends>(R.layout.item_friends).initItem { position, data ->
            activityLauncher.launch(createIntent<DetailFriendsActivity> { // TODO: pakai activitylauncher untuk memeriksa result code dari activity yang akan di open
                putExtra(Const.LIST.FRIENDS, data)
            }) {
                if (it.resultCode == Const.LIST.RELOAD) { // TODO: cek result code apakah harus reload
                    viewModel.getListFriend()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        search()
        getUser()
        adapter()
        getListFriend()
        askNotificationPermission()
        observe()
        initClick()

        //TODO: get user
        val user = session.getUser()
        if (user != null) {
            binding.data = user

        }


    }

    private fun initClick() {

        binding.profileHome.setOnClickListener {
            activityLauncher.launch(createIntent<ProfileActivity>()) {
                if (it.resultCode == 12345) {
                    getUser()
                }
            }
        }

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

    private fun adapter() {
        binding.recyclerViewHome.adapter = adapterFriends
    }


    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //TODO: untuk get usernya
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
                    //TODO: untuk fungsi swipeRefreshLayout dan search serta empty
                    viewModel.responseSave.collect { Friends ->
                        binding.swipeRefreshLayout.isRefreshing = false
                        Log.d("data produk", "cek ${Friends}")
                        friends.clear()
                        friendsAll.clear()
                        friendsAll.addAll(Friends)
                        friends.addAll(Friends)
                        adapterFriends.submitList(friends)
                        adapterFriends.submitList(Friends)
                        if (friends.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                        }

                    }

                }
            }
        }
    }

    private fun getUser() {
        viewModel.getUser()
    }

    private fun getListFriend() {
        viewModel.getListFriend()
    }

    //TODO:notofication
    //TODO:untuk menunjukan ijin atau tidak dari askNotificationPermission
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            //TODO: FCM SDK (and your app) can post notifications.
            tos("Permission Granted")
            //todo:boleh
        } else {
            // TODO: Inform user that that your app will not show notifications.
            tos("Permission Denied")
            //todo:tidak boleh
        }
    }

    //TODO: menunjukan aksinya
    private fun askNotificationPermission() {
        //TODO: this is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                //TODO: FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // TODO: directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
