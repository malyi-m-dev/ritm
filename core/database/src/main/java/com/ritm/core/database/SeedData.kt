package com.ritm.core.database

/**
 * Стартовый набор — те же 6 привычек, что были в вебовском прототипе (mobile-android.html),
 * теперь как реальные строки в Room. Без фейковой истории выполнения — статистика на чистой
 * базе честно показывает отсутствие данных, а не выдуманные цифры.
 */
internal object SeedData {

    fun habits(): List<HabitEntity> = listOf(
        HabitEntity(
            name = "Прогулка",
            kind = "POSITIVE",
            scheduleType = "DAILY",
            scheduleDays = null,
            targetAmount = 20,
            unit = "минут",
            subtitle = "20 минут до работы",
            reminderEnabled = false,
            reminderTime = null,
            sortOrder = 0,
        ),
        HabitEntity(
            name = "Вода",
            kind = "POSITIVE",
            scheduleType = "DAILY",
            scheduleDays = null,
            targetAmount = 8,
            unit = "стаканов",
            subtitle = "До обеда",
            reminderEnabled = false,
            reminderTime = null,
            sortOrder = 1,
        ),
        HabitEntity(
            name = "Чтение",
            kind = "POSITIVE",
            scheduleType = "DAILY",
            scheduleDays = null,
            targetAmount = 20,
            unit = "минут",
            subtitle = "Без уведомлений",
            reminderEnabled = false,
            reminderTime = null,
            sortOrder = 2,
        ),
        HabitEntity(
            name = "Сон до 23:30",
            kind = "POSITIVE",
            scheduleType = "DAILY",
            scheduleDays = null,
            targetAmount = 1,
            unit = "раз",
            subtitle = "Вечерний режим",
            reminderEnabled = false,
            reminderTime = null,
            sortOrder = 3,
        ),
        HabitEntity(
            name = "Без сладкого",
            kind = "BOUNDARY",
            scheduleType = "DAILY",
            scheduleDays = null,
            targetAmount = 0,
            unit = "раз",
            subtitle = "Сегодня без исключений",
            reminderEnabled = false,
            reminderTime = null,
            sortOrder = 4,
        ),
        HabitEntity(
            name = "Соцсети",
            kind = "BOUNDARY",
            scheduleType = "DAILY",
            scheduleDays = null,
            targetAmount = 60,
            unit = "минут",
            subtitle = "Лимит до вечера",
            reminderEnabled = false,
            reminderTime = null,
            sortOrder = 5,
        ),
    )
}
