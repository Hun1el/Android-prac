package com.example.androidpracapp.data.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidpracapp.ui.screen.RegisterAccountScreen
import com.example.androidpracapp.ui.screen.SignInScreen

@Composable
fun NavigationApp(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.SignUp.route
    ) {
        composable(NavRoute.SignUp.route) {
            RegisterAccountScreen(
                onSignInClick = { navController.navigate(NavRoute.SignIn.route) }
            )
        }

        composable(NavRoute.SignIn.route) {
            SignInScreen(
                onSignUpClick = { navController.navigate(NavRoute.SignUp.route) },
                onSignInSuccess = { navController.navigate(NavRoute.Home.route) }
            )
        }
    }
}