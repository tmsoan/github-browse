package com.anos.gitbrowse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.metrics.performance.JankStats
import androidx.navigation.compose.rememberNavController
import com.anos.gitbrowse.navigation.AppNavHost
import com.anos.ui.theme.GitBrowseColors
import com.anos.ui.theme.GitBrowseTheme
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope

class MainActivity : ComponentActivity(), AndroidScopeComponent {
    // Koin scope
    override val scope: Scope by activityScope()

    /**
     * Lazily inject [JankStats], which is used to track jank throughout the app.
     */
//    private val lazyStats: JankStats by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            GitBrowseTheme {
                Surface(
                    modifier = Modifier
                        .systemBarsPadding()
                        .fillMaxSize(),
                    color = GitBrowseColors.colors.background
                ) {
                    AppNavHost(
                        navHostController = navController
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        lazyStats.isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
//        lazyStats.isTrackingEnabled = false
    }
}
