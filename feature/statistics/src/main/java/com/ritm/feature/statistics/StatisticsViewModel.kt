package com.ritm.feature.statistics

import androidx.lifecycle.viewModelScope
import com.ritm.core.data.HabitRepository
import com.ritm.core.model.StatsPeriod
import com.ritm.core.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: HabitRepository,
) : MviViewModel<StatisticsState, StatisticsIntent, StatisticsEffect>(StatisticsState()) {

    init {
        loadSnapshot(currentState.period)
    }

    override fun onIntent(intent: StatisticsIntent) {
        when (intent) {
            is StatisticsIntent.SelectPeriod -> loadSnapshot(intent.period)
            StatisticsIntent.Refresh -> loadSnapshot(currentState.period)
            StatisticsIntent.NavigateToToday -> sendEffect { StatisticsEffect.NavigateToToday }
        }
    }

    private fun loadSnapshot(period: StatsPeriod) {
        setState { copy(period = period, isLoading = true) }
        viewModelScope.launch {
            val snapshot = repository.statisticsSnapshot(period)
            setState { copy(snapshot = snapshot, isLoading = false) }
        }
    }
}
