package com.anos.navigation

import kotlinx.serialization.Serializable

/**
 * A sealed interface representing different screen routes in the application.
 */
sealed interface ScreenRoute {
    @Serializable
    data object HomeScreen : ScreenRoute

    @Serializable
    data class DetailsScreen(
        val repoId: Int,
        val owner: String,
        val name: String,
    ) : ScreenRoute
}
