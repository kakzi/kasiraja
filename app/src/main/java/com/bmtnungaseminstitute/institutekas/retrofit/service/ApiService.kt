package com.bmtnungaseminstitute.institutekas.retrofit.service

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL: String = "http://kasir.bmtnupintar.com/api/"
//const val BASE_URL: String = "http://192.168.100.129/kasir/public/api/"
//const val URL_IMAGE: String = "http://192.168.100.129/kasir/public/uploads/produk/"
const val URL_IMAGE: String = "http://kasir.bmtnupintar.com/uploads/produk/"

object ApiService {

    val owner: OwnerEndpoint
        get() {
            return retrofit.create(
                OwnerEndpoint::class.java)
        }

    val cashier: CashierEndpoint
        get() {
            return retrofit.create(
                CashierEndpoint::class.java)
        }

    val auth: AuthEndpoint
        get() {
            return retrofit.create(
                AuthEndpoint::class.java)
        }

    val retrofit: Retrofit
        get() {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }
}