package com.example.androidpracapp.data.services

import com.example.androidpracapp.domain.models.FavoriteItemWrapper
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface FavoriteManagementService {
    @Headers("apikey: $API_KEY")
    @GET("rest/v1/favourite")
    suspend fun getFavorites(@Query("user_id") userId: String, @Query("select") select: String = "products!favourite_product_id_fkey(*)"): Response<List<FavoriteItemWrapper>>

    @Headers("apikey: $API_KEY", "Prefer: return=minimal")
    @POST("rest/v1/favourite")
    suspend fun addFavorite(@Body favorite: NewFavorite): Response<Void>

    @Headers("apikey: $API_KEY")
    @DELETE("rest/v1/favourite")
    suspend fun deleteFavorite(@Query("user_id") userId: String, @Query("product_id") productId: String): Response<Void>
}

data class NewFavorite(
    val user_id: String,
    val product_id: String
)

data class Favorite(
    val id: String,
    val user_id: String,
    val product_id: String
)