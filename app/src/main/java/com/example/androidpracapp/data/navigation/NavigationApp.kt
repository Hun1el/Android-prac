package com.example.androidpracapp.data.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidpracapp.ui.screen.CreateNewPasswordScreen
import com.example.androidpracapp.ui.screen.ForgotPasswordScreen
import com.example.androidpracapp.ui.screen.HomeScreen
import com.example.androidpracapp.ui.screen.OnboardPagerScreen
import com.example.androidpracapp.ui.screen.RegisterAccountScreen
import com.example.androidpracapp.ui.screen.SignInScreen
import com.example.androidpracapp.ui.screen.SplashScreen
import com.example.androidpracapp.ui.screen.VerificationScreen
import com.example.androidpracapp.ui.viewModel.SignInViewModel
import com.example.androidpracapp.ui.viewModel.SignUpViewModel

@Composable
fun NavigationApp(navController: NavHostController) {
    val signUpViewModel: SignUpViewModel = viewModel()
    val signInViewModel: SignInViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavRoute.Splash.route
    ) {
        composable(NavRoute.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(NavRoute.Onboard.route) {
                        popUpTo(NavRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoute.Onboard.route) {
            OnboardPagerScreen(
                onOnboardComplete = { navController.navigate(NavRoute.SignUp.route) }
            )
        }

        composable(NavRoute.SignUp.route) {
            RegisterAccountScreen(
                viewModel = signUpViewModel,
                onSignInClick = { navController.navigate(NavRoute.SignIn.route) },
                onSignUpSuccess = { navController.navigate(NavRoute.SignIn.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoute.SignIn.route) {
            SignInScreen(
                viewModel = signInViewModel,
                onSignUpClick = { navController.navigate(NavRoute.SignUp.route) },
                onSignInSuccess = {
                    navController.navigate(NavRoute.Home.route) {
                        popUpTo(NavRoute.SignIn.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { navController.navigate(NavRoute.ForgotPassword.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoute.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = signInViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToOTP = { email ->
                    navController.navigate("${NavRoute.VerifyOTP.route}?email=$email")
                }
            )
        }

        composable("${NavRoute.VerifyOTP.route}?email={email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificationScreen(
                email = email,
                viewModel = signInViewModel,
                onBackClick = { navController.popBackStack() },
                onSuccess = { navController.navigate(NavRoute.CreateNewPassword.route) }
            )
        }

        composable(NavRoute.CreateNewPassword.route) {
            CreateNewPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(NavRoute.SignIn.route) {
                        popUpTo(NavRoute.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoute.Home.route) {
            HomeScreen()
        }
    }
}