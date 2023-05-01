package com.maruchan.myclass.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.crocodic.core.helper.DateTimeHelper
import com.maruchan.myclass.R
import com.maruchan.myclass.base.BaseActivity
import com.maruchan.myclass.data.list.ListSchoolTwo
import com.maruchan.myclass.data.session.Session
import com.maruchan.myclass.databinding.ActivityProfileBinding
import com.maruchan.myclass.ui.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity :
    BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {

    @Inject
    lateinit var CoreSession: Session
    private var username: String? = null
    private var school: Int? = null
    private var photoFile: File? = null
    private val listSchoolFilter = ArrayList<ListSchoolTwo?>()
    private var schoolId: Int? = null
    private var schoolIdStatic: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        onClick()
        getUser()
        getListSchoolEdit()

        val user = session.getUser()
        if (user != null) {
            binding.user = user
            schoolId = user.sekolah_id
            schoolIdStatic = user.sekolah_id
            Log.d("cek foto", "foto:${user.foto}")

        }
        getlistSekolah(user?.sekolah_id)
    }

    private fun onClick() {
        binding.ivBackProfile.setOnClickListener {
            finish()
        }

        binding.tvNameProfile.setOnClickListener {
            editName {
                binding.user = session.getUser()
            }
        }

        binding.btnLogoutProfile.setOnClickListener {
            logout()
        }

        binding.btnEditPasswordProfile.setOnClickListener {
            editPasswprd()
        }

        binding.tvSchoolProfile.setOnClickListener {
            autocompleteSpinner()
        }

        binding.ivImageSaveEditProfil.setOnClickListener {
            validateForm()
        }

        binding.imgProfile.setOnClickListener {
            if (checkPermissionGallery()) {
                openGallery()
            } else {
                requestPermissionGallery()
            }
        }
    }

    //TODO: untuk kompres foto
    suspend fun compressFile(filePhoto: File): File? {
        loadingDialog.show("Loading...")
        println("Compress 1")
        try {
            println("Compress 2")
            return Compressor.compress(this, filePhoto) {
                resolution(720, 720)
                quality(80)
                format(Bitmap.CompressFormat.PNG)
                size(515)
            }
        } catch (e: Exception) {
            println("Compress 3")
            tos("Gagal kompress anda bisa mengganti foto lain")
            e.printStackTrace()
            return null
        }

    }

    private fun validateForm() {
        val name = binding.tvNameProfile.textOf()
        val schoolId = binding.tvSchoolProfile.textOf()
        val user = session.getUser()
        if (photoFile == null) {
            if (name == username && schoolId == school.toString()) {
                return
            }
            viewModel.editProfile(name, schoolId = user?.sekolah_id)
        } else {
            lifecycleScope.launch {
                val compressPhoto = compressFile(photoFile!!)
                if (compressPhoto != null) {
                    user?.nama?.let {
                        viewModel.editWithPhoto(
                            name = it,
                            schoolId = user.sekolah_id,
                            compressPhoto
                        )
                    }
                }
            }
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //TODO: untuk getUser
                launch {
                    viewModel.responseAPI.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                binding.user = session.getUser()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }

                    }
                }
                //TODO: untuk getUser
                launch {
                    viewModel.saveUserGetProfile.collect {
                        binding.user = it
                    }
                }
                //TODO: untuk fungsi logout
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
                //TODO: untuk fungsi save profile tanpa foto
                launch {
                    viewModel.editProfile.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Save Profile")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                finish()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
                //TODO: untuk transformasi dari sekolah_id menjadi sekolah_nama
                launch {
                    viewModel.saveListSekolah.collect {
                        Log.d("filter school", "school:$it")
                        binding.tvSchoolProfile.text(it.sekolah)
                    }

                }
                //  TODO: untuk fungsii edit sekolah
                //        Panggil fungsi untuk spinner item dengan data yang diambil
                launch {
                    viewModel.saveListSekolahPopup.collect { school ->
                        listSchoolFilter.addAll(school)
                        Log.d("cek sekolah", "listSchool:${school}")

                    }
                }
                //TODO: untuk fungsi save profile dengan foto
                launch {
                    viewModel.editProfileWithPhoto.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Save Profile With Photo")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.show("succes")
                                loadingDialog.dismiss()
                                setResult(12345)
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
            }
        }
    }

    private fun editPasswprd() {
        val builder = AlertDialog.Builder(this)
        val customLayout: View = layoutInflater.inflate(R.layout.popup_edit_password, null)
        builder.setView(customLayout)
        builder.setView(customLayout)

        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        val editTexPassword = customLayout.findViewById<EditText?>(R.id.et_password_old)
        val editTextPasswordNw = customLayout.findViewById<EditText>(R.id.et_password_new)
        val editConfirmPassword = customLayout.findViewById<EditText>(R.id.et_confirmasi_password)
        val textConfirmPassword = customLayout.findViewById<TextView>(R.id.tvPasswordNotMatch)

        val btnSave = customLayout.findViewById<TextView>(R.id.btn_save_password)
        btnSave.setOnClickListener {

            if (editTexPassword.isEmptyRequired(R.string.label_must_fill) ||
                editTextPasswordNw.isEmptyRequired(R.string.label_must_fill) ||
                editConfirmPassword.isEmptyRequired(R.string.label_must_fill)
            ) {
                return@setOnClickListener
            }

            val currentPassword = editTexPassword.textOf()
            val newPassword = editTextPasswordNw.textOf()
            val confirmPassword = editConfirmPassword.textOf()

            //TODO: untuk memastikan apakah password sama dengan confirmasi password
            if (newPassword != confirmPassword) {
                textConfirmPassword.visibility = View.VISIBLE
            } else {
                textConfirmPassword.visibility = View.GONE
                viewModel.editPassword(currentPassword, newPassword)
            }

        }
        dialog.show()
    }


    private fun logout() {
        val builder = AlertDialog.Builder(this)
        val customLayout: View = layoutInflater.inflate(R.layout.popup_logout, null)
        builder.setView(customLayout)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnLogout = customLayout.findViewById<TextView>(R.id.btn_yes)
        btnLogout.setOnClickListener {
            viewModel.logout()
        }

        val btnCancle = customLayout.findViewById<TextView>(R.id.btn_no)
        btnCancle.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getUser() {
        viewModel.getUser()
    }


    private fun getlistSekolah(id: Int?) {
        id?.let {
            viewModel.getListSekolah(id)
        }

    }

    private fun getListSchoolEdit() {
        viewModel.getListSchoolEdit()
    }


    private fun editName(
        onDismiss: () -> Unit
    ) {
        // TODO: Create an alert builder
        val builder = AlertDialog.Builder(this)

        // TODO: set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.popup_edit_name, null)
        builder.setView(customLayout)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val editTextName = customLayout.findViewById<EditText?>(R.id.et_add_name_nw)
        editTextName.setText(session.getUser()?.nama)

        val btnSaveName = customLayout.findViewById<TextView>(R.id.btn_save)
        btnSaveName.setOnClickListener {
            val user = session.getUser()
            viewModel.editProfile(name = editTextName.textOf(), schoolId = user?.sekolah_id)

            onDismiss.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }

    //todo: untuk fungsi edit foto
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 200) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openGallery()
            } else {
                Toast.makeText(this, "Ijin gallery ditolak", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private var activityLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let {
                generateFileImage(it)
            }
        }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGallery.launch(galleryIntent)
    }

    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            110
        )
    }

    private fun generateFileImage(uri: Uri) {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()

            val orientation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getOrientation2(uri)
            } else {
                getOrientation(uri)
            }

            val file = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                createImageFile()
            } else {
                File(externalCacheDir?.absolutePath, getNewFileName())
            }

            val fos = FileOutputStream(file)
            var bitmap = image

            if (orientation != -1 && orientation != 0) {

                val matrix = Matrix()
                when (orientation) {
                    6 -> matrix.postRotate(90f)
                    3 -> matrix.postRotate(180f)
                    8 -> matrix.postRotate(270f)
                    else -> matrix.postRotate(orientation.toFloat())
                }
                bitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            binding.imgProfile.setImageBitmap(bitmap)
            //TODO: untuk mengganti centang pada icon pensil jika foto telah diganti
            binding.ivImageSaveEditProfil.setImageResource(R.drawable.ic_check)
            photoFile = file
        } catch (e: Exception) {
            e.printStackTrace()
            binding.root.snacked("File ini tidak dapat digunakan")
        }
    }

    @SuppressLint("Range")
    private fun getOrientation(shareUri: Uri): Int {
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur = contentResolver.query(
            shareUri,
            orientationColumn,
            null,
            null,
            null
        )
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            if (cur.columnCount > 0) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            }
            cur.close()
        }
        return orientation
    }

    @SuppressLint("NewApi")
    private fun getOrientation2(shareUri: Uri): Int {
        val inputStream = contentResolver.openInputStream(shareUri)
        return getOrientation3(inputStream)
    }

    @SuppressLint("NewApi")
    private fun getOrientation3(inputStream: InputStream?): Int {
        val exif: ExifInterface
        var orientation = -1
        inputStream?.let {
            try {
                exif = ExifInterface(it)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return orientation
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = DateTimeHelper().createAtLong().toString()
        val storageDir =
            getAppSpecificAlbumStorageDir(Environment.DIRECTORY_DOCUMENTS, "Attachment")
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun getNewFileName(isPdf: Boolean = false): String {
        val timeStamp = DateTimeHelper().createAtLong().toString()
        return if (isPdf) "PDF_${timeStamp}_.pdf" else "JPEG_${timeStamp}_.jpg"
    }

    private fun getAppSpecificAlbumStorageDir(albumName: String, subAlbumName: String): File {
        val file = File(getExternalFilesDir(albumName), subAlbumName)
        if (!file.mkdirs()) {
        }
        return file

    }

    private fun autocompleteSpinner() {
        val autoCompleteSpinner = findViewById<AutoCompleteTextView>(R.id.tv_school_profile)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSchoolFilter)
        autoCompleteSpinner.setAdapter(adapter)

        // TODO:menampilkan dropdown saat itmenya diklik
        autoCompleteSpinner.setOnClickListener {
            autoCompleteSpinner.showDropDown()
            //TODO: untuk mengganti icon pensil menjadi icon drop down saat memilih sekolah
            binding.imgDropDrown.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
            autoCompleteSpinner.setDropDownVerticalOffset(-autoCompleteSpinner.height)

        }

        autoCompleteSpinner.setOnItemClickListener { parent, view, position, id ->
            // TODO:untuk selected itemenya
            val selectedItem = listSchoolFilter[position]
            schoolId = selectedItem?.sekolahId!!
            //TODO: untuk mengganti incon drop down menjadi centang setelah  sekolah dipilih
            binding.imgDropDrown.setImageResource(R.drawable.ic_check_profile)
        }
        val btnSave = findViewById<ImageView>(R.id.img_drop_drown)
        btnSave.setOnClickListener {
            val user = session.getUser()
            if (schoolId != schoolIdStatic) {
                user?.nama?.let { nama ->
                    viewModel.editProfile(name = nama, schoolId)
                }
            }
        }
    }
}
