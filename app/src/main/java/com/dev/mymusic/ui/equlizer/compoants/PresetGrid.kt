package com.dev.mymusic.ui.equlizer.compoants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dev.mymusic.domain.equalizer.EqualizerPreset



@Composable
fun PresetGrid(
    selectedPreset: EqualizerPreset,
    onPresetSelected: (EqualizerPreset) -> Unit,
    modifier: Modifier = Modifier) {

    val presets = EqualizerPreset.entries



    // 3 columns x 2 rows
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        presets.chunked(3).forEach { rowPresets ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowPresets.forEach { preset ->
                    PresetChip(
                        preset = preset,
                        isSelected = preset == selectedPreset,
                        onClick = { onPresetSelected(preset) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty slots in last row
                repeat(3 - rowPresets.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
    
}


