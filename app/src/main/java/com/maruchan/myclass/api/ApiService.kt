package com.maruchan.myclass.api

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("nomor_telepon") phone: String?,
        @Field("password") password: String?,
        @Field("device_token") device_token:String?
    ): String

    @FormUrlEncoded
    @POST("api/register")
    suspend fun register(
        @Field("nama") name: String?,
        @Field("nomor_telepon") phone: String?,
        @Field("sekolah_id") school: Int?,
        @Field("password") password: String?
    ): String

    @FormUrlEncoded
    @POST("api/editprofile")
    suspend fun editProfile(
        @Field("nama") name: String?,
        @Field("sekolah_id") schoolId: Int?,
    ): String

    @Multipart
    @POST("api/editprofile")
    suspend fun editProfileWithPhoto(
        @Query("nama") name : String,
        @Query("sekolah_id") schoolId: Int?,
        @Part foto : MultipartBody.Part?
    ) : String

    @FormUrlEncoded
    @POST("api/getnotif")
    suspend fun getNotify(
        @Field("to") to: String?,
        @Field("title") title: String?,
        @Field("body") body: String?
    ): String

    @FormUrlEncoded
    @POST("api/editpassword")
    suspend fun editPassword(
        @Field("current_password") current_password: String?,
        @Field("new_password") new_password: String?
    ): String

    @POST("api/data_user")
    suspend fun getUserToken(): String

    @POST("api/like/{id}")
    suspend fun liked(
        @Path("id") id: Int?
    ): String

    @POST("api/unlike/{id}")
    suspend fun unLiked(
        @Path("id") id: Int?
    ): String

    @POST("api/logout")
    suspend fun logout(): String

    @GET("api/listsekolah")
    suspend fun getListSchool(): String

    @GET("api/myfriend")
    suspend fun getListFriend(): String

}