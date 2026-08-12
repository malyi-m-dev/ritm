package com.ritm.app

import android.app.Application
import com.ritm.app.notifications.MidnightScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RitmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MidnightScheduler.scheduleNext(this)
    }
}
