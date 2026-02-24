package com.dev.mymusic.ui.equlizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.mymusic.ui.equlizer.compoants.EqBandSection
import com.dev.mymusic.ui.equlizer.compoants.PresetGrid
import com.dev.mymusic.ui.equlizer.compoants.RotaryKnob
import com.dev.mymusic.ui.theme.AccentGreen
import com.dev.mymusic.ui.theme.DarkBg
import com.dev.mymusic.ui.theme.TextPrimary
import com.dev.mymusic.ui.theme.TextSecondary


// Accent colors

@Composable
fun EqualizerScreen (modifier: Modifier = Modifier,onBack: () -> Unit,
                   viewModel: EqualizerViewModel = hiltViewModel()
) {


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Subtle radial glow behind EQ bands
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.Center)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AccentGreen.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {


            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Equalizer",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }


            // ── EQ Band Visualizer + Sliders ──────────────────────────────────
            EqBandSection(
                bandGains = uiState.bandGains,
                onBandChanged = { index, gain -> viewModel.setBandGain(index, gain) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(32.dp))


            // ── Preset label ──────────────────────────────────────────────────
            Text(
                text = "PRESETS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            // ── Preset chips ──────────────────────────────────────────────────
            PresetGrid(
                selectedPreset = uiState.selectedPreset,
                onPresetSelected = { viewModel.selectPreset(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )


           // Spacer(Modifier.height(12.dp))
//            Text(
//                text = "BASS & TREBLE",
//                color = TextSecondary,
//                fontSize = 11.sp,
//                fontWeight = FontWeight.Bold,
//                letterSpacing = 2.sp,
//                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
//            )
            /*
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                RotaryKnob(
                    label = "Bass",
                    value = uiState.bassValue,
                    onValueChange = { viewModel.setBass(it) },
                    accentColor = Color(0xFF6C63FF)  // purple for bass
                )

                // Center divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(60.dp)
                        .background(Color(0xFF2A2A3A))
                )

                RotaryKnob(
                    label         = "Treble",
                    value         = uiState.trebleValue,
                    onValueChange = { viewModel.setTreble(it) },
                    accentColor   = Color(0xFF00E5A0)  // green for treble
                )
            }

             */
        }

}
}

@Preview(showBackground = true)
@Composable
fun EqualizerScreenPreview() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.Center)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AccentGreen.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}