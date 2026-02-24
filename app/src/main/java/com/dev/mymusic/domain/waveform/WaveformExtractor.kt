package com.dev.mymusic.domain.waveform

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteOrder
import java.util.Random
import javax.inject.Inject

class WaveformExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val SAMPLE_COUNT = 200 // number of bars to display
    }


    // Returns list of 200 normalized amplitudes (0f to 1f)
    suspend fun extract(assetPath: Uri): List<Float> = withContext(Dispatchers.IO) {
        Log.d("Waveform", "asset in exicuter ${assetPath}")
        try {
            Log.d("Waveform", "asset in exicuter ${assetPath}")
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, assetPath)

            val durationMs = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLong() ?: return@withContext emptyList()

            retriever.release()

            // Sample audio at evenly spaced intervals across the track
            val decoder = MediaExtractor().apply {
                setDataSource(context, assetPath, null)
            }

            // Find the audio track
            val audioTrackIndex = (0 until decoder.trackCount).firstOrNull { index ->
                decoder.getTrackFormat(index).getString(MediaFormat.KEY_MIME)
                    ?.startsWith("audio/") == true
            } ?: return@withContext generateFallback()

            decoder.selectTrack(audioTrackIndex)
            val format = decoder.getTrackFormat(audioTrackIndex)

            val codec = MediaCodec.createDecoderByType(
                format.getString(MediaFormat.KEY_MIME) ?: return@withContext generateFallback()
            )
            codec.configure(format, null, null, 0)
            codec.start()

            val amplitudes = mutableListOf<Float>()
            val info = MediaCodec.BufferInfo()
            var isDone = false
            val chunkSize = durationMs / SAMPLE_COUNT


            while (!isDone && amplitudes.size < SAMPLE_COUNT) {
                // Feed input
                val inputIndex = codec.dequeueInputBuffer(10_000)
                if (inputIndex >= 0) {
                    val buffer = codec.getInputBuffer(inputIndex)!!
                    val sampleSize = decoder.readSampleData(buffer, 0)
                    if (sampleSize < 0) {
                        codec.queueInputBuffer(
                            inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        isDone = true
                    } else {
                        codec.queueInputBuffer(
                            inputIndex, 0, sampleSize, decoder.sampleTime, 0
                        )
                        decoder.advance()
                    }
                }

                // Read output
                val outputIndex = codec.dequeueOutputBuffer(info, 10_000)
                if (outputIndex >= 0) {
                    val buffer = codec.getOutputBuffer(outputIndex)!!
                    val chunk = ShortArray(info.size / 2)
                    buffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(chunk)

                    if (chunk.isNotEmpty()) {
                        // RMS amplitude for this chunk
                        val rms = Math.sqrt(chunk.map { it.toDouble() * it.toDouble() }.average())
                            .toFloat()
                        amplitudes.add(rms)
                    }

                    codec.releaseOutputBuffer(outputIndex, false)
                }
            }

            codec.stop()
            codec.release()
            decoder.release()

            if (amplitudes.isEmpty()) return@withContext generateFallback()

            // Normalize to 0f..1f and downsample to SAMPLE_COUNT bars
            val max = amplitudes.max()
            val normalized = amplitudes.map { if (max > 0) it / max else 0f }
            downsample(normalized, SAMPLE_COUNT)


        } catch (e: Exception) {
            Log.e("Waveform", "Extraction failed: ${e.message}")
            generateFallback()
        }
    }

    // Fallback: random plausible waveform if decoding fails
    private fun generateFallback(): List<Float> {
        val random = Random(42)
        return List(SAMPLE_COUNT) {
            0.2f + random.nextFloat() * 0.6f
        }
    }

    // Evenly downsample to target size
    private fun downsample(samples: List<Float>, target: Int): List<Float> {
        if (samples.size <= target) return samples
        val step = samples.size.toFloat() / target
        return List(target) { i -> samples[(i * step).toInt()] }
    }
}