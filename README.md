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
com.dev.mymusic/
│
├── 📁 data/
│   ├── 📁 model/
│   │   └── AudioTrack.kt
│   │
│   ├── 📁 repository/
│   │   ├── AudioRepository.kt
│   │   └── AudioRepositoryImpl.kt
│   │
│   └── 📁 datasource/
│       └── AssetAudioDataSource.kt
│
├── 📁 domain/
│   ├── 📁 equalizer/
│   │   └── EqualizerPreset.kt
│   │
│   └── 📁 waveform/
│       └── WaveformExtractor.kt
│
├── 📁 service/
│   └── MusicService.kt
│
├── 📁 di/
│   ├── AppModule.kt
│   └── RepositoryModule.kt
│
├── 📁 ui/
│   ├── 📁 tracklist/
│   ├── 📁 playback/
│   ├── 📁 equalizer/
│   └── 📁 navigation/
│
└── MainActivity.kt

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




