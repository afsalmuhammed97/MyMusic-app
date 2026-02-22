package com.dev.mymusic.ui.equlizer

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.mymusic.domain.equalizer.EqualizerPreset
import com.dev.mymusic.domain.equalizer.bandGains
import com.dev.mymusic.service.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PREF_PRESET = "eq_preset"
private const val PREF_BAND_PREFIX = "eq_band_"


@HiltViewModel
class EqualizerViewModel  @Inject constructor(private val prefs: SharedPreferences): ViewModel(){


    private val _uiState = MutableStateFlow(EqualizerUiState())
    val uiState: StateFlow<EqualizerUiState> = _uiState.asStateFlow()

    // Reference to service — set from outside when service connects
    private var musicService: MusicService? = null

    init {
        loadPersistedState()
    }

    // ── Called from AppNavigation after service binds
    fun attachService(service: MusicService) {
        musicService = service
        // Apply persisted preset immediately to the audio engine
        applyBandsToService(_uiState.value.bandGains)
    }

    fun detachService() {
        musicService = null
    }


    // ── Preset selection ──────────────────────────────────────────────────────
    fun selectPreset(preset: EqualizerPreset) {
        val gains = preset.bandGains
        _uiState.update {
            it.copy(selectedPreset = preset, bandGains = gains)
        }
        applyBandsToService(gains)
        persistState(preset, gains)
    }



    // ── Manual band adjustment ────────────────────────────────────────────────
    fun setBandGain(bandIndex: Int, gainMb: Int) {
        val newGains = _uiState.value.bandGains.toMutableList()
        newGains[bandIndex] = gainMb
        _uiState.update {
            it.copy(
                bandGains = newGains,
                selectedPreset = EqualizerPreset.FLAT // custom = no preset active
            )
        }
        applyBandsToService(newGains)
        persistState(EqualizerPreset.FLAT, newGains)
    }

    // ── Apply to Android Equalizer API via service ────────────────────────────
    private fun applyBandsToService(gains: List<Int>) {
        musicService?.applyEqualizer(gains)
    }



    // ── Persistence ───────────────────────────────────────────────────────────
    private fun persistState(preset: EqualizerPreset, gains: List<Int>) {
        viewModelScope.
        launch {
            prefs.edit().apply {
                putString(PREF_PRESET, preset.name)
                gains.forEachIndexed { index, gain ->
                    putInt("$PREF_BAND_PREFIX$index", gain)
                }
                apply()
            }
        }
    }


    // ── Load from SharedPreferences ───────────────────────────────────────────

    private fun loadPersistedState() {
        // 1. Load saved preset name — default to FLAT if nothing saved
        val savedPresetName = prefs.getString(PREF_PRESET, EqualizerPreset.FLAT.name)

        // 2. Safely convert string back to enum — fallback to FLAT if corrupted
        val savedPreset = try {
            EqualizerPreset.valueOf(savedPresetName ?: EqualizerPreset.FLAT.name)
        } catch (e: IllegalArgumentException) {
            EqualizerPreset.FLAT
        }

        // 3. Load each band individually — fallback to preset's default gain
        //    This handles the case where user manually dragged bands
        val savedGains = List(5) { index ->
            prefs.getInt(
                "$PREF_BAND_PREFIX$index",
                savedPreset.bandGains[index]  // ← preset gain as default
            )
        }

        // 4. Push into UI state
        _uiState.update {
            it.copy(
                selectedPreset = savedPreset,
                bandGains = savedGains
            )
        }
    }

    companion object {
        private const val PREF_PRESET      = "eq_preset"
        private const val PREF_BAND_PREFIX = "eq_band_"
    }
}