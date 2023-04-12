package com.maruchan.myclass.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("nomor_telepon") phone: String?,
        @Field("password") password: String?
    ): String

    @POST("api/data_user")
    suspend fun getToken(): String

    @POST("api/logout")
    suspend fun logout(): String

    @GET("api/listsekolah")
    suspend fun getListSekolah(): String

    @GET("api/listuser")
    suspend fun getList(): String

    @GET("api/myfriend")
    suspend fun getListFriend(): String

    @FormUrlEncoded
    @POST("api/register")
    suspend fun register(
        @Field("nama") name: String?,
        @Field("nomor_telepon") phone: String?,
        @Field("sekolah_id") school: String?,
        @Field("password") password: String?
    ): String

}