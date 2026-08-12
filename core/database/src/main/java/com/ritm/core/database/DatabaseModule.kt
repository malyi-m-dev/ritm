package com.ritm.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRitmDatabase(@ApplicationContext context: Context): RitmDatabase {
        lateinit var database: RitmDatabase
        database = Room.databaseBuilder(context, RitmDatabase::class.java, "ritm.db")
            .fallbackToDestructiveMigration()
            .addCallback(
                object : RoomDatabase.Callback() {
                    // Срабатывает ровно один раз — когда файл БД создаётся впервые. В отличие от
                    // проверки "таблица пуста", не подсевает привычки заново, если пользователь
                    // удалил их все вручную.
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                            database.habitDao().insertAll(SeedData.habits())
                        }
                    }
                },
            )
            .build()
        return database
    }

    @Provides
    fun provideHabitDao(database: RitmDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideHabitCompletionDao(database: RitmDatabase): HabitCompletionDao =
        database.habitCompletionDao()
}
