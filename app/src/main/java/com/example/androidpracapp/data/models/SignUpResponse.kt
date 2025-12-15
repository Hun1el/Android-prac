package com.example.androidpracapp.domain.models

import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("user")
    val user: UserData? = null,
    @SerializedName("id")
    val id: String? = null
) {
    data class UserData(
        val id: String,
        val email: String
    )
}