package com.example.androidpracapp.data.navigation

sealed class NavRoute(val route: String) {
    object Splash : NavRoute("splash")
    object Onboard : NavRoute("onboard")
    object SignIn : NavRoute("sign_in")
    object SignUp : NavRoute("sign_up")
    object ForgotPassword : NavRoute("forgot_password")
    object VerifyOTP : NavRoute("verify_otp")
    object CreateNewPassword : NavRoute("create_new_password")
    object ProductDetails : NavRoute("product_details")
    object MyCart : NavRoute("my_cart")
    object Checkout : NavRoute("checkout")

    object Home : NavRoute("home")
    object Favorite : NavRoute("favorite")
    object Orders : NavRoute("orders")
    object Profile : NavRoute("profile")
    object Catalog : NavRoute("catalog")
}
