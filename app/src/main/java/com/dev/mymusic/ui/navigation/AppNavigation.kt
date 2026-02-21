package com.dev.mymusic.ui.navigation

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.mymusic.data.model.AudioTrack
import com.dev.mymusic.ui.playback.PlaybackScreen
import com.dev.mymusic.ui.playback.PlaybackViewModel
import com.dev.mymusic.ui.tracklist.TrackListingScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    val playbackViewModel: PlaybackViewModel = hiltViewModel(
        LocalContext.current as ComponentActivity
    )

    NavHost(navController = navController, startDestination = Screens.TrackList.route){

        composable (Screens.TrackList.route){
            TrackListingScreen(playbackViewModel=playbackViewModel){track->

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("audioTrack", track)

                navController.navigate(Screens.Playback.route)
            }
        }

        composable(Screens.Playback.route) {

            val audioTrack =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<AudioTrack>("audioTrack")

            Log.d("MMM","product 2 $audioTrack",)

            PlaybackScreen(audioTrack = audioTrack, viewModel = playbackViewModel)
        }


    }
}