package com.anos.gitbrowse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.metrics.performance.JankStats
import com.anos.gitbrowse.ui.GitBrowseMain
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope

class MainActivity : ComponentActivity(), AndroidScopeComponent {
    // Koin scope
    override val scope: Scope by activityScope()

    /**
     * Lazily resolved [JankStats], which is used to track jank throughout the app.
     * Resolved from the activity [scope] on first use (in [onResume]) so the activity window
     * it tracks is already created.
     */
    private val lazyStats: JankStats by lazy { scope.get() }

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
        lazyStats.isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        lazyStats.isTrackingEnabled = false
    }
}
