package com.maruchan.myclass.ui.register

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.maruchan.myclass.R
import com.maruchan.myclass.base.BaseActivity
import com.maruchan.myclass.data.list.ListSchoolTwo
import com.maruchan.myclass.databinding.ActivityRegisterBinding
import com.maruchan.myclass.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity :
    BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {

    private val listSchool = ArrayList<ListSchoolTwo?>()
    private var schoolId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initClick()
        getListSchool()
        autocompleteSpinner()
        observe()

    }

    private fun initClick() {
        binding.btnRegister.setOnClickListener {
            register()
        }

        binding.tvLogInRegister.setOnClickListener {
            finish()
        }
        binding.btnBackReg.setOnClickListener {
            finish()
        }
    }

    private fun getListSchool() {
        viewModel.getListSchool()
    }

    private fun register() {
        val name = binding.etNameRegister.textOf()
        val phone = binding.etPhoneRegister.textOf()
        val password = binding.etPasswordRegister.textOf()
        val confirmPassword = binding.etConfirmPasswordRegister.textOf()

        if (binding.etNameRegister.isEmptyRequired(R.string.label_must_fill) ||
            binding.etPhoneRegister.isEmptyRequired(R.string.label_must_fill) ||
            binding.etPasswordRegister.isEmptyRequired(R.string.label_must_fill) ||
            binding.etConfirmPasswordRegister.isEmptyRequired(R.string.label_must_fill)
        ) {
            return
        }
        if (password.length < 6) {
            binding.root.snacked(R.string.password_characters)
        } else {
            if (password != confirmPassword) {
                binding.tvPasswordNotMatch.visibility = View.VISIBLE
            } else {
                binding.tvPasswordNotMatch.visibility = View.GONE
                viewModel.register(name, phone, schoolId, password)
            }
        }
    }


    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show(R.string.register_loading)
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<LoginActivity>()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                binding.root.snacked(R.string.register_failed)
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
                launch {
                    viewModel.saveListSchool.collect { school ->
                        listSchool.addAll(school)

                    }
                }
            }
        }
    }

    private fun autocompleteSpinner() {
        val autoCompleteSpinner = findViewById<AutoCompleteTextView>(R.id.auto_complete_spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSchool)
        autoCompleteSpinner.setAdapter(adapter)

        autoCompleteSpinner.setOnClickListener {
            autoCompleteSpinner.showDropDown()
            autoCompleteSpinner.setDropDownVerticalOffset(-autoCompleteSpinner.height)
        }
        autoCompleteSpinner.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = listSchool[position]
            schoolId = selectedItem?.sekolahId!!

        }
    }
}


