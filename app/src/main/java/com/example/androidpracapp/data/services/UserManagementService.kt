package com.example.androidpracapp.data.services

import com.example.androidpracapp.domain.models.User
import com.example.androidpracapp.domain.models.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJhcmZybXJ0dmRycW1xZmx0eWZhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjU3OTU0NzYsImV4cCI6MjA4MTM3MTQ3Nn0.5fth8PNr90GBITRf5topSEyV68ebKApIZTw6HV9VlIU"

interface UserManagementService {

    @Headers("apikey: $API_KEY")
    @POST("auth/v1/signup")
    suspend fun signUp(@Body user: User): Response<SignUpResponse>

    @Headers("apikey: $API_KEY")
    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body user: User): Response<SignUpResponse>

    @Headers("apikey: $API_KEY")
    @POST("auth/v1/recover")
    suspend fun resetPasswordForEmail(@Body request: ResetPasswordRequest): Response<Void>

    @Headers("apikey: $API_KEY")
    @POST("rest/v1/users")
    suspend fun checkUserExists(@Query("email") email: String): Response<List<UserCheckResponse>>

    @Headers("apikey: $API_KEY", "Content-Type: application/json")
    @GET("rest/v1/profiles")
    suspend fun getUserProfile(@Query("user_id") userId: String, @Query("select") select: String = "*"): Response<List<UserProfile>>

    @Headers("apikey: $API_KEY", "Content-Type: application/json", "Prefer: return=representation")
    @PATCH("rest/v1/profiles")
    suspend fun updateUserProfile(@Query("user_id") userId: String, @Body profile: UserProfileUpdate): Response<List<UserProfile>>
}

data class UserCheckResponse(
    val id: String,
    val email: String
)

data class ResetPasswordRequest(
    val email: String
)

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