package com.anos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

val LocalComposeNavigator: ProvidableCompositionLocal<AppNavigator> =
    compositionLocalOf {
        error("No Compose Navigator provided! " +
                "Make sure to wrap all usages of navigation components in a Navigator provider.")
    }

/**
 * Retrieves the current [AppNavigator] at the call site's position in the hierarchy.
 */
val currentComposeNavigator: AppNavigator
    @Composable
    @ReadOnlyComposable
    get() = LocalComposeNavigator.current
