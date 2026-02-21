package com.dev.mymusic.data.repository

import com.dev.mymusic.data.model.AudioTrack

interface AudioRepository {
    suspend fun getTracks(): List<AudioTrack>
}