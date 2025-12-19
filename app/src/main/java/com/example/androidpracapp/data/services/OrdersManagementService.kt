package com.example.androidpracapp.data.services

import com.example.androidpracapp.domain.model.OrderWithItems
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OrdersManagementService {
    @GET("rest/v1/orders")
    suspend fun getOrders(@Query("user_id") userId: String, @Query("order") order: String = "created_at.desc", @Query("select") select: String = "*, orders_items(*)"): Response<List<OrderWithItems>>
}
