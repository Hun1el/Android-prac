package com.example.androidpracapp.data.navigation

sealed class NavRoute(val route: String) {
    object SignIn : NavRoute("sign_in")
    object SignUp : NavRoute("sign_up")
    object ForgotPassword : NavRoute("forgot_password")
    object Home : NavRoute("home")
    object VerifyOTP : NavRoute("verify_otp")
}