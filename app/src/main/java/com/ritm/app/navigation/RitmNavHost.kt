package com.ritm.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ritm.core.navigation.RitmRoute
import com.ritm.feature.splash.splashGraph
import com.ritm.feature.statistics.statisticsGraph
import com.ritm.feature.today.todayGraph

@Composable
fun RitmNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = RitmRoute.Splash.route) {
        splashGraph(navController)
        todayGraph(navController)
        statisticsGraph(navController)
    }
}
