package com.anos.gitbrowse.di

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.JankStats.OnFrameListener
import io.kotzilla.sdk.KotzillaSDK
import org.koin.android.annotation.ActivityScope
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module

@Module
@Configuration
class JankStatsKoinModule {

    @ActivityScope
    fun jankStats(activity: ComponentActivity): JankStats {
        return JankStats.createAndTrack(activity.window, providesOnFrameListener())
    }

    fun providesOnFrameListener(): OnFrameListener = OnFrameListener { frameData ->
        // Make sure to only log janky frames.
        if (frameData.isJank) {
            // We're currently logging this but would better report it to a backend.
            Log.v("NiA Jank", frameData.toString())
            KotzillaSDK.log("NiA Jank - $frameData")
        }
    }
}
