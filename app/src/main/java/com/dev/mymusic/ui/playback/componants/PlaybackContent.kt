package com.dev.mymusic.ui.playback.componants


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dev.mymusic.R
import com.dev.mymusic.ui.playback.model.MusicPlaybackState

@Composable
fun PlaybackContent(
    uiState: MusicPlaybackState,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onBack: () -> Unit,
    onOpenEqualizer: () -> Unit,

    modifier: Modifier = Modifier
) {


    // Subtle vinyl rotation animation when playing
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(uiState.isPlaying) {
        if (uiState.isPlaying) {
            rotation.animateTo(
                targetValue = rotation.value + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rotation.stop()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        // ── Blurred artwork background ─────────────────────────────────────
        AsyncImage(
            model = uiState.currentTrack?.albumArt,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.music_bg),
            modifier = Modifier
                .fillMaxSize()
                .blur(60.dp)
                .graphicsLayer { alpha = 0.25f }
        )

    }
    // Gradient overlay so text stays readable
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x990D0D0D),
                        Color(0xCC3F3C3C),
                        Color(0xFF0D0D0D)
                    )
                )
            )
    )

    // ── Main content column ────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

//            // CENTER — Screen title
//            Text(
//                text = "Now Playing",
//                color = Color.White,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Medium
//            )

            // RIGHT SIDE — Equalizer button
            IconButton(onClick = onOpenEqualizer) {
                Icon(
                    imageVector = Icons.Rounded.Equalizer,
                    contentDescription = "Equalizer",
                    tint = Color(0xFF6C63FF)
                )
            }

        }


        Spacer(Modifier.height(32.dp))

        // ── Artwork ───────────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(290.dp)
                .shadow(
                    elevation = 32.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFF6C63FF),
                    spotColor = Color(0xFF6C63FF)
                )
                .clip(CircleShape)
                .graphicsLayer { rotationZ = rotation.value }
        ) {
            AsyncImage(
                model = uiState.currentTrack?.albumArt,
                contentDescription = "Album artwork",
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.music_bg),
                modifier = Modifier.fillMaxSize()
            )

            // Vinyl center circle
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0D0D0D))
            )
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6C63FF))
            )
        }

        Spacer(Modifier.height(40.dp))

        // ── Title + Artist ─────────────────────────────────────────────
        Text(
            text = uiState.currentTrack?.title ?: "Unknown Title",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = uiState.currentTrack?.artist ?: "Unknown Artist",
            color = Color(0xFFAAAAAA),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(40.dp))


        // ── Seek bar ───────────────────────────────────────────────────
        SeekBarSection(
            currentMs = uiState.currentPositionMs,
            durationMs = uiState.durationMs,
            onSeek = onSeek
        )

        Spacer(Modifier.height(40.dp))

        // ── Transport controls ─────────────────────────────────────────
        TransportControls(
            isPlaying = uiState.isPlaying,
            isLoading = uiState.isLoading,
            onPlayPause = onPlayPause,
            onNext = onNext,
            onPrev = onPrev
        )
        // ── Error banner ───────────────────────────────────────────────
        uiState.error?.let { error ->
            Spacer(Modifier.height(16.dp))
            Text(
                text = error,
                color = Color(0xFFFF6B6B),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }

    }
}