package com.dev.mymusic.ui.equlizer.compoants

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.mymusic.ui.theme.AccentGreen
import com.dev.mymusic.ui.theme.TextSecondary

@Composable
fun EqBandSlider( label: String,
                  gainMb: Int,         // -1500 to +1500
                  onGainChanged: (Int) -> Unit,
                  modifier: Modifier = Modifier) {

    val gainDb = gainMb / 100f   // convert mB → dB for display


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        // dB value
        Text(
            text = "${if (gainDb >= 0) "+" else ""}${"%.0f".format(gainDb)}",
            color = if (gainMb != 0) AccentGreen else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(4.dp))

        // Vertical slider
        VerticalSlider(
            value = gainMb.toFloat(),
            onValueChange = { onGainChanged(it.toInt()) },
            valueRange = -1500f..1500f,
            modifier = Modifier.height(160.dp)
        )

        Spacer(Modifier.height(8.dp))

        // Frequency label
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )

    }
    
}