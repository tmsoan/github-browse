package com.anos.gitbrowse.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.anos.details.navigation.detailsScreen
import com.anos.details.navigation.navigateToDetails
import com.anos.home.navigation.HOME_ROUTE
import com.anos.home.navigation.homeScreen

@Composable
fun AppNavHost(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = HOME_ROUTE
    ) {
        homeScreen(
            onItemClick = { repo ->
                navHostController.navigateToDetails(repoInfo = repo)
            }
        )
        detailsScreen {
            navHostController.popBackStack()
        }
    }
}