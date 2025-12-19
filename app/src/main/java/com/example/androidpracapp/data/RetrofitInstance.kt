package com.example.androidpracapp.data

import com.example.androidpracapp.data.services.API_KEY
import com.example.androidpracapp.data.services.CartManagementService
import com.example.androidpracapp.data.services.CatalogManagementService
import com.example.androidpracapp.data.services.FavoriteManagementService
import com.example.androidpracapp.data.services.OrderManagementService
import com.example.androidpracapp.data.services.ProfileManagementService
import com.example.androidpracapp.data.services.UserManagementService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

object RetrofitInstance {

    const val SUPABASE_URL = "https://rarfrmrtvdrqmqfltyfa.supabase.co/"

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("apikey", API_KEY)
            .header("Authorization", "Bearer $API_KEY")

        val request = requestBuilder.build()
        chain.proceed(request)
    }

//    private var proxy: Proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("10.207.106.77", 3128))
//    private var client: OkHttpClient = OkHttpClient.Builder()
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .readTimeout(30, TimeUnit.SECONDS)
//        .writeTimeout(30, TimeUnit.SECONDS)
//        .proxy(proxy)
//        .addInterceptor(authInterceptor)
//        .build()

    val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(SUPABASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val userManagementService = retrofit.create(UserManagementService::class.java)
    val profileManagementService = retrofit.create(ProfileManagementService::class.java)
    val catalogManagementService = retrofit.create(CatalogManagementService::class.java)
    val favoriteManagementService = retrofit.create(FavoriteManagementService::class.java)
    val cartManagementService = retrofit.create(CartManagementService::class.java)
    val orderService = retrofit.create(OrderManagementService::class.java)
}