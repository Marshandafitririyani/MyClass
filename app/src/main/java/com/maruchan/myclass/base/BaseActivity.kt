package com.maruchan.myclass.base

import androidx.databinding.ViewDataBinding
import com.crocodic.core.base.activity.CoreActivity
import com.crocodic.core.base.viewmodel.CoreViewModel
import com.crocodic.core.extension.clearNotification
import com.crocodic.core.extension.openActivity
import com.maruchan.myclass.data.session.Session
import com.maruchan.myclass.ui.splash.SplashActivity
import javax.inject.Inject

open class BaseActivity<VB : ViewDataBinding, VM : CoreViewModel>(layoutRes: Int) :
    CoreActivity<VB, VM>(layoutRes) {

    @Inject
    lateinit var session: Session

    override fun authLogoutSuccess() {
        super.authLogoutSuccess()
        loadingDialog.dismiss()
        expiredDialog.dismiss()
        clearNotification()
        session.clearAll()
        openActivity<SplashActivity>()
        finishAffinity()
    }


}
