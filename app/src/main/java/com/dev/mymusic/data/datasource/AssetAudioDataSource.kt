package com.dev.mymusic.data.datasource

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.net.toUri
import com.dev.mymusic.data.model.AudioTrack
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class AssetAudioDataSource @Inject constructor(   @ApplicationContext private val context: Context) {


    fun loadTracks(): List<AudioTrack> {
        val assetManager = context.assets
        val files = assetManager.list("") ?: return emptyList()

//        return files
//            .filter { it.endsWith(".mp3") }
//            .map { fileName ->
//                extractMetadata(fileName)
//            }

        return files
            .filter { it.endsWith(".mp3", ignoreCase = true) }
            .mapNotNull { fileName ->
                createTrackFromAsset(fileName)
            }
    }


    private fun createTrackFromAsset(fileName: String): AudioTrack? {

        val fileUri = copyAssetToCache(fileName) ?: return null

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, fileUri)

        val title = retriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_TITLE
        ) ?: fileName.substringBeforeLast(".")

        val artist = retriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_ARTIST
        )

        val duration = retriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_DURATION
        )?.toLong() ?: 0L

        val albumArt = retriever.embeddedPicture

        retriever.release()

        return AudioTrack(

            assetPath = fileUri,
            title = title,
            artist = artist,
            duration = duration,
            albumArt = albumArt,
            trackId = fileName,
        )
    }

    private fun copyAssetToCache(fileName: String): Uri? {
        return try {
            val file = File(context.cacheDir, fileName)

            if (!file.exists()) {
                context.assets.open(fileName).use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            file.toURI().toString().toUri()

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


//    private fun extractMetadata(fileName: String): AudioTrack {
//        val retriever = MediaMetadataRetriever()
//
//        val afd = context.assets.openFd(fileName)
//        retriever.setDataSource(
//            afd.fileDescriptor,
//            afd.startOffset,
//            afd.length
//        )
//
//        val title = retriever.extractMetadata(
//            MediaMetadataRetriever.METADATA_KEY_TITLE
//        ) ?: fileName
//
//        val artist = retriever.extractMetadata(
//            MediaMetadataRetriever.METADATA_KEY_ARTIST
//        )
//
//        val duration = retriever.extractMetadata(
//            MediaMetadataRetriever.METADATA_KEY_DURATION
//        )?.toLong() ?: 0L
//
//        val albumArt = retriever.embeddedPicture
//
//        retriever.release()
//
//        return AudioTrack(
//            assetPath = fileName,
//            title = title,
//            artist = artist,
//            duration = duration,
//            albumArt = albumArt
//        )
//    }
}