package com.dev.mymusic.ui.playback

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.mymusic.data.model.AudioTrack
import com.dev.mymusic.service.MusicService
import com.dev.mymusic.ui.playback.model.MusicPlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel@Inject constructor(private val application: Application): AndroidViewModel(application) {
    private var musicService: MusicService? = null

    private var isBound = false

    private val _uiState = MutableStateFlow(MusicPlaybackState())
    val uiState: StateFlow<MusicPlaybackState> = _uiState.asStateFlow()
    private var trackList: List<AudioTrack> = emptyList()
    private var selectedTrack: AudioTrack? = null

    private var serviceStateJob: Job? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            binder: IBinder?
        ) {
            Log.d("MMP", "onServiceConnected fired")
            val service=(binder as MusicService.LocalBinder).getService()
            musicService = service
            isBound = true

            if (trackList.isNotEmpty()) {
                service.setTrackList(trackList)
            }

            selectedTrack?.let {
                Log.d("MMP", "service ready — now playing selected track: ${it.title}")
                service.play(it)
                selectedTrack = null
            }

            // Collect service state whenever UI reconnects
            serviceStateJob?.cancel()
            serviceStateJob= viewModelScope.launch {
                Log.d("MMP", "starting to collect playbackState")
                service.playbackState.collect { state->

                    Log.d("MMP", "state received: $state")
                    _uiState.update {
                        it.copy(
                            isPlaying        = state.isPlaying,
                            isLoading        = state.isLoading,
                            currentTrack     = state.currentTrack,
                            currentPositionMs = state.currentPositionMs,
                            durationMs       = state.durationMs,
                            error            = state.error
                        )
                    }
                }
            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("MMP", "onServiceDisconnected fired")
            musicService=null
            isBound=false
            serviceStateJob?.cancel()
        }






    }

    private fun startAndBindService(){
        Log.d("MMP", "startAndBindService called")
        val intent = Intent(application, MusicService::class.java)

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            application.startForegroundService(intent)
        }else{
            application.startService(intent)
        }

      //  application.bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE)

        val bindResult = application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d("MMP", "bindService result: $bindResult")
    }

    // ── Public API for UI ─────────────────────────────────────────────────────

    fun play(track: AudioTrack) {
        Log.d("MMP", "play() called — musicService is null: ${musicService == null}")
        Log.d("MMP","track in viewmodel ${track?.title}")

       // musicService?.play(track)


        if (musicService == null) {
            // Service not ready yet — remember this track, play it in onServiceConnected
            selectedTrack = track
            // Immediately reflect in UI so screen shows track info while loading
            _uiState.update { it.copy(currentTrack = track, isLoading = true) }
            return
        }
        musicService?.play(track)
    }
    fun pause()                  = musicService?.pause()
    fun resume()                 = musicService?.resume()
    fun seekTo(positionMs: Int)  = musicService?.seekTo(positionMs)
    fun next() =                  musicService?.next()

    fun previous() =             musicService?.previous()
    fun stopMusic()              = musicService?.stopPlaybackAndSelf()



    fun setTrackList(tracks: List<AudioTrack>) {
        trackList = tracks
        musicService?.setTrackList(tracks) // might be null if not bound yet
    }


    override fun onCleared() {
        serviceStateJob?.cancel()
        // Unbind only — service keeps running
        if (isBound) {
            application.unbindService(serviceConnection)
            isBound = false
        }
        super.onCleared()
    }


    fun withService(block: (MusicService) -> Unit) {
        musicService?.let(block)
    }

    init {
        startAndBindService()
    }
}