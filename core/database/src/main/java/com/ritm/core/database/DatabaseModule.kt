package com.ritm.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRitmDatabase(@ApplicationContext context: Context): RitmDatabase =
        Room.databaseBuilder(context, RitmDatabase::class.java, "ritm.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideHabitDao(database: RitmDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideHabitCompletionDao(database: RitmDatabase): HabitCompletionDao =
        database.habitCompletionDao()
}
