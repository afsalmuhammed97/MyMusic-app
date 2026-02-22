package com.dev.mymusic.ui.equlizer

import com.dev.mymusic.domain.equalizer.EqualizerPreset
import com.dev.mymusic.domain.equalizer.bandGains

data class EqualizerUiState(
    val selectedPreset: EqualizerPreset = EqualizerPreset.FLAT,
    // Current gain for each of the 5 bands in milliBels (-1500 to +1500)
    val bandGains: List<Int> = EqualizerPreset.FLAT.bandGains,
    val isEqualizerAvailable: Boolean = true,
    val error: String? = null
)
