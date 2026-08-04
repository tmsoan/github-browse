package com.anos.gitbrowse

import android.app.Application
import android.util.Log
import com.skydoves.compose.stability.runtime.ComposeStabilityAnalyzer
import com.skydoves.compose.stability.runtime.RecompositionEvent
import com.skydoves.compose.stability.runtime.RecompositionLogger
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.annotation.KoinApplication
import org.koin.ksp.generated.startKoin

@KoinApplication
class GitBrowseApp : Application() {
    override fun onCreate() {
        // generated extension: pulls in every @Configuration module found on the classpath
        startKoin {
            androidContext(this@GitBrowseApp)
            workManagerFactory()
        }
        super.onCreate()
        setupComposeStabilityAnalyzer()
    }

    private fun setupComposeStabilityAnalyzer() {
        ComposeStabilityAnalyzer.setLogger(object : RecompositionLogger {
            override fun log(event: RecompositionEvent) {
                val message = buildString {
                    append("🔄 Recomposition #${event.recompositionCount}")
                    append(" - ${event.composableName}")
                    if (event.tag.isNotEmpty()) {
                        append(" [${event.tag}]")
                    }
                    appendLine()

                    event.parameterChanges.forEach { change ->
                        append("   • ${change.name}: ${change.type}")
                        when {
                            change.changed -> append(" ➡️ CHANGED")
                            change.stable -> append(" ✅ STABLE")
                            else -> append(" ⚠️ UNSTABLE")
                        }
                        appendLine()
                    }

                    if (event.unstableParameters.isNotEmpty()) {
                        append("   ⚠️ Unstable: ${event.unstableParameters.joinToString()}")
                    }
                }
                Log.d("CustomRecomposition", message)
            }
        })
    }
}