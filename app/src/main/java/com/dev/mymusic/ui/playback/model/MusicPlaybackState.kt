package com.dev.mymusic.ui.playback.model

import com.dev.mymusic.data.model.AudioTrack

data class MusicPlaybackState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentTrack: AudioTrack? = null,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val error: String? = null
)
