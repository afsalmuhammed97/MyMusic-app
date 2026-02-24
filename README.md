# MyMusic-app
A fully-featured Android music player built with **Jetpack Compose**, **MediaPlayer**, and **Material3**. Includes a real-time spectrum visualizer, 5-band equalizer with presets, and a persistent foreground playback service.


##  Features

- 🎶 **Track Playback** — Play, Pause, Resume, Seek, Next, Previous
- 🔊 **Foreground Service** — Music continues playing when the app is backgrounded
- 📊 **Real-time Spectrum Visualizer** — Dancing FFT bars synced to audio frequency
- 🎚️ **5-Band Equalizer** — Adjust 60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz
- 🎼 **EQ Presets** — Flat, Rock, Jazz, Classical, Pop, Vocal
- 💾 **Persistent EQ State** — Preset and band values saved across sessions
- 🎨 **Smooth Animations** — Rotating vinyl, cubic bezier EQ curve, spring animations
- 🔔 **Media Notification** — Play/Pause/Stop controls from notification shade
- 📋 **Mini Player** — Persistent mini player on track list screen while playing


## Architecture
This project follows **Clean Architecture** with **MVVM** pattern.

```
Presentation (UI)  →  ViewModel  →  Domain  →  Data
     │                    │             │          │
  Compose              StateFlow    Repository  DataSource
  Screens             AndroidVM     Interface   MediaMetadata

## 📁 Project Structure

```
com.dev.mymusic/
│
├── 📁 data/
│   ├── 📁 model/
│   │   └── AudioTrack.kt              # Data class — id, title, artist,
│   │                                  # duration, albumArt, assetPath
│   │
│   ├── 📁 repository/
│   │   ├── AudioRepository.kt         # Interface — defines getTracks()
│   │   └── AudioRepositoryImpl.kt     # Calls AssetAudioDataSource,
│   │                                  # maps raw data to AudioTrack list
│   │
│   └── 📁 datasource/
│       └── AssetAudioDataSource.kt    # Reads mp3 files from res/raw/
│                                      # Extracts metadata via
│                                      # MediaMetadataRetriever
│
├── 📁 domain/
│   ├── 📁 equalizer/
│   │   └── EqualizerPreset.kt         # Enum: FLAT, ROCK, JAZZ,
│   │                                  # CLASSICAL, POP, VOCAL
│   │                                  # bandGains → 5 mB values per preset
│   │                                  # BAND_LABELS → frequency labels
│   │
│   └── 📁 waveform/
│       └── WaveformExtractor.kt       # Decodes audio via MediaCodec
│                                      # Returns 100 normalized amplitudes
│
├── 📁 service/
│   └── MusicService.kt                # Foreground Service — core engine
│                                      # Owns MediaPlayer lifecycle
│                                      # Owns Equalizer (AudioEffect API)
│                                      # Owns Visualizer (real-time FFT)
│                                      # Exposes StateFlow<MusicPlaybackState>
│                                      # Handles play/pause/seek/next/prev
│                                      # Persistent media notification
│
├── 📁 di/
│   ├── AppModule.kt                   # Provides SharedPreferences,
│   │                                  # WaveformExtractor
│   └── RepositoryModule.kt            # Binds AudioRepository impl
│
├── 📁 ui/
│   ├── 📁 tracklist/
│   │   ├── TrackListScreen.kt         # Track list + MiniPlayer bottom bar
│   │   ├── TrackListViewModel.kt      # Loads tracks from repository
│   │   └── 📁 components/
│   │       └── TrackItem.kt           # Single track row component
│   │
│   ├── 📁 playback/
│   │   ├── PlaybackScreen.kt          # Now Playing full-screen UI
│   │   ├── PlaybackViewModel.kt       # Service binding + state bridge
│   │   ├── MusicPlaybackState.kt      # UI state data class
│   │   └── 📁 components/
│   │       ├── SeekBarSection.kt      # Progress slider + timestamps
│   │       ├── TransportControls.kt   # Prev/Play/Next buttons
│   │       ├── SpectrumVisualizer.kt  # Real-time FFT dancing bars
│   │       └── WaveformCanvas.kt      # Static decoded waveform
│   │
│   ├── 📁 equalizer/
│   │   ├── EqualizerScreen.kt         # EQ full screen UI
│   │   ├── EqualizerViewModel.kt      # Band gains, presets, persistence
│   │   ├── EqualizerUiState.kt        # EQ state data class
│   │   └── 📁 components/
│   │       ├── EqBandSection.kt       # Curve + sliders container
│   │       ├── EqCurveCanvas.kt       # Cubic bezier EQ curve
│   │       ├── EqBandSlider.kt        # Single vertical frequency slider
│   │       ├── PresetGrid.kt          # 3×2 preset chip grid
│   │       └── RotaryKnob.kt          # Canvas-drawn draggable knob
│   │
│   └── 📁 navigation/
│       ├── AppNavigation.kt           # NavHost, Activity-scoped ViewModels
│       └── Screens.kt                 # Route definitions
│
└── MainActivity.kt                    # Entry point, notification permission

```
Instead of raw or asset, you can implement local media using the content resolver. Here, we are using the asset folder for it
res/raw/*.mp3
      │
      ▼
AssetAudioDataSource ──► AudioRepositoryImpl ──► TrackListViewModel
                                                        │
                                                  TrackListScreen
                                                        │
                                                   user taps track
                                                        │
                                                        ▼
                                              PlaybackViewModel
                                             (pendingTrack queue)
                                                        │
                                                        ▼
                                                 MusicService
                                            ┌────────┴────────┐
                                         MediaPlayer      Equalizer
                                            │                 │
                                       Visualizer        AudioEffect
                                            │
                                     StateFlow<State>
                                            │
                                            ▼
                                   PlaybackViewModel
                                            │
                                            ▼
                                    PlaybackScreen
                                  SpectrumVisualizer


##  Tech Stack

| Layer | Technology |
|---|---|
| **UI** | Jetpack Compose, Material3 |
| **Architecture** | MVVM, Clean Architecture |
| **DI** | Hilt |
| **Navigation** | Navigation Compose |
| **Audio Playback** | Android MediaPlayer |
| **Audio Effects** | Android Equalizer API |
| **Spectrum Analysis** | Android Visualizer API |
| **State Management** | StateFlow, collectAsStateWithLifecycle |
| **Background Playback** | Foreground Service |
| **Persistence** | SharedPreferences |
| **Image Loading** | Coil |
| **Async** | Kotlin Coroutines |

2. **Add your audio files**

Place `.mp3` files in `app/src/main/assets/`. The app uses `AssetManager` to automatically discover and load all `.mp3` files at runtime — no code changes needed when adding new tracks:




