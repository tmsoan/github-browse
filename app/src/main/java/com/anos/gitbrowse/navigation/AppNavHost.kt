package com.anos.gitbrowse.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.anos.details.navigation.detailsScreen
import com.anos.details.navigation.navigateToDetails
import com.anos.home.navigation.homeScreen
import com.anos.navigation.ScreenRoute

@Composable
fun AppNavHost(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = ScreenRoute.HomeScreen
    ) {
        homeScreen {
            navHostController.navigateToDetails(repoInfo = it)
        }
        detailsScreen(
            onBackClick = { navHostController.popBackStackSafe() },
        )
    }
}

private fun NavHostController.popBackStackSafe() {
    if (this.previousBackStackEntry != null) {
        this.popBackStack()
    }
}
