package com.anos.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.anos.home.ui.HomeRoute
import com.anos.model.Repo

const val HOME_ROUTE = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(HOME_ROUTE, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onItemClick: (Repo) -> Unit,
) {
    composable(
        route = HOME_ROUTE,
    ) {
         HomeRoute(
             onItemClick = onItemClick,
         )
    }
}