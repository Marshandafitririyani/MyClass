package com.maruchan.myclass.ui.register

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.maruchan.myclass.R
import com.maruchan.myclass.base.BaseActivity
import com.maruchan.myclass.data.list.ListSchool
import com.maruchan.myclass.data.list.ListSchoolTwo
import com.maruchan.myclass.databinding.ActivityRegisterBinding
import com.maruchan.myclass.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity :
    BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {

    private val listSchool = ArrayList<ListSchoolTwo?>()
    private var schoolId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initClick()
        getListSchool()
        autocompleteSpinner()
        observe()

     /*   ArrayAdapter(this, android.R.layout.simple_spinner_item, listSekolah).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.etFilterSchoolRegister.adapter = adapter
        }

        binding.etFilterSchoolRegister.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                tos("Data: ${listSekolah[position]}")
                Toast.makeText(this@RegisterActivity, "Data: ${listSekolah[position]}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

*/
    }


    private fun initClick() {
        binding.btnRegister.setOnClickListener {
            register()
        }

        binding.tvLogInRegister.setOnClickListener {
           finish()
        }
    }
    private fun tvLogin(){
        val spannableString = SpannableString("Have an accoun? Log in")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                openActivity<LoginActivity>()
            }
        }
        spannableString.setSpan(clickableSpan, 22, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLogInRegister.text = spannableString
        binding.tvLogInRegister.movementMethod = LinkMovementMethod.getInstance() //  TODO: Required for clickable spans to work
    }

    private fun getListSchool() {
        viewModel.getListSekolah()
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
        if (password != confirmPassword) {
            binding.tvPasswordNotMatch.visibility = View.VISIBLE
        } else {
            binding.tvPasswordNotMatch.visibility = View.GONE
            viewModel.register(name, phone, schoolId, password)
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Register....in")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<LoginActivity>()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                binding.root.snacked("Register Failed")
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
                launch {
                    viewModel.saveListSekolah.collect { school ->
                        listSchool.addAll(school)
                    //  TODO: Panggil fungsi untuk spinner item dengan data yang diambil
                    }
                }
            }
        }

    }

    private fun autocompleteSpinner() {
        val autoCompleteSpinner = findViewById<AutoCompleteTextView>(R.id.autoCompleteSpinner)
        val options = arrayListOf("Pilih Sekolah") // untuk mengganti pilihan anda sendiri
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSchool)
        autoCompleteSpinner.setAdapter(adapter)

        // TODO:menampilkan dropdown saat itmenya diklik
        autoCompleteSpinner.setOnClickListener {
            autoCompleteSpinner.showDropDown()
            autoCompleteSpinner.setDropDownVerticalOffset(-autoCompleteSpinner.height)

        }

        autoCompleteSpinner.setOnItemClickListener { parent, view, position, id ->
            // TODO:untuk selected itemenya
            val selectedItem = listSchool[position]
            schoolId = selectedItem?.sekolahId!!
            Toast.makeText(this@RegisterActivity, "Selected: $schoolId", Toast.LENGTH_SHORT).show()


        }
    }
}


