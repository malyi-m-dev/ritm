package com.ritm.feature.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ritm.core.designsystem.RitmTheme
import com.ritm.core.ui.PulseMark

@Composable
fun SplashRoute(
    onNavigateToToday: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(SplashIntent.Start)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SplashEffect.NavigateToToday -> onNavigateToToday()
            }
        }
    }

    SplashScreen(state = state)
}

@Composable
private fun SplashScreen(state: SplashState) {
    val colors = RitmTheme.colors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(17.dp),
        ) {
            PulseMark(
                modifier = Modifier.size(128.dp),
                strokeColor = colors.surface,
                backgroundColor = colors.foreground,
            )
            Text(
                text = "Ритм",
                color = colors.foreground,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                letterSpacing = (-0.035).em,
            )
            Text(
                text = "привычки на каждый день",
                color = colors.muted,
                style = RitmTheme.typography.overline,
            )
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    RitmTheme {
        SplashScreen(state = SplashState)
    }
}
