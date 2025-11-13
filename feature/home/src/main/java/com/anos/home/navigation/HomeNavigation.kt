package com.anos.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.anos.home.ui.HomeRoute
import com.anos.model.Repo
import com.anos.navigation.ScreenRoute

fun NavGraphBuilder.homeScreen(
    onItemClick: (Repo) -> Unit,
) {
    composable<ScreenRoute.HomeScreen> {
         HomeRoute(
             onItemClick = onItemClick,
         )
    }
}