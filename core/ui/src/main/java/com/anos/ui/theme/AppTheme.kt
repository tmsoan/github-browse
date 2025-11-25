package com.anos.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

/**
 * Local providers for various properties we connect to our components, for styling.
 */
private val LocalColors = compositionLocalOf<AppColors> {
    error("No colors provided! Make sure to wrap all usages of App components in AppTheme.")
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: AppColors = if (darkTheme) {
        AppColors.defaultDarkColors()
    } else {
        AppColors.defaultLightColors()
    },
    background: AppBackground = AppBackground.defaultBackground(darkTheme),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalBackgroundTheme provides background,
    ) {
        Box(
            modifier = Modifier
                .background(background.color)
                .semantics { testTagsAsResourceId = true },
        ) {
            content()
        }
    }
}

object AppThemeProps {
    /**
     * Retrieves the current [AppColors] at the call site's position in the hierarchy.
     */
    val colors: AppColors
        @Composable
        get() = AppColors.defaultLightColors()

    /**
     * Retrieves the current [AppBackground] at the call site's position in the hierarchy.
     */
    val background: AppBackground
        @Composable
        @ReadOnlyComposable
        get() = LocalBackgroundTheme.current
}