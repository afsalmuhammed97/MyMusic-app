package com.dev.mymusic.ui.navigation

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.mymusic.data.model.AudioTrack
import com.dev.mymusic.ui.equlizer.EqualizerScreen
import com.dev.mymusic.ui.equlizer.EqualizerViewModel
import com.dev.mymusic.ui.playback.PlaybackScreen
import com.dev.mymusic.ui.playback.PlaybackViewModel
import com.dev.mymusic.ui.tracklist.TrackListingScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    val playbackViewModel: PlaybackViewModel = hiltViewModel(
        LocalContext.current as ComponentActivity
    )

    val equalizerViewModel: EqualizerViewModel = hiltViewModel(
        LocalContext.current as ComponentActivity
    )

    // ── Wire EqualizerViewModel to service ────────────────────────────────────
    // Whenever playback state changes (track loads, service connects),
    // give EqualizerViewModel access to the service
    val uiState by playbackViewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(uiState.currentTrack) {
        playbackViewModel.withService { service ->
            equalizerViewModel.attachService(service)  // ← attach when service ready
        }
    }
    NavHost(navController = navController, startDestination = Screens.TrackList.route) {

        composable(Screens.TrackList.route) {
            TrackListingScreen(
                playbackViewModel = playbackViewModel,
                onClick = { track ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("audioTrack", track)

                    navController.navigate(Screens.Playback.route)
                }, onEqualizerClick = {
                    navController.navigate(Screens.Equalizer.route)
                })


        }

        composable(Screens.Playback.route) {

            val audioTrack =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<AudioTrack>("audioTrack")

            Log.d("MMM", "product 2 $audioTrack")

            PlaybackScreen(
                audioTrack = audioTrack,
                viewModel = playbackViewModel,
                onBack = { navController.popBackStack() },
                onOpenEqualizer = {
                    navController.navigate(Screens.Equalizer.route)
                })
        }


        composable(Screens.Equalizer.route) {


            EqualizerScreen(
                viewModel = equalizerViewModel,
                onBack = { navController.popBackStack() })
        }


    }
}