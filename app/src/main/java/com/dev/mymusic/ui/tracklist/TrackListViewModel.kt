package com.dev.mymusic.ui.tracklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.mymusic.data.model.AudioTrack
import com.dev.mymusic.data.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackListViewModel  @Inject constructor(
    private val repository: AudioRepository
): ViewModel() {


    private val _tracks= MutableStateFlow<List<AudioTrack>>(emptyList())
     val tracks: StateFlow<List<AudioTrack>> =_tracks


    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            _tracks.value = repository.getTracks()
        }
    }
}