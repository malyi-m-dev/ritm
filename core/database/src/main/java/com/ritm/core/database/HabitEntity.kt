package com.ritm.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * kind: "POSITIVE" | "BOUNDARY". scheduleType: "DAILY" | "WEEKDAYS" | "CUSTOM".
 * scheduleDays: comma-separated java.time.DayOfWeek names (only set when scheduleType == CUSTOM).
 * reminderTime: "HH:mm" or null. Enums/dates are stored as plain strings on purpose — keeps
 * this module free of TypeConverters and the schema trivially readable in a DB inspector.
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val kind: String,
    val scheduleType: String,
    val scheduleDays: String?,
    val targetAmount: Int,
    val unit: String,
    val subtitle: String,
    val reminderEnabled: Boolean,
    val reminderTime: String?,
    val sortOrder: Int,
)
