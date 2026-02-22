package com.dev.mymusic.ui.equlizer.compoants

//import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.room.util.copy
import com.dev.mymusic.ui.theme.AccentGreen
import com.dev.mymusic.ui.theme.SurfaceCard
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import java.util.Collections.list

@Composable
fun EqCurveCanvas( gains: List<Int>,modifier: Modifier = Modifier) {

    val accentColor = AccentGreen
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .drawBehind {
                if (gains.size < 2) return@drawBehind
                drawEqCurve(gains, accentColor)
            }
    )
    
}


private fun DrawScope.drawEqCurve(gains: List<Int>, color: Color) {
    val maxGain = 1500f
    val width = size.width
    val height = size.height
    val midY = height / 2f
    val stepX = width / (gains.size - 1).toFloat()

    val points = gains.mapIndexed { i, gain ->
        val x = i * stepX
        val y = midY - (gain / maxGain) * (midY - 12.dp.toPx())
        Offset(x, y)
    }

    // Draw glow line (thick, low alpha)
    for (i in 0 until points.size - 1) {
        drawLine(
            color = color.copy(alpha = 0.15f),
            start = points[i],
            end = points[i + 1],
            strokeWidth = 12.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Draw main line
    for (i in 0 until points.size - 1) {
        drawLine(
            color = color.copy(alpha = 0.9f),
            start = points[i],
            end = points[i + 1],
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Draw dots at each band
    points.forEach { point ->
        drawCircle(
            color = color,
            radius = 4.dp.toPx(),
            center = point
        )
    }

    // Draw center baseline
    drawLine(
        color = color.copy(alpha = 0.15f),
        start = Offset(0f, midY),
        end = Offset(width, midY),
        strokeWidth = 1.dp.toPx()
    )
}

@Preview
@Composable
private fun EqCurveCanvasPreview() {

 //val list=List<Int> =listOf(1, 2, 3, 4)
    val numbers: List<Int> = listOf(11, 22, 33, 34)
    EqCurveCanvas(numbers)

}