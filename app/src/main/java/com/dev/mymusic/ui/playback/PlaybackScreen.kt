package com.dev.mymusic.ui.playback

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.mymusic.data.model.AudioTrack
import com.dev.mymusic.ui.playback.componants.PlaybackContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackScreen(
    modifier: Modifier = Modifier, audioTrack: AudioTrack?,
    viewModel: PlaybackViewModel
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Auto-play when screen opens with a new track
    LaunchedEffect(audioTrack) {

        audioTrack?.let {
            Log.d("MMP", "track 12 ${audioTrack?.title}")
            viewModel.play(it)
        }
    }

    // Pause on background, resume on foreground
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    val activity = (lifecycleOwner as? ComponentActivity)
                    if (activity?.isChangingConfigurations == false &&
                        activity?.isFinishing == false
                    ) {
                        viewModel.pause()
                    }
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (uiState.isPlaying) viewModel.resume()
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    PlaybackContent(
        uiState = uiState,
        onPlayPause = {
            if (uiState.isPlaying) viewModel.pause() else viewModel.resume()
        },
        onSeek = { viewModel.seekTo(it) },
        onNext = { viewModel.next() },
        onPrev = { viewModel.previous() },
        modifier = modifier
    )



}