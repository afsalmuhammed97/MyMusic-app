package com.dev.mymusic.ui.equlizer.compoants


import androidx.collection.mutableFloatSetOf
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.mymusic.ui.theme.AccentGreen




@Composable
fun RotaryKnob(label: String,
               value: Float,          // 0f to 1f
               onValueChange: (Float) -> Unit,
               accentColor: Color = Color(0xFF00E5A0), modifier: Modifier = Modifier) {


    val knobSize = 80.dp
    val currentValue by rememberUpdatedState(value)
    var rotation by remember { mutableFloatStateOf(valueToAngle(value)) }


    // Sync external value changes
    LaunchedEffect(value) {
        rotation = valueToAngle(value)
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // dB label above knob
        Text(
            text = "${((value * 30f) - 15f).toInt()}dB",
            color = accentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        // Knob
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(knobSize)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()

                        val delta    = -dragAmount.y / 200f
                        val newValue = (currentValue + delta).coerceIn(0f, 1f)
                        onValueChange(newValue)
                        // Drag up = increase, drag down = decrease
//
//                        val delta = -dragAmount.y / 300f
//                        val newValue = (value + delta).coerceIn(0f, 1f)
//                        onValueChange(newValue)
                    }
                }
        ) {
            KnobCanvas(
                rotation    = rotation,
                modifier    = Modifier.size(knobSize)
            )
        }
        Spacer(Modifier.height(8.dp))

        // Frequency label below knob
        Text(
            text     = label,
            color    = Color(0xFF8888AA),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )





    }
}

// Convert 0f..1f value to -135°..+135° rotation angle
private fun valueToAngle(value: Float): Float = -135f + value * 270f