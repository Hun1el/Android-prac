package com.example.androidpracapp.data.services

import com.example.androidpracapp.ui.viewModel.CreateOrderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderManagementService {
    @POST("rest/v1/orders")
    suspend fun createOrder(@Body order: CreateOrderRequest): Response<OrderResponse>
}

data class CreateOrderRequest(
    val user_id: String,
    val total_amount: Double,
    val delivery_address: String,
    val phone: String,
    val email: String,
    val payment_method: String,
    val items: List<Map<String, Any>>
)

data class OrderResponse(
    val id: String,
    val user_id: String,
    val total_amount: Double,
    val status: String,
    val created_at: String
)
