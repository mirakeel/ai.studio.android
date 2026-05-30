package com.example.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkProvider {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(NetworkConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val socialDashService: SocialDashService by lazy {
        retrofit.create(SocialDashService::class.java)
    }

    private val ollamaRetrofit = Retrofit.Builder()
        .baseUrl(NetworkConfig.OLLAMA_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val ollamaService: OllamaService by lazy {
        ollamaRetrofit.create(OllamaService::class.java)
    }

    val repository: com.example.repository.SocialDashRepository by lazy {
        com.example.repository.SocialDashRepository(socialDashService)
    }
}
