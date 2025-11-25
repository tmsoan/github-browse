package com.anos.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Navigator interface for Nav3 backstack navigation.
 */
interface AppNavigator {
    /**
     * Navigate to a new screen by adding it to the back stack.
     */
    fun navigate(screen: ScreenRoute)

    /**
     * Navigate back by removing the top screen from the back stack.
     * @return true if navigation was successful, false if back stack is empty or only has one item.
     */
    fun navigateUp(): Boolean
}

/**
 * Implementation of [AppNavigator] that operates on a [NavBackStack].
 */
class AppNavigatorImpl(
    private val backStack: NavBackStack<NavKey>,
) : AppNavigator {
    override fun navigate(screen: ScreenRoute) {
        backStack.add(screen)
    }

    override fun navigateUp(): Boolean {
        return if (backStack.size > 1) {
            backStack.removeLastOrNull() != null
        } else {
            false
        }
    }
}
