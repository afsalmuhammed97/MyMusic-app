package com.dev.mymusic.data.repository

import com.dev.mymusic.data.datasource.AssetAudioDataSource
import com.dev.mymusic.data.model.AudioTrack
import javax.inject.Inject

/*
  repositoryImpl is the single source of truth,
  we are accessing the dataSource through the constructor
 */
class AudioRepositoryImpl @Inject constructor(
    private val dataSource: AssetAudioDataSource
): AudioRepository {



    override suspend fun getTracks(): List<AudioTrack> {
        return dataSource.loadTracks()
    }
}