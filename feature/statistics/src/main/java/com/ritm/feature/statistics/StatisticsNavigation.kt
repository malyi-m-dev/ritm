package com.ritm.feature.statistics

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ritm.core.navigation.RitmRoute

fun NavGraphBuilder.statisticsGraph(navController: NavHostController) {
    composable(RitmRoute.Statistics.route) {
        StatisticsRoute(
            onNavigateToToday = {
                navController.navigate(RitmRoute.Today.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        )
    }
}
