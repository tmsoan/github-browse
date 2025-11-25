package com.anos.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anos.ui.R

@Immutable
public data class AppBackground(
    val color: Color = Color.Unspecified,
    val tonalElevation: Dp = Dp.Unspecified,
) {
    public companion object Companion {
        @Composable
        public fun defaultBackground(darkTheme: Boolean): AppBackground {
            return if (darkTheme) {
                AppBackground(
                    color = colorResource(id = R.color.background_dark),
                    tonalElevation = 0.dp,
                )
            } else {
                AppBackground(
                    color = colorResource(id = R.color.background),
                    tonalElevation = 0.dp,
                )
            }
        }
    }
}

public val LocalBackgroundTheme: ProvidableCompositionLocal<AppBackground> =
    staticCompositionLocalOf { AppBackground() }
