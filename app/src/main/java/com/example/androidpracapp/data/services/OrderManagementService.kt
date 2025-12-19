package com.example.androidpracapp.data.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OrderManagementService {
    @Headers("Prefer: return=representation")
    @POST("rest/v1/orders")
    suspend fun createOrder(@Body order: CreateOrderRequest): Response<List<OrderIdResponse>>
    @POST("rest/v1/orders_items")
    suspend fun addOrderItems(@Body items: List<CreateOrderItemRequest>): Response<Unit>
}

data class CreateOrderRequest(
    val user_id: String,
    val address: String,
    val phone: String,
    val email: String,
    val payment_id: String?,
    val delivery_coast: Long
)

data class OrderIdResponse(
    val id: Long
)

data class CreateOrderItemRequest(
    val order_id: Long,
    val product_id: String,
    val count: Int,
    val coast: Double,
    val title: String
)