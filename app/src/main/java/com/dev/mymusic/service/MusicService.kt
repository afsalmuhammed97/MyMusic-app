package com.dev.mymusic.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dev.mymusic.MainActivity
import com.dev.mymusic.R
import com.dev.mymusic.data.model.AudioTrack
import com.dev.mymusic.ui.playback.model.MusicPlaybackState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicService() : Service() {

    // ── Binder
    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }


    private val binder = LocalBinder()


    // ── State
    private val _playbackState = MutableStateFlow(MusicPlaybackState())
    val playbackState: StateFlow<MusicPlaybackState> = _playbackState.asStateFlow()


    private var trackList: List<AudioTrack> = emptyList()
    private var currentIndex: Int = -1

    // ── Internals ─
    private var mediaPlayer: MediaPlayer? = null
    private var lastEqualizerGains: List<Int>? = null

    private var equalizer: Equalizer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    //lifecycle

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
       // startForeground(1001, buildNotification())
    }


    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d("MPS", "service onStartCommand action: ${intent?.action}")
        when (intent?.action) {
            ACTION_PAUSE -> pause()
            ACTION_RESUME -> resume()
            ACTION_STOP -> stopPlaybackAndSelf()
            ACTION_NEXT-> next()
            ACTION_PREV-> previous()

        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("MPS", "service bind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean = true

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onDestroy() {

        progressJob?.cancel()
        serviceScope.cancel()
        equalizer?.release()
        mediaPlayer?.release()
        equalizer = null
        mediaPlayer = null
        super.onDestroy()
    }


    fun setTrackList(tracks: List<AudioTrack>) {
        trackList = tracks
    }

    fun next() {
        Log.d("MPS", " next ")
        if (trackList.isEmpty()) return

        currentIndex = (currentIndex + 1) % trackList.size
        play(trackList[currentIndex])
    }

    fun previous() {
        if (trackList.isEmpty()) return
        // If more than 3s into track → restart it instead of going back
        if ((mediaPlayer?.currentPosition ?: 0) > 3_000) {
            seekTo(0)
            return
        }
        currentIndex = if (currentIndex <= 0) trackList.lastIndex else currentIndex - 1
        play(trackList[currentIndex])
    }

    fun applyEqualizer(gains: List<Int>) {
        lastEqualizerGains = gains
        val eq = equalizer ?: return
        try {
            gains.forEachIndexed { index, gainMb ->
                if (index < eq.numberOfBands) {
                    eq.setBandLevel(index.toShort(), gainMb.toShort())
                }
            }
        } catch (e: Exception) {
            Log.e("MMP", "Failed to apply equalizer bands", e)
        }
    }


    private fun initEqualizer(audioSessionId: Int) {
        equalizer?.release()
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }
            // Re-apply last known gains if equalizer was configured
            lastEqualizerGains?.let { applyEqualizer(it) }
        } catch (e: Exception) {
            Log.e("MMP", "Equalizer init failed", e)
        }
    }

    // Playback Controlls


    fun play(track: AudioTrack) {
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("MPS", "track in service ${track}")
        _playbackState.update {
            it.copy(isLoading = true, currentTrack = track, error = null)
        }

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(applicationContext, track.assetPath)
            setOnPreparedListener { mp ->
                mp.start()
                initEqualizer(mp.audioSessionId)
                _playbackState.update {
                    it.copy(
                        isLoading = false, isPlaying = true,
                        durationMs = mp.duration, currentPositionMs = 0
                    )
                }
                startProgressPolling()
                updateNotification(track, isPlaying = true)
            }

            setOnCompletionListener {
                progressJob?.cancel()
                _playbackState.update {
                    it.copy(isPlaying = false, currentPositionMs = 0)
                }

                updateNotification(track, isPlaying = false)
            }
            setOnErrorListener { _, what, extra ->
                _playbackState.update {
                    it.copy(isPlaying = false, isLoading = false, error = "Error $what /$extra")
                }
                true
            }


            prepareAsync()

        }


    }

    // ── Progress Polling
    private fun startProgressPolling() {
        progressJob?.cancel()
        progressJob = serviceScope.launch {
            while (isActive) {
                delay(500L)
                val position = mediaPlayer?.currentPosition ?: break
                _playbackState.update { it.copy(currentPositionMs = position) }
            }
        }
    }

    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            progressJob?.cancel()
            _playbackState.update { it.copy(isPlaying = false) }
            _playbackState.value.currentTrack?.let {
                updateNotification(it, isPlaying = false)
            }
        }
    }

    fun resume() {
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
            _playbackState.update { it.copy(isPlaying = true) }
            _playbackState.value.currentTrack?.let {
                updateNotification(it, isPlaying = true)
            }
            startProgressPolling()
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _playbackState.update { it.copy(currentPositionMs = positionMs) }
    }

    fun stopPlaybackAndSelf() {
        mediaPlayer?.release()
        mediaPlayer = null
        progressJob?.cancel()
        _playbackState.update { MusicPlaybackState() } // reset to idle
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


// ── Notification ───────────────────────────────────────────────────────────

    private fun buildNotification(
        track: AudioTrack? = null,
        isPlaying: Boolean = false
    ): Notification {

        // Tap notification → reopen app
        val openAppIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val pauseResumeIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, MusicService::class.java).apply {
                action = if (isPlaying) ACTION_PAUSE else ACTION_RESUME
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getService(
            this,
            3,
            Intent(this, MusicService::class.java).apply {
                action = ACTION_NEXT
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = PendingIntent.getService(
            this,
            4,
            Intent(this, MusicService::class.java).apply {
                action = ACTION_PREV
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this,
            2,
            Intent(this, MusicService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(track?.title ?: "Music Player")
            .setContentText(track?.artist ?: "Ready to play")
            .setSmallIcon(R.drawable.music_bg)//ic_music_note
            .setContentIntent(openAppIntent)
            .addAction(
                R.drawable.outline_arrow_back_,
                "Previous",
                prevIntent
            )
            .addAction(
                if (isPlaying) R.drawable.info_outline_24 else R.drawable.play_circle_outline_24,
                if (isPlaying) "Pause" else "Play",
                pauseResumeIntent
            )
            .addAction(
                R.drawable.outline_arrow_forward_,
                "Next",
                nextIntent
            )
            .addAction(R.drawable.outline_cancel_24, "Stop", stopIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1)
            )
            .setOngoing(isPlaying)   // dismissible only when paused
            .setSilent(true)
            .build()
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW  // no sound/vibration for media
            ).also {
                ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)
                    ?.createNotificationChannel(it)

//                ContextCompat.getSystemService(NotificationManager::class.java)
//                    .createNotificationChannel(it)
            }
        }
    }


    private fun updateNotification(track: AudioTrack, isPlaying: Boolean) {
        val notification = buildNotification(track, isPlaying)
        // Use startForeground to update — more reliable than notify() alone
        startForeground(NOTIFICATION_ID, notification)
    }


    companion object {
        const val ACTION_PAUSE = "com.yourapp.ACTION_PAUSE"
        const val ACTION_RESUME = "com.yourapp.ACTION_RESUME"
        const val ACTION_STOP = "com.yourapp.ACTION_STOP"

        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREV = "ACTION_PREV"

        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "music_playback_channel"
    }
}

