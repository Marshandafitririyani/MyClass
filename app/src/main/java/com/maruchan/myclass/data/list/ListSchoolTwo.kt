package com.maruchan.myclass.data.list

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ListSchoolTwo(
    @Expose
    @SerializedName("sekolah")
    val sekolah: String?,
    @Expose
    @SerializedName("sekolah_id")
    val sekolahId: Int?,
) {
    override fun toString(): String {
        return sekolah.toString()
    }

}