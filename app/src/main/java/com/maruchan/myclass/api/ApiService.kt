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

    @POST("api/data_user")
    suspend fun getToken(): String

    @FormUrlEncoded
    @POST("api/getnotif")
    suspend fun getNotif(
        @Field("to") to: String?,
        @Field("title") title: String?,
        @Field("body") body: String?
    ): String

    @POST("api/like/{id}")
    suspend fun like(
        @Path("id") id: Int?
    ): String

    @POST("api/unlike/{id}")
    suspend fun unLike(
        @Path("id") id: Int?
    ): String

    @FormUrlEncoded
    @POST("api/editpassword")
    suspend fun editPassword(
        @Field("current_password") current_password: String?,
        @Field("new_password") new_password: String?
    ): String

    @DELETE("api/deleteuser/{id}")
    suspend fun delete(): String

    @POST("api/logout")
    suspend fun logout(): String

    @GET("api/listsekolah")
    suspend fun getListSekolah(): String

    @GET("api/listuser")
    suspend fun getList(): String

    @GET("api/myfriend")
    suspend fun getListFriend(): String

}