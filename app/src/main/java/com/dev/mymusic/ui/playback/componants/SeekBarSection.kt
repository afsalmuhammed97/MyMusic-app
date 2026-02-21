package com.dev.mymusic.ui.playback.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.dev.mymusic.utils.toFormattedTime

@Composable
fun SeekBarSection(
    currentMs: Int,
    durationMs: Int,
    onSeek: (Int) -> Unit
) {
    val progress = if (durationMs > 0) currentMs.toFloat() / durationMs.toFloat() else 0f

    Column(modifier = Modifier.fillMaxWidth()) {

        Slider(
            value = progress,
            onValueChange = { fraction ->
                onSeek((fraction * durationMs).toInt())
            },
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF6C63FF),
                activeTrackColor = Color(0xFF6C63FF),
                inactiveTrackColor = Color(0xFF444444)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentMs.toFormattedTime(),
                color = Color(0xFFAAAAAA),
                fontSize = 13.sp
            )
            Text(
                text = durationMs.toFormattedTime(),
                color = Color(0xFFAAAAAA),
                fontSize = 13.sp
            )
        }
    }
}