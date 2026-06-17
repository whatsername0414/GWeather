package com.gweather.di

import android.content.Context
import androidx.room.Room
import com.gweather.data.local.AppDatabase
import com.gweather.data.local.dao.UserDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "gweather.db").build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}
