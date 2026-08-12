package com.ritm.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** После перезагрузки устройства будильники сбрасываются — планируем заново. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            MidnightScheduler.scheduleNext(context.applicationContext)
        }
    }
}
