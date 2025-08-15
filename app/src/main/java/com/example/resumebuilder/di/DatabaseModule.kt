// File: app/src/main/java/com/example/resumebuilder/di/DatabaseModule.kt
package com.example.resumebuilder.di

import android.content.Context
import androidx.room.Room
import com.example.resumebuilder.data.local.AppDatabase
import com.example.resumebuilder.data.local.ResumeDao
import com.example.resumebuilder.data.repository.IResumeRepository
import com.example.resumebuilder.data.repository.ResumeRepository
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .build()
    }

    @Provides
    fun provideResumeDao(database: AppDatabase): ResumeDao {
        return database.resumeDao()
    }

    @Provides
    @Singleton
    fun provideResumeRepository(resumeDao: ResumeDao): IResumeRepository {
        return ResumeRepository(resumeDao)
    }
}