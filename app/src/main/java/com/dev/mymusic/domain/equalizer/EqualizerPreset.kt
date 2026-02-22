package com.dev.mymusic.domain.equalizer

enum  class EqualizerPreset (val label: String) {
    FLAT("Flat"),
    ROCK("Rock"),
    JAZZ("Jazz"),
    CLASSICAL("Classical"),
    POP("Pop"),
    VOCAL("Vocal")
}

// Band gains in milliBels for 5 bands: 60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz
// Range: -1500 to +1500 mB  (Android Equalizer API unit)
val EqualizerPreset.bandGains: List<Int>
    get() = when (this) {
        EqualizerPreset.FLAT      -> listOf(0,     0,     0,     0,     0)
        EqualizerPreset.ROCK      -> listOf(600,   300,  -100,   400,   600)
        EqualizerPreset.JAZZ      -> listOf(400,   200,  -200,   200,   500)
        EqualizerPreset.CLASSICAL -> listOf(500,   300,  -200,   400,   500)
        EqualizerPreset.POP       -> listOf(-100,  300,   600,   300,  -100)
        EqualizerPreset.VOCAL     -> listOf(-300,  -100,  600,   500,   -200)
    }

// Band labels shown in UI

val BAND_LABELS = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")