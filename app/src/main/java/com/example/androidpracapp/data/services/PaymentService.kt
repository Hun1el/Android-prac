package com.example.androidpracapp.data.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class PaymentCard(
    val id: String,
    val user_id: String,
    val card_name: String,
    val card_number: Long
)

interface PaymentService {
    @GET("payments")
    suspend fun getUserCards(@Query("user_id") userId: String): Response<List<PaymentCard>>
}