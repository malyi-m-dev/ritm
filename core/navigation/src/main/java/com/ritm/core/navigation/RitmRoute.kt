package com.ritm.core.navigation

/**
 * Единственное место, где feature-модули пересекаются друг с другом — общие строковые
 * маршруты, вокруг которых app-модуль собирает NavHost. Ни один feature-модуль не
 * зависит от другого напрямую.
 */
sealed class RitmRoute(val route: String) {
    data object Splash : RitmRoute("splash")
    data object Today : RitmRoute("today")
    data object Statistics : RitmRoute("statistics")
}
