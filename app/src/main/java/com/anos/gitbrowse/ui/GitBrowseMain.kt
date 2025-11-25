package com.anos.gitbrowse.ui

import androidx.compose.runtime.Composable
import com.anos.gitbrowse.navigation.AppNavHost
import com.anos.ui.theme.AppTheme

@Composable
fun GitBrowseMain() {
    AppTheme {
        AppNavHost()
    }
}
