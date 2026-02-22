package com.dev.mymusic.ui.equlizer.compoants

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.mymusic.domain.equalizer.EqualizerPreset
import com.dev.mymusic.ui.theme.AccentGreen
import com.dev.mymusic.ui.theme.SurfaceCard
import com.dev.mymusic.ui.theme.TextSecondary

@Composable
fun PresetChip(
    preset: EqualizerPreset,
    isSelected: Boolean,
    onClick: () -> Unit, modifier: Modifier = Modifier) {

    val animatedBorder by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(200),
        label = "border"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) AccentGreen.copy(alpha = 0.15f)
                else SurfaceCard
            )
            .border(
                width = (1.5 * animatedBorder).dp,
                color = AccentGreen.copy(alpha = animatedBorder),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {

        Text(
            text = preset.label,
            color = if (isSelected) AccentGreen else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )

    }
    
}