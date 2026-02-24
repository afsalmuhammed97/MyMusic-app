package com.dev.mymusic.ui.equlizer

import com.dev.mymusic.domain.equalizer.EqualizerPreset
import com.dev.mymusic.domain.equalizer.bandGains

data class EqualizerUiState(
    val selectedPreset: EqualizerPreset = EqualizerPreset.FLAT,
    // Current gain for each of the 5 bands in milliBels (-1500 to +1500)
    val bandGains: List<Int> = EqualizerPreset.FLAT.bandGains,
    val bassValue: Float = 0.5f,    // 0f=min, 0.5f=flat, 1f=max
    val trebleValue: Float = 0.5f,
    val isEqualizerAvailable: Boolean = true,
    val error: String? = null
)
