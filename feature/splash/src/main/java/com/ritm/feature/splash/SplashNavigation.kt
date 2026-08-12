package com.ritm.feature.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ritm.core.navigation.RitmRoute

fun NavGraphBuilder.splashGraph(navController: NavHostController) {
    composable(RitmRoute.Splash.route) {
        SplashRoute(
            onNavigateToToday = {
                navController.navigate(RitmRoute.Today.route) {
                    popUpTo(RitmRoute.Splash.route) { inclusive = true }
                }
            },
        )
    }
}
