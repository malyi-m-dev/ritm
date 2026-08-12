package com.ritm.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ritm.core.data.HabitRepository
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Срабатывает вскоре после полуночи: финализирует вчерашние ограничения (засчитывает
 * выполненными те, что не превысили лимит) и показывает пуш с итогом, затем планирует
 * следующий будильник — так цепочка держится сама, пока приложение хоть раз запускалось.
 */
@AndroidEntryPoint
class MidnightReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: HabitRepository

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val yesterday = LocalDate.now().minusDays(1)
                val summary = repository.finalizeBoundaryHabits(yesterday)
                RitmNotifications.showDailySummary(appContext, summary)
            } finally {
                MidnightScheduler.scheduleNext(appContext)
                pendingResult.finish()
            }
        }
    }
}
