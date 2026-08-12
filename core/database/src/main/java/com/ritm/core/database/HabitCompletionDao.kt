package com.ritm.core.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun observeForDate(date: String): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE date BETWEEN :start AND :end")
    fun observeRange(start: String, end: String): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE date BETWEEN :start AND :end")
    suspend fun getRange(start: String, end: String): List<HabitCompletionEntity>

    @Query("SELECT done FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun getDone(habitId: Long, date: String): Boolean?

    @Query("SELECT progress FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun getProgress(habitId: Long, date: String): Int?

    @Upsert
    suspend fun upsert(completion: HabitCompletionEntity)

    @Upsert
    suspend fun upsertAll(completions: List<HabitCompletionEntity>)
}
