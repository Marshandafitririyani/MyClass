package com.maruchan.myclass.injection

import android.content.Context
import com.crocodic.core.BuildConfig
import com.crocodic.core.helper.okhttp.SSLTrust
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.maruchan.myclass.api.ApiService
import com.maruchan.myclass.data.constant.Const
import com.maruchan.myclass.data.session.Session
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

@InstallIn(SingletonComponent::class)
@Module
class DataModul {
    @Provides
    fun provideSession(@ApplicationContext context: Context, gson: Gson) = Session(context, gson)

    @Provides
    fun provideGson() =
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    @Provides
    fun provideOkHttpClient(session: Session): OkHttpClient {

        val unsafeTrustManager = SSLTrust().createUnsafeTrustManager()
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(unsafeTrustManager), null)

        val okHttpClient = OkHttpClient().newBuilder()
            .sslSocketFactory(sslContext.socketFactory, unsafeTrustManager)
            .connectTimeout(Const.TIMEOUT.NINETY_LONG, TimeUnit.SECONDS)
            .readTimeout(Const.TIMEOUT.NINETY_LONG, TimeUnit.SECONDS)
            .writeTimeout(Const.TIMEOUT.NINETY_LONG, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val token = session.getString(Const.TOKEN.API_TOKEN)
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("Content-Type", "application/json")
                    .method(original.method, original.body)

                val request = requestBuilder.build()
                chain.proceed(request)
            }

        if (BuildConfig.DEBUG) {
            val interceptors = HttpLoggingInterceptor()
            interceptors.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(interceptors)
        }

        return okHttpClient.build()

    }

    @Provides
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(com.maruchan.myclass.BuildConfig.API_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build().create(ApiService::class.java)
    }
}