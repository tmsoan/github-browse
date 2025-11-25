package com.anos.gitbrowse.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.anos.details.ui.DetailsRoute
import com.anos.home.ui.HomeRoute
import com.anos.navigation.AppNavigatorImpl
import com.anos.navigation.LocalComposeNavigator
import com.anos.navigation.ScreenRoute
import com.skydoves.compose.stability.runtime.TraceRecomposition

@TraceRecomposition
@Composable
fun AppNavHost() {
    val backStack = rememberNavBackStack(ScreenRoute.HomeScreen)
    val navigator = remember(backStack) { AppNavigatorImpl(backStack = backStack) }

    CompositionLocalProvider(
        LocalComposeNavigator provides navigator,
    ) {
        SharedTransitionLayout {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
                entryProvider = entryProvider<NavKey> {
                    entry<ScreenRoute.HomeScreen> {
                        HomeRoute()
                    }
                    entry<ScreenRoute.DetailsScreen> {
                        DetailsRoute(it.repo)
                    }
                }
            )
        }
    }
}
