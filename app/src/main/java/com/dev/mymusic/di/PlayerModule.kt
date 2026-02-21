package com.dev.mymusic.di

import android.content.Context
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {


//    fun provideMusicPlayerController( @ApplicationContext context: Context):MusicPlayerController {
//        return MusicPlayerControllerImpl(context)
//    }
}