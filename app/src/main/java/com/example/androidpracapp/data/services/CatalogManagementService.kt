package com.example.androidpracapp.data.services

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CatalogManagementService {

    @Headers("apikey: $API_KEY")
    @GET("rest/v1/categories")
    suspend fun getCategories(@Query("select") select: String = "*"): Response<List<Category>>

    @Headers("apikey: $API_KEY")
    @GET("rest/v1/products")
    suspend fun getProducts(@Query("select") select: String = "*"): Response<List<Product>>

    @Headers("apikey: $API_KEY")
    @GET("rest/v1/products")
    suspend fun getProductsByCategory(@Query("category_id") categoryId: String, @Query("select") select: String = "*"): Response<List<Product>>
}

data class Product(
    val id: String,
    val title: String,
    val description: String,
    val cost: Double,
    val category_id: String?,
    @SerializedName("is_best_seller") val isBestSeller: Boolean?,

    var categoryName: String? = null,
    var isFavorite: Boolean = false,
    val imageResId: Int? = null
)

data class Category(
    val id: String,
    val title: String
)