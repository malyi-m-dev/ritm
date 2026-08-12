package com.ritm.feature.splash

import androidx.lifecycle.viewModelScope
import com.ritm.core.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_DELAY_MS = 1400L

@HiltViewModel
class SplashViewModel @Inject constructor() :
    MviViewModel<SplashState, SplashIntent, SplashEffect>(SplashState) {

    override fun onIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.Start -> viewModelScope.launch {
                delay(SPLASH_DELAY_MS)
                sendEffect { SplashEffect.NavigateToToday }
            }
        }
    }
}
