package com.anos.details.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.anos.details.ui.DetailsRoute
import com.anos.model.Repo
import com.anos.navigation.ScreenRoute

fun NavController.navigateToDetails(repoInfo: Repo, navOptions: NavOptions? = null) {
    navigate(
        route = ScreenRoute.DetailsScreen(
            repoId = repoInfo.id,
            owner = repoInfo.owner.login,
            name = repoInfo.name.orEmpty()
        ),
        navOptions
    )
}

fun NavGraphBuilder.detailsScreen(
    onBackClick: () -> Unit,
) {
    composable<ScreenRoute.DetailsScreen> {
        DetailsRoute(onBackClick = onBackClick)
    }
}