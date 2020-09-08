package com.hike.tagging.audiotaggerappreduced.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitUtils {

    var client: TaggerRESTAPIService

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        val builder =
            Retrofit.Builder().baseUrl("https://voice-tools-stag.hike.in/")
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)

        val retrofit = builder.build()
        client = retrofit.create(TaggerRESTAPIService::class.java)
    }

    fun getTaggerRestApiClient(): TaggerRESTAPIService {
        return client
    }
}