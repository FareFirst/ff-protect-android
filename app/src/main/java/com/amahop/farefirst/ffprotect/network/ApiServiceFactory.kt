package com.amahop.farefirst.ffprotect.network

import com.amahop.farefirst.ffprotect.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceFactory {
    private val clientMap: HashMap<String, Retrofit> = HashMap()

    @Synchronized
    fun <T> getService(baseUrl: String, clazz: Class<T>): T {
        val existingClient = clientMap[baseUrl]
        if (existingClient != null) {
            return existingClient.create(clazz)
        }

        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor {
            val request = it.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            it.proceed(request)
        }

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(logging)
        }

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(httpClient.build())
            .build()

        clientMap[baseUrl] = retrofit

        return retrofit.create(clazz)
    }
}