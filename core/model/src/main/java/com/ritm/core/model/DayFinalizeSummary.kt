package com.ritm.core.model

/** Итог ночной финализации ограничений за прошедший день — основа текста пуш-уведомления. */
data class DayFinalizeSummary(
    val keptNames: List<String>,
    val exceededNames: List<String>,
) {
    val keptCount: Int get() = keptNames.size
    val exceededCount: Int get() = exceededNames.size
    val isEmpty: Boolean get() = keptCount == 0 && exceededCount == 0
}
