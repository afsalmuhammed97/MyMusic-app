package com.dev.mymusic.data.model

import android.net.Uri
import android.os.Parcelable

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class AudioTrack(val trackId: String,
    val assetPath: Uri,
                       val title: String,
                       val artist: String?,
                       val duration: Long,
                       val albumArt: ByteArray?): Parcelable
