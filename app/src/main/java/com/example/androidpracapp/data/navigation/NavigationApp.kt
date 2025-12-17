package com.example.androidpracapp.data.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.androidpracapp.R
import androidx.navigation.compose.composable
import com.example.androidpracapp.ui.components.BottomNavItem
import com.example.androidpracapp.ui.components.BottomNavigation
import com.example.androidpracapp.ui.screen.CatalogScreen
import com.example.androidpracapp.ui.screen.CreateNewPasswordScreen
import com.example.androidpracapp.ui.screen.ForgotPasswordScreen
import com.example.androidpracapp.ui.screen.HomeScreen
import com.example.androidpracapp.ui.screen.OnboardPagerScreen
import com.example.androidpracapp.ui.screen.ProfileScreen
import com.example.androidpracapp.ui.screen.RegisterAccountScreen
import com.example.androidpracapp.ui.screen.SignInScreen
import com.example.androidpracapp.ui.screen.SplashScreen
import com.example.androidpracapp.ui.screen.VerificationScreen
import com.example.androidpracapp.ui.viewModel.CatalogViewModel
import com.example.androidpracapp.ui.viewModel.SignInViewModel
import com.example.androidpracapp.ui.viewModel.SignUpViewModel

@Composable
fun NavigationApp(navController: NavHostController) {
    val signUpViewModel: SignUpViewModel = viewModel()
    val signInViewModel: SignInViewModel = viewModel()
    val catalogViewModel: CatalogViewModel = viewModel()

    fun navigateToTab(index: Int) {
        val route = when (index) {
            0 -> NavRoute.Home.route
            1 -> NavRoute.Favorite.route
            2 -> NavRoute.Orders.route
            3 -> NavRoute.Profile.route
            else -> NavRoute.Home.route
        }

        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

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
            HomeScreen(
                viewModel = catalogViewModel,
                selectedTabIndex = 0,
                onTabSelected = { index -> navigateToTab(index) },
                onCategoryClick = { navController.navigate(NavRoute.Catalog.route) }
            )
        }

        composable(NavRoute.Catalog.route) {
            CatalogScreen(
                viewModel = catalogViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoute.Favorite.route) {
            PlaceholderScreen(stringResource(R.string.favorite), 1) { index -> navigateToTab(index) }
        }

        composable(NavRoute.Orders.route) {
            PlaceholderScreen(stringResource(R.string.order), 2) { index -> navigateToTab(index) }
        }

        composable(NavRoute.Profile.route) {
            ProfileScreen(
                selectedTabIndex = 3,
                onTabSelected = { index -> navigateToTab(index) }
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, index: Int, onTabSelected: (Int) -> Unit) {
    androidx.compose.material3.Scaffold(
        bottomBar = {
            BottomNavigation(
                items = listOf(
                    BottomNavItem(R.drawable.home, "Home"),
                    BottomNavItem(R.drawable.favorite, "Favorite"),
                    BottomNavItem(R.drawable.orders, "Orders"),
                    BottomNavItem(R.drawable.profile, "Profile"),
                ),
                selectedTabIndex = index,
                onTabSelected = onTabSelected,
                onFabClick = { },
                fabIconRes = R.drawable.shoping
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text(text = title)
        }
    }
}