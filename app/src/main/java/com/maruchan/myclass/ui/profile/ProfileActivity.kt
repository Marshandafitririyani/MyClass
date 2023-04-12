package com.maruchan.myclass.ui.profile

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.text
import com.maruchan.myclass.R
import com.maruchan.myclass.base.BaseActivity
import com.maruchan.myclass.data.session.Session
import com.maruchan.myclass.databinding.ActivityProfileBinding
import com.maruchan.myclass.ui.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {

    @Inject
    lateinit var CoreSession: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()

        val user = session.getUser()
        if (user != null) {
            binding.user = user
        }

        binding.ivBackProfile.setOnClickListener {
            finish()
        }

        getlistSekolah(user?.user_id)

        binding.btnLogoutProfile.setOnClickListener {
            viewModel.logout()
        }

        /*  binding.btnLogoutProfile.setOnClickListener {
            val builder = AlertDialog.Builder(this@ProfileActivity)
            builder.setMessage("Apakah Anda Ingin Menghapus Produk")
                .setCancelable(false)
                //setPositive button (tombol untuk iya)
                .setPositiveButton("Iya") { dialog, id ->
                    binding.root.snacked("Product Removed")
                }
                //setNegative Button Tombol untuk tidak
                .setNegativeButton("Tidak") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
           alert.show()
        }*/

        /* binding.btnEditPasswordProfile.setOnClickListener{
            val dialogBinding = layoutInflater.inflate(R.layout.popup_edit_password,null)
            val myDialog = Dialog(this@ProfileActivity)
            myDialog.setContentView(dialogBinding)

            myDialog.setCancelable(true)
            myDialog.window?.setBackgroundDrawableResource(R.color.transparent)

            val textView = dialogBinding.findViewById<TextView>(R.id.btn_save_password)
            textView.setOnClickListener {
                binding.root.snacked("buttonBisa")
            }
            myDialog.show()
        }*/

        /* binding.ivImageEditProfil.setOnClickListener {
            val kembali = Intent(this, ProfileActivity::class.java).apply {
                putExtra("photoFile", binding?.user?.image)
                putExtra("username", binding?.user?.name)
                putExtra("nomorTelepon", binding?.user?.nomorTelepon)
            }
            startActivity(kembali)
        }*/

        /*lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> {
                                loadingDialog.show()
                            }
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<SplashActivity> {
                                    finishAffinity()

                                }
                            }
                            ApiStatus.ERROR -> {
                                loadingDialog.setResponse(it.message ?: return@collect)
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }

                }
            }
        }*/}
        private fun observe() {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {

                    launch {
                        viewModel.apiResponse.collect {
                            when (it.status) {
                                ApiStatus.SUCCESS -> {
                                    loadingDialog.dismiss()
                                    binding.user = session.getUser()
                                }
                                else -> loadingDialog.setResponse(it.message ?: return@collect)
                            }

                        }
                    }
                    launch {
                        viewModel.apiResponse.collect {
                            when (it.status) {
                                ApiStatus.LOADING -> {
                                    loadingDialog.show()
                                }
                                ApiStatus.SUCCESS -> {
                                    loadingDialog.dismiss()
                                    openActivity<SplashActivity> {
                                        finishAffinity()

                                    }
                                }
                                ApiStatus.ERROR -> {
                                    loadingDialog.setResponse(it.message ?: return@collect)
                                }
                                else -> loadingDialog.setResponse(it.message ?: return@collect)
                            }
                        }

                    }


                    launch {
                        viewModel.saveListSekolah.collect{
                            binding.tvSchoolProfile.text(it.sekolah)
                        }


                    }
                }
            }
        }
    private fun getlistSekolah(id:Int?) {
        id?.let {
            viewModel.getListSekolah(id)
        }

    }
    }
