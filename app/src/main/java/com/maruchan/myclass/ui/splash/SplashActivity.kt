package com.maruchan.myclass.ui.splash

import android.os.Bundle
import com.crocodic.core.extension.openActivity
import com.maruchan.myclass.R
import com.maruchan.myclass.base.BaseActivity
import com.maruchan.myclass.databinding.ActivitySplashBinding
import com.maruchan.myclass.ui.home.HomeActivity
import com.maruchan.myclass.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(R.layout.activity_splash)  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userLogin = session.getUser()

        viewModel.splash {
            if (userLogin == null) {
                openActivity<LoginActivity>()
            } else {
                openActivity<HomeActivity>()
            }
            finish()
        }
    }
}