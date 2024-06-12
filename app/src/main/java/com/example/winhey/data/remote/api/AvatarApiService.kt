package com.example.winhey.data.remote.api

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface AvatarApiService {
    @GET("{id}.png")
    suspend fun getAvatar(@Path("id") id: String): Response<ResponseBody>

    companion object {
        private const val BASE_URL = "https://api.multiavatar.com/"

        fun create(): AvatarApiService {
            val logger =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val client = OkHttpClient.Builder().addInterceptor(logger).build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AvatarApiService::class.java)
        }
    }
}
