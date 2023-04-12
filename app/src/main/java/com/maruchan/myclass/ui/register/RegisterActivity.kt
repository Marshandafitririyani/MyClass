package com.maruchan.myclass.ui.register

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.maruchan.myclass.R
import com.maruchan.myclass.base.BaseActivity
import com.maruchan.myclass.data.list.ListSchool
import com.maruchan.myclass.databinding.ActivityRegisterBinding
import com.maruchan.myclass.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register)  {

/*    private var filter: String? = null*/
    private  val listSekolah = ArrayList<ListSchool>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getlistSekolah()
        spinner()



    /*    ArrayAdapter(this, android.R.layout.simple_spinner_item, listSekolah).also { adapter ->
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

        }*/

      /*  binding.etFilterSchoolRegister.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                p2: Int,
                p3: Long
            ){
                filter = if (p2 == 0) {
                    null
                }else{
                    binding.etFilterSchoolRegister.selectedItem as String
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                filter = null
            }
        }*/



        binding.tvLogInRegister.setOnClickListener{
            openActivity<LoginActivity>()
        }
        binding.btnRegister.setOnClickListener {
            if (binding.etNameRegister.isEmptyRequired(R.string.label_must_fill) ||
                binding.etPhoneRegister.isEmptyRequired(R.string.label_must_fill) ||
                binding.etPasswordRegister.isEmptyRequired(R.string.label_must_fill)
            ) {
                return@setOnClickListener
            }


            val name = binding.etNameRegister.textOf()
            val phone = binding.etPhoneRegister.textOf()
//            val school = binding.etFilterSchoolRegister.
            val password = binding.etPasswordRegister.textOf()

//            viewModel.register(name, phone,school,password)

        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Please Wait Register")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.show("Succes")
                                openActivity<LoginActivity>()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.dismiss()
                                loadingDialog.setResponse(it.message ?: return@collect)
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
                launch {
                    viewModel.saveListSekolah.collect{ data ->
                        listSekolah.clear()
                        Log.d("cek dari api", "data: $data")
                        listSekolah.addAll(data)
                    }
                }
            }
        }

    }
    private fun getlistSekolah() {
            viewModel.getListSekolah()
        }

    private fun spinner(){
        var items = arrayListOf<String>("pilih sekolah")
        listSekolah.forEach{
            it.sekolah?.let { it1 -> items.add(it1) }
        }
        Log.d("cek item spinner", "items: $items")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        binding.etFilterSchoolRegister.adapter = adapterSpinner

        binding.etFilterSchoolRegister.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle item selection here
                val selectedItem = items[position]
                // Do something with the selected item
//                binding.etSchool.text = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected event here
            }
        }

    }

    /*private fun register() {
        val name = binding.etName.textOf()
        val phone = binding.etPhone.textOf()
        val password = binding.etPassword.textOf()
        val confirm = binding.etConfirmPass.textOf()

        if (binding.etName.isEmptyRequired(R.string.mustFill) || binding.etPhone.isEmptyRequired(R.string.mustFill)
            || binding.etPhone.isEmptyRequired(R.string.mustFill) || binding.etConfirmPass.isEmptyRequired(R.string.mustFill))
        {
            return
        }
        if (password != confirm){
            binding.textInputConfirmPassword.error = "Password Not Match"
            return
        }
        binding.root.snacked("register")
        }*/
}