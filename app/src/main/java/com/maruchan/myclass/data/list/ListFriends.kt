package com.maruchan.myclass.data.list

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListFriends(
    @Expose
    @SerializedName("user_id")
    val user_id: Int?,
    @Expose
    @SerializedName("nama")
    val nama: String?,
    @Expose
    @SerializedName("sekolah_id")
    val sekolah_id: String?,
    @Expose
    @SerializedName("nomor_telepon")
    val nomor_telepon: String?,
    @Expose
    @SerializedName("api_token")
    val api_token: String?,
    @Expose
    @SerializedName("foto")
    val foto: String?,
    @Expose
    @SerializedName("nama_sekolah")
    val nama_sekolah: String?,
    @Expose
    @SerializedName("device_token")
    val device_token: String?,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?,
    @Expose
    @SerializedName("like_by_you")
    var like_by_you: Boolean?,
) : Parcelable