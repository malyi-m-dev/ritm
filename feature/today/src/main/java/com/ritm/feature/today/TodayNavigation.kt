package com.ritm.feature.today

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ritm.core.navigation.RitmRoute

fun NavGraphBuilder.todayGraph(navController: NavHostController) {
    composable(RitmRoute.Today.route) {
        TodayRoute(
            onNavigateToStatistics = {
                navController.navigate(RitmRoute.Statistics.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        )
    }
}
