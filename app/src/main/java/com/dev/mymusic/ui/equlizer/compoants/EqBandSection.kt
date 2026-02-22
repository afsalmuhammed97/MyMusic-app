package com.dev.mymusic.ui.equlizer.compoants

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val BAND_LABELS = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")
@Composable
fun EqBandSection( bandGains: List<Int>,
                   onBandChanged: (Int, Int) -> Unit,modifier: Modifier = Modifier) {


    // Animated curve drawn above sliders
    val animatedGains = bandGains.map { gain ->
        animateIntAsState(
            targetValue = gain,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 200f),
            label = "gain"
        ).value
    }

    Column(modifier = modifier) {

        // Curve visualizer
        EqCurveCanvas(
            gains = animatedGains,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(bottom = 8.dp)
        )

        // 5 vertical sliders side by side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bandGains.forEachIndexed { index, gain ->
                EqBandSlider(
                    label = BAND_LABELS[index],
                    gainMb = gain,
                    onGainChanged = { onBandChanged(index, it) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

}