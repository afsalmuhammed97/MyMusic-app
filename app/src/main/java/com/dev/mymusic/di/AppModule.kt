package com.dev.mymusic.di

import android.content.Context
import android.content.SharedPreferences
import com.dev.mymusic.data.repository.AudioRepository
import com.dev.mymusic.data.repository.AudioRepositoryImpl
import dagger.Binds
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
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences =
        context.getSharedPreferences("music_player_prefs", Context.MODE_PRIVATE)
}