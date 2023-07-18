package com.maruchan.myclass.ui.login

import android.content.ContentValues
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.maruchan.myclass.R
import com.maruchan.myclass.base.BaseActivity
import com.maruchan.myclass.data.constant.Const
import com.maruchan.myclass.databinding.ActivityLoginBinding
import com.maruchan.myclass.ui.home.HomeActivity
import com.maruchan.myclass.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()


        binding.tvSignUpAccount.setOnClickListener {
            tvRegister()

        }

        binding.btnLogin.setOnClickListener {
            if (binding.etPhone.isEmptyRequired(R.string.label_must_fill) ||
                binding.etPassword.isEmptyRequired(R.string.label_must_fill)
            ) {
                return@setOnClickListener
            }
            val phone = binding.etPhone.textOf()
            val password = binding.etPassword.textOf()
            val deviceToken = session.getString(Const.TOKEN.DEVICETOKEN)

            viewModel.login(phone, password, deviceToken)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            val msg = getString(R.string.msg_token_fmt, token)
            session.setValue(Const.TOKEN.DEVICETOKEN, token)
        })


    }

    private fun tvRegister() {
        val spannableString = SpannableString("Don't have an account? Sign up")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                openActivity<RegisterActivity>()
            }
        }
        spannableString.setSpan(
            clickableSpan,
            22,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvSignUpAccount.text = spannableString
        binding.tvSignUpAccount.movementMethod =
            LinkMovementMethod.getInstance()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show(R.string.loading)
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<HomeActivity>()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                loadingDialog.setResponse(it.message ?: return@collect)
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }
    }
}