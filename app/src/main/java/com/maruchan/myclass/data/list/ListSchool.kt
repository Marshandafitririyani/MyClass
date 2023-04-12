package com.maruchan.myclass.data.list

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListSchool(
    @Expose
    @SerializedName("sekolah")
    val sekolah: String?,
    @Expose
    @SerializedName("sekolah_id")
    val sekolah_id: Int?,
    @Expose
    @SerializedName("created_at")
    val createdAt: String?,
    @Expose
    @SerializedName("updated_at")
    val updatedAt: String?
) : Parcelable