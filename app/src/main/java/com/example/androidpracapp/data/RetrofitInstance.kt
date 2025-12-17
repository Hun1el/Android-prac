package com.example.androidpracapp.data

import com.example.androidpracapp.data.services.ProfileManagementService
import com.example.androidpracapp.data.services.UserManagementService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy
import kotlin.jvm.java

object RetrofitInstance {

    const val SUPABASE_URL = "https://rarfrmrtvdrqmqfltyfa.supabase.co/"

    //private var proxy: Proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress("10.207.106.77", 3128))
    //private var client: OkHttpClient = OkHttpClient.Builder().proxy(proxy).build()

    val client: OkHttpClient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(SUPABASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val userManagementService = retrofit.create(UserManagementService::class.java)
    val profileManagementService = retrofit.create(ProfileManagementService::class.java)
}