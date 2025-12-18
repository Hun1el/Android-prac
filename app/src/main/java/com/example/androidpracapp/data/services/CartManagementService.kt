package com.example.androidpracapp.data.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

data class CartEntry(
    val id: String = "",
    val user_id: String,
    val product_id: String,
    val count: Int? = 1
)

interface CartManagementService {

    @Headers("Prefer: return=representation")
    @GET("cart")
    suspend fun getCartItems(@Query("select") select: String = "*", @Query("user_id") userId: String): Response<List<CartEntry>>

    @Headers("Prefer: return=representation")
    @POST("cart")
    suspend fun addToCart(@Body cartEntry: CartEntry): Response<List<CartEntry>>

    @Headers("Prefer: return=representation")
    @PATCH("cart")
    suspend fun updateCartItem(@Query("id") id: String, @Body body: Map<String, Int>): Response<List<CartEntry>>

    @Headers("Prefer: return=representation")
    @DELETE("cart")
    suspend fun deleteCartItem(@Query("id") id: String): Response<Unit>
}
