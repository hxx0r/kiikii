package com.quark.kiikii.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Headers
import java.util.concurrent.TimeUnit

interface HIBPApiService {
    @GET("range/{hashPrefix}")
    @Headers(
        "User-Agent: DataBreachChecker-Android-App",
        "Add-Padding: true"  // Для конфиденциальности
    )
    suspend fun getPasswordRange(@Path("hashPrefix") hashPrefix: String): String
}

object HIBPApi {
    private const val BASE_URL = "https://api.pwnedpasswords.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: HIBPApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(HIBPApiService::class.java)
    }
}