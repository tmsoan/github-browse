package com.anos.gitbrowse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.metrics.performance.JankStats
import com.anos.gitbrowse.ui.GitBrowseMain
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
            GitBrowseMain()
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
