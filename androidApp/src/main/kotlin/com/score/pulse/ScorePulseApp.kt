package com.score.pulse

import android.app.Application
import com.score.pulse.di.initKoin
import org.koin.android.ext.koin.androidContext

class ScorePulseApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@ScorePulseApp)
        }
    }
}