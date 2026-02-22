package com.dev.mymusic.ui.equlizer.compoants

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.dev.mymusic.ui.theme.AccentGreen
import com.dev.mymusic.ui.theme.SurfaceCard

@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,modifier: Modifier = Modifier) {


    // Compose doesn't have a vertical slider natively — rotate horizontal one
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = AccentGreen,
                activeTrackColor = AccentGreen,
                inactiveTrackColor = SurfaceCard
            ),
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = -90f
                    // Width becomes height after rotation
                }
                .width(160.dp)
        )
    }
    
}