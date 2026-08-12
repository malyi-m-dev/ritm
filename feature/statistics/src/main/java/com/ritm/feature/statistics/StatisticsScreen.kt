package com.ritm.feature.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ritm.core.designsystem.DisplayFontFamily
import com.ritm.core.designsystem.MonoFontFamily
import com.ritm.core.designsystem.RitmTheme
import com.ritm.core.model.StatsPeriod
import com.ritm.core.ui.BarChart
import com.ritm.core.ui.InsightRow
import com.ritm.core.ui.MetricCard
import com.ritm.core.ui.RitmBottomNavBar
import com.ritm.core.ui.RitmDividedList
import com.ritm.core.ui.RitmNavDestination
import com.ritm.core.ui.TopBrandBar

@Composable
fun StatisticsRoute(
    onNavigateToToday: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.onIntent(StatisticsIntent.Refresh)
        viewModel.effect.collect { effect ->
            when (effect) {
                StatisticsEffect.NavigateToToday -> onNavigateToToday()
            }
        }
    }

    StatisticsScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
private fun StatisticsScreen(state: StatisticsState, onIntent: (StatisticsIntent) -> Unit) {
    val colors = RitmTheme.colors
    val snapshot = state.snapshot

    Scaffold(
        containerColor = colors.surface,
        topBar = { TopBrandBar() },
        bottomBar = {
            RitmBottomNavBar(
                selected = RitmNavDestination.STATISTICS,
                onSelect = { destination ->
                    if (destination == RitmNavDestination.TODAY) onIntent(StatisticsIntent.NavigateToToday)
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.surface)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding() + 24.dp,
            ),
        ) {
            item {
                Intro(rangeLabel = snapshot?.rangeLabel ?: "")
                PeriodToggle(
                    selected = state.period,
                    onSelect = { onIntent(StatisticsIntent.SelectPeriod(it)) },
                    modifier = Modifier.padding(bottom = 17.dp),
                )
            }
            if (snapshot != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 25.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        MetricCard(
                            label = "Дни в ритме",
                            value = "${snapshot.daysInRhythm} из ${snapshot.totalDays}",
                            caption = snapshot.rhythmCaption,
                            modifier = Modifier.weight(1f),
                        )
                        MetricCard(
                            label = "Лучше всего",
                            value = snapshot.bestHabitName,
                            caption = snapshot.bestHabitCaption,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                item {
                    SectionHeader(title = "Выполнение по дням", trailing = snapshot.chartLabel)
                    BarChart(bars = snapshot.bars, modifier = Modifier.padding(bottom = 25.dp))
                }
                item {
                    SectionHeader(title = "Наблюдения", trailing = "")
                    RitmDividedList(items = snapshot.insights) { insight ->
                        InsightRow(insight = insight)
                    }
                    Text(
                        text = "Значения на графике посчитаны из твоей реальной истории отметок в этом приложении.",
                        color = colors.muted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun Intro(rangeLabel: String) {
    val colors = RitmTheme.colors
    Column(modifier = Modifier.padding(top = 13.dp, bottom = 18.dp)) {
        Text(
            text = rangeLabel,
            color = colors.muted,
            fontFamily = MonoFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.09.em,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Text(
            text = "Статистика",
            color = colors.foreground,
            fontFamily = DisplayFontFamily,
            fontSize = 44.sp,
            letterSpacing = (-0.055).em,
            modifier = Modifier.padding(bottom = 7.dp),
        )
        Text(text = "Привычки работают, когда их видно в динамике.", color = colors.muted, fontSize = 14.sp)
    }
}

@Composable
private fun PeriodToggle(selected: StatsPeriod, onSelect: (StatsPeriod) -> Unit, modifier: Modifier = Modifier) {
    val colors = RitmTheme.colors
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        listOf(StatsPeriod.WEEK to "Неделя", StatsPeriod.MONTH to "Месяц").forEach { (period, label) ->
            val isSelected = period == selected
            Box(
                modifier = Modifier
                    .heightIn(min = 38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, if (isSelected) colors.foreground else colors.border, RoundedCornerShape(10.dp))
                    .background(if (isSelected) colors.foreground else Color.Transparent)
                    .clickable { onSelect(period) }
                    .padding(horizontal = 15.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isSelected) colors.surface else colors.foreground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, trailing: String) {
    val colors = RitmTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp, bottom = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(title, color = colors.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        if (trailing.isNotEmpty()) {
            Text(trailing, color = colors.muted, fontFamily = MonoFontFamily, fontSize = 9.sp)
        }
    }
}

@Preview
@Composable
private fun StatisticsScreenPreview() {
    RitmTheme {
        StatisticsScreen(state = StatisticsState(), onIntent = {})
    }
}
