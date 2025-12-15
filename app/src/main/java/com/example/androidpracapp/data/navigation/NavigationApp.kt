package com.example.androidpracapp.data.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidpracapp.ui.screen.ForgotPasswordScreen
import com.example.androidpracapp.ui.screen.RegisterAccountScreen
import com.example.androidpracapp.ui.screen.SignInScreen
import com.example.androidpracapp.ui.screen.VerificationScreen
import com.example.androidpracapp.ui.viewModel.SignInViewModel
import com.example.androidpracapp.ui.viewModel.SignUpViewModel

@Composable
fun NavigationApp(navController: NavHostController) {
    val signUpViewModel: SignUpViewModel = viewModel()
    val signInViewModel: SignInViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavRoute.SignUp.route
    ) {
        composable(NavRoute.SignUp.route) {
            RegisterAccountScreen(
                viewModel = signUpViewModel,
                onSignInClick = { navController.navigate(NavRoute.SignIn.route) },
                onSignUpSuccess = { navController.navigate(NavRoute.SignIn.route) }
            )
        }

        composable(NavRoute.SignIn.route) {
            SignInScreen(
                viewModel = signInViewModel,
                onSignUpClick = { navController.navigate(NavRoute.SignUp.route) },
                onSignInSuccess = { navController.navigate(NavRoute.Home.route) },
                onForgotPasswordClick = { navController.navigate(NavRoute.ForgotPassword.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoute.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToOTP = { navController.navigate(NavRoute.VerifyOTP.route) }
            )
        }

        composable(NavRoute.VerifyOTP.route) {
            VerificationScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess = { navController.navigate(NavRoute.Home.route) }
            )
        }
    }
}