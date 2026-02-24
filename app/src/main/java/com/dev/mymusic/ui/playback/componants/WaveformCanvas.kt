package com.dev.mymusic.ui.playback.componants

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.dev.mymusic.ui.theme.glowColor
import com.dev.mymusic.ui.theme.playedColor
import com.dev.mymusic.ui.theme.unplayedColor

@Composable
fun WaveformCanvas( amplitudes: List<Float>,
                    progress: Float,           // 0f to 1f
    modifier: Modifier = Modifier) {


    if (amplitudes.isEmpty()) return



    // Animate progress line
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "waveformProgress"
    )

    Canvas(modifier = modifier) {
        val totalWidth    = size.width
        val totalHeight   = size.height
        val midY          = totalHeight / 2f
        val barCount      = amplitudes.size
        val barWidth      = totalWidth / (barCount * 1.6f)
        val barSpacing    = totalWidth / barCount
        val progressX     = totalWidth * animatedProgress
        val minBarHeight  = 4.dp.toPx()

        amplitudes.forEachIndexed { index, amplitude ->
            val x           = index * barSpacing + barSpacing / 2f
            val barHeight   = (amplitude * (totalHeight * 0.85f)).coerceAtLeast(minBarHeight)
            val isPlayed    = x <= progressX
            val color       = if (isPlayed) playedColor else unplayedColor

            // Glow effect on played bars
//            if (isPlayed) {
//                drawRect(
//                    color = glowColor,
//                    topLeft = Offset(x - barWidth / 2f - 2.dp.toPx(), midY - barHeight / 2f),
//                    size = Size(barWidth + 4.dp.toPx(), barHeight),
//                )
//            }

            // Main bar — rounded via strokeCap
            drawLine(
                color = color,
                start = Offset(x, midY - barHeight / 2f),
                end   = Offset(x, midY + barHeight / 2f),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }




        // Playhead line
        if (progress > 0f) {
            drawLine(
                color = Color.White.copy(alpha = 0.9f),
                start = Offset(progressX, 0f),
                end   = Offset(progressX, totalHeight),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }




}