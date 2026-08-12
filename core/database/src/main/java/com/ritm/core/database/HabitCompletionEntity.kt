package com.ritm.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** date хранится как ISO "yyyy-MM-dd" строка, чтобы использовать её напрямую в BETWEEN-запросах. */
@Entity(
    tableName = "habit_completions",
    primaryKeys = ["habitId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("habitId"), Index("date")],
)
data class HabitCompletionEntity(
    val habitId: Long,
    val date: String,
    val progress: Int = 0,
    val done: Boolean,
)
