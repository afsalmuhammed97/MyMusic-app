package com.dev.mymusic.ui.tracklist

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.mymusic.data.model.AudioTrack
import com.dev.mymusic.ui.playback.PlaybackViewModel
import com.dev.mymusic.ui.tracklist.componats.MiniPlayer
import com.dev.mymusic.ui.tracklist.componats.TrackItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackListingScreen(
    modifier: Modifier = Modifier,
    viewModel: TrackListViewModel = hiltViewModel(),
    playbackViewModel: PlaybackViewModel,
    onClick: (AudioTrack) -> Unit, onEqualizerClick: () -> Unit
) {


    val playbackState by playbackViewModel.uiState.collectAsStateWithLifecycle()

    val tracks by viewModel.tracks.collectAsState()

    LaunchedEffect(tracks) {
        if (tracks.isNotEmpty()) {
            playbackViewModel.setTrackList(tracks)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "My music")
                },
                actions = {
                    IconButton(onClick = {
                        onEqualizerClick()
                    }) {
                        Icon(imageVector = Icons.Default.Equalizer, contentDescription = "")
                    }
                }
            )
        }, bottomBar = {
            AnimatedVisibility(
                visible = playbackState.currentTrack != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()

            ) {
                MiniPlayer(
                    uiState = playbackState,
                    onPlay = { playbackViewModel.resume() },
                    onPause = { playbackViewModel.pause() },
                    onNext = { playbackViewModel.next() },
                    onPrev = { playbackViewModel.previous() },
                    onNavigate = {

                        playbackState.currentTrack?.let {
                            onClick(it)
                        }

                    },
                     modifier =modifier.padding(bottom = 16.dp)
                )

            }
        }
    ) { padding ->


        LazyColumn(contentPadding = padding) {
            items(tracks) { track ->
                Log.d("MMM", "$track")
                // Text(text = track.title, modifier = modifier.padding(padding))
                TrackItem(modifier = modifier.padding(6.dp), track) { item ->
                    onClick(item)
                }
            }
        }
    }


}