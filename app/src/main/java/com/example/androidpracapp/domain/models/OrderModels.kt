package com.example.androidpracapp.domain.model

import com.google.gson.annotations.SerializedName

data class OrderWithItems(
    val id: Long,
    val created_at: String?,
    val delivery_coast: Long?,
    val status_id: String?,

    @SerializedName("orders_items")
    val items: List<OrderItemDto> = emptyList()
)

data class OrderItemDto(
    val id: String?,
    val product_id: String?,
    val title: String,
    val coast: Double,
    val count: Int
)
