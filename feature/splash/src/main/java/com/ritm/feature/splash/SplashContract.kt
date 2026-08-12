package com.ritm.feature.splash

import com.ritm.core.mvi.UiEffect
import com.ritm.core.mvi.UiIntent
import com.ritm.core.mvi.UiState

data object SplashState : UiState

sealed interface SplashIntent : UiIntent {
    data object Start : SplashIntent
}

sealed interface SplashEffect : UiEffect {
    data object NavigateToToday : SplashEffect
}
