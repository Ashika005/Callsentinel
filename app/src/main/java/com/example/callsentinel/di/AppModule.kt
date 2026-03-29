package com.example.callsentinel.di

import android.content.Context
import com.example.callsentinel.data.db.AppDatabase
import com.example.callsentinel.data.db.CallSentinelDao
import com.example.callsentinel.data.repository.CallRepository
import com.example.callsentinel.utils.PrefsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideCallSentinelDao(database: AppDatabase): CallSentinelDao {
        return database.callSentinelDao()
    }

    @Provides
    @Singleton
    fun provideCallRepository(dao: CallSentinelDao): CallRepository {
        return CallRepository(dao)
    }

    @Provides
    @Singleton
    fun providePrefsManager(@ApplicationContext context: Context): PrefsManager {
        return PrefsManager(context)
    }
}