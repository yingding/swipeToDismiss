package com.example.android.wearable.composeforwearos.nav

/**
 * Navigation Screen element class
 * used to navigate through the wear compose nav graph
 */
sealed class NavScreen(val route: String) {
    object Menu: NavScreen("menu")
    object Graphs: NavScreen("Graphs")
    object Settings: NavScreen("settings")
}