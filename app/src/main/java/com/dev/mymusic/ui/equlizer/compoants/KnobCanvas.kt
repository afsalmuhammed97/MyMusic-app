package com.dev.mymusic.ui.equlizer.compoants

import android.graphics.Color
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.dev.mymusic.ui.theme.AccentGreen
import com.dev.mymusic.ui.theme.knobCircleColor
import com.dev.mymusic.ui.theme.knobColor
import com.dev.mymusic.ui.theme.whiteColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun KnobCanvas(
    rotation: Float,
     modifier: Modifier = Modifier
) {

    val sweepAngle by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "knobRotation"
    )


    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerRadius = size.minDimension / 2f
        val innerRadius = outerRadius * 0.65f
        val startAngle = 135f   // bottom-left
        val totalSweep = 270f   // total arc degrees


        // ── Outer ring track (full arc) ───────────────────────────────────────
        drawArc(
            color = knobColor,
            startAngle = startAngle,
            sweepAngle = totalSweep,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
            size = Size(outerRadius * 2, outerRadius * 2)
        )

        // ── Active arc — shows how much the knob is turned ───────────────────
        val activeSweep = ((sweepAngle + 135f) / 270f) * totalSweep
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(AccentGreen.copy(alpha = 0.5f), knobCircleColor),
                center = center
            ),
            startAngle = startAngle,
            sweepAngle = activeSweep.coerceIn(0f, totalSweep),
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
            size = Size(outerRadius * 2, outerRadius * 2)
        )

        // ── Knob body — dark circle with gradient ─────────────────────────────
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(knobColor, knobCircleColor),
                center = center,
                radius = innerRadius
            ),
            radius = innerRadius,
            center = center
        )

        // ── Knob border glow ──────────────────────────────────────────────────
        drawCircle(
            color = AccentGreen.copy(alpha = 0.15f),
            radius = innerRadius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // ── Indicator line — rotates with knob ────────────────────────────────
        val angleRad = Math.toRadians((sweepAngle - 90f).toDouble())
        val lineStart = Offset(
            x = center.x + (innerRadius * 0.45f) * cos(angleRad).toFloat(),
            y = center.y + (innerRadius * 0.45f) * sin(angleRad).toFloat()
        )
        val lineEnd = Offset(
            x = center.x + (innerRadius * 0.85f) * cos(angleRad).toFloat(),
            y = center.y + (innerRadius * 0.85f) * sin(angleRad).toFloat()
        )


        // Glow behind indicator
        drawLine(
            color = AccentGreen.copy(alpha = 0.4f),
            start = lineStart,
            end = lineEnd,
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Bright indicator line
        drawLine(
            color = whiteColor,
            start = lineStart,
            end = lineEnd,
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        // ── Center dot ────────────────────────────────────────────────────────
        drawCircle(
            color = AccentGreen,
            radius = 3.dp.toPx(),
            center = center
        )

    }
}