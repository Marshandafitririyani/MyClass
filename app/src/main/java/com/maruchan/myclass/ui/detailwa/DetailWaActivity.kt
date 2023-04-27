package com.maruchan.myclass.ui.detailwa

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.text
import com.crocodic.core.extension.tos
import com.maruchan.myclass.R
import com.maruchan.myclass.data.constant.Const
import com.maruchan.myclass.data.list.ListFriends
import com.maruchan.myclass.databinding.ActivityDetailWaBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.net.URLEncoder

@AndroidEntryPoint
class DetailWaActivity : NoViewModelActivity<ActivityDetailWaBinding>(R.layout.activity_detail_wa) {
    private var phone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        phone = intent.getStringExtra("phone")
        binding.detail = this
        binding.edtnum.setText(phone)

        binding.btnsend.setOnClickListener {
            val nomorHp = binding.edtnum.text.toString().substring(1)
            if (nomorHp.isEmpty()) {
                Toast.makeText(
                    this@DetailWaActivity, getString(R.string.label_must_fill),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                whatsapp("+62$nomorHp")
            }
        }
    }

    private fun whatsapp(number: String) {
        val intentUri = Uri.parse("https://api.whatsapp.com/send?phone="+number)
        val mapIntent = Intent(Intent.ACTION_VIEW)
        mapIntent.setData(intentUri)
        startActivity(mapIntent)
    }
}