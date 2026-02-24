package com.dev.mymusic.ui.navigation


sealed class Screens (val route: String) {

    object TrackList : Screens("track_list")

    object Playback : Screens("playback")

    object Equalizer: Screens("equalizer")

//    object Details : Screens("details/{trackId}") {
//        fun createRoute(trackId: String) = "details/$trackId"
//    }
}