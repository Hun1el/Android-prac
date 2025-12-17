package com.example.androidpracapp.data.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface ProfileManagementService {
    @Headers("apikey: $API_KEY", "Content-Type: application/json")
    @GET("rest/v1/profiles")
    suspend fun getUserProfile(@Query("user_id") userId: String, @Query("select") select: String = "*"): Response<List<UserProfile>>

    @Headers("apikey: $API_KEY", "Content-Type: application/json", "Prefer: return=representation")
    @PATCH("rest/v1/profiles")
    suspend fun updateUserProfile(@Query("user_id") userId: String, @Body profile: UserProfileUpdate): Response<List<UserProfile>>

    @Headers("apikey: $API_KEY", "Content-Type: application/json", "Prefer: return=representation")
    @POST("rest/v1/profiles")
    suspend fun createUserProfile(@Body profile: UserProfileCreate): Response<List<UserProfile>>
}

data class UserProfile(
    val id: String,
    val user_id: String,
    val firstname: String?,
    val lastname: String?,
    val address: String?,
    val phone: String?,
    val photo: String?
)

data class UserProfileUpdate(
    val firstname: String?,
    val lastname: String?,
    val address: String?,
    val phone: String?,
    val photo: String?
)

data class UserProfileCreate(
    val user_id: String,
    val firstname: String?,
    val lastname: String?,
    val address: String?,
    val phone: String?,
    val photo: String?
)