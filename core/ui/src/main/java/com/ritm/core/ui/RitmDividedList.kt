package com.ritm.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key as composeKey
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ritm.core.designsystem.RitmTheme

/**
 * Аналог .habit-list / .insight-list — карточка-контейнер с рамкой и скруглением,
 * дети разделены тонкой линией, последний элемент — без разделителя снизу.
 *
 * [itemKey] обязателен для списков, из которых элементы могут удаляться посреди работы
 * (например, привычки): без стабильного ключа Compose при сдвиге позиций переиспользует
 * remember-состояние строки (в т.ч. состояние свайпа) для другого элемента, и удаление/свайп
 * начинают попадать не туда. Для статичных списков вроде инсайтов сойдёт ключ по индексу.
 */
@Composable
fun <T> RitmDividedList(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemKey: (T) -> Any = { it as Any },
    itemContent: @Composable (T) -> Unit,
) {
    val colors = RitmTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(18.dp)),
    ) {
        items.forEachIndexed { index, item ->
            composeKey(itemKey(item)) {
                itemContent(item)
                if (index != items.lastIndex) {
                    HorizontalDivider(color = colors.border, thickness = 1.dp)
                }
            }
        }
    }
}
