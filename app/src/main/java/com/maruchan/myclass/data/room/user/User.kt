package com.maruchan.myclass.data.room.user

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @Expose
    @SerializedName("user_id")
    val user_id: Int?,
    @Expose
    @SerializedName("nama")
    val nama: String?,
    @Expose
    @SerializedName("sekolah_id")
    val sekolah_id: Int?,
    @Expose
    @SerializedName("nomor_telepon")
    val nomor_telepon: String?,
    @SerializedName("foto")
    val foto: String?,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?
) : Parcelable