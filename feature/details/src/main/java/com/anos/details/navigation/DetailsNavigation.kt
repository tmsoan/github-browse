package com.anos.details.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anos.details.ui.DetailsRoute
import com.anos.model.Repo

const val DETAILS_ROUTE = "details/{repoId}/{owner}/{name}"

fun NavController.navigateToDetails(repoId: Int, navOptions: NavOptions? = null) {
    navigate(
        DETAILS_ROUTE.replace("{repoId}", repoId.toString()),
        navOptions
    )
}

fun NavController.navigateToDetails(repoInfo: Repo, navOptions: NavOptions? = null) {
    navigate(
        DETAILS_ROUTE
            .replace("{repoId}", repoInfo.id.toString())
            .replace("{owner}", repoInfo.owner?.login ?: "")
            .replace("{name}", repoInfo.name ?: ""),
        navOptions
    )
}

fun NavGraphBuilder.detailsScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = DETAILS_ROUTE,
        arguments = listOf(
            navArgument("repoId") {
                type = NavType.IntType
                nullable = false
            }
        ),
    ) { backStackEntry ->
        val repoId = backStackEntry.arguments?.getInt("repoId")
            ?: throw IllegalStateException("repoId parameter wasn't found. Please make sure it's set!")
        val owner = backStackEntry.arguments?.getString("owner")
            ?: throw IllegalStateException("owner parameter wasn't found. Please make sure it's set!")
        val name = backStackEntry.arguments?.getString("name")
            ?: throw IllegalStateException("name parameter wasn't found. Please make sure it's set!")
        DetailsRoute(
            repoId = repoId,
            owner = owner,
            name = name,
            onBackClick = onBackClick
        )
    }
}