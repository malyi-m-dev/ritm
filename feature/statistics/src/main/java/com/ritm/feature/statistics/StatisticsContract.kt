package com.ritm.feature.statistics

import com.ritm.core.model.StatisticsSnapshot
import com.ritm.core.model.StatsPeriod
import com.ritm.core.mvi.UiEffect
import com.ritm.core.mvi.UiIntent
import com.ritm.core.mvi.UiState

data class StatisticsState(
    val period: StatsPeriod = StatsPeriod.WEEK,
    val snapshot: StatisticsSnapshot? = null,
    val isLoading: Boolean = true,
) : UiState

sealed interface StatisticsIntent : UiIntent {
    data class SelectPeriod(val period: StatsPeriod) : StatisticsIntent
    data object Refresh : StatisticsIntent
    data object NavigateToToday : StatisticsIntent
}

sealed interface StatisticsEffect : UiEffect {
    data object NavigateToToday : StatisticsEffect
}
