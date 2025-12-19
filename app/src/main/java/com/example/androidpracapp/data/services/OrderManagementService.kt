package com.example.androidpracapp.data.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderManagementService {
    @POST("rest/v1/orders")
    suspend fun createOrder(@Body order: CreateOrderRequest): Response<Unit>
}

data class CreateOrderRequest(
    val user_id: String,
    val address: String,
    val phone: String,
    val email: String,
    val payment_id: String?,
    val delivery_coast: Long
)