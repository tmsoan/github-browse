package com.anos.navigation

import androidx.navigation3.runtime.NavKey
import com.anos.model.Repo
import kotlinx.serialization.Serializable

/**
 * A sealed interface representing different screen routes in the application.
 */
sealed interface ScreenRoute : NavKey {
    @Serializable
    data object HomeScreen : ScreenRoute

    @Serializable
    data class DetailsScreen(val repo: Repo) : ScreenRoute
}
