package com.dev.mymusic.ui.equlizer.compoants

//import android.graphics.Color
import android.util.Log
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.room.util.copy
import com.dev.mymusic.ui.theme.AccentGreen
import com.dev.mymusic.ui.theme.SurfaceCard
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import java.util.Collections.list

@Composable
fun  EqCurveCanvas ( //
    gains: List<Int>,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(0xFF00E5A0)

    // Animate each gain value smoothly
    val animatedGains = gains.map { gain ->
        animateIntAsState(
            targetValue = gain,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = 200f),
            label = "gain"
        ).value
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF13131A))
            .drawBehind {
                if (animatedGains.size < 2) return@drawBehind
                drawSmoothEqCurve(animatedGains, accentColor)
            }
    )
}

private fun DrawScope.drawSmoothEqCurve(gains: List<Int>, color: Color) {
    val maxGain = 1500f
    val width   = size.width
    val height  = size.height
    val midY    = height / 2f
    val padding = 24.dp.toPx()

    // ── Calculate band point positions ────────────────────────────────────────
    val points = gains.mapIndexed { i, gain ->
        val x = padding + (i.toFloat() / (gains.size - 1)) * (width - padding * 2)
        val y = midY - (gain / maxGain) * (midY - 16.dp.toPx())
        Offset(x, y)
    }

    // ── Smooth cubic bezier path ──────────────────────────────────────────────
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        for (i in 0 until points.size - 1) {
            val p0       = points[i]
            val p1       = points[i + 1]
            val controlX = (p1.x - p0.x) / 3f
            val cp1      = Offset(p0.x + controlX, p0.y)
            val cp2      = Offset(p1.x - controlX, p1.y)
            cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, p1.x, p1.y)
        }
    }

    // ── Gradient fill under the curve ─────────────────────────────────────────
    val fillPath = Path().apply {
        addPath(path)
        lineTo(points.last().x, midY)
        lineTo(points.first().x, midY)
        close()
    }
    drawPath(
        path  = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                color.copy(alpha = 0.25f),
                color.copy(alpha = 0.0f)
            ),
            startY = 0f,
            endY   = height
        )
    )

    // ── Outer glow line ───────────────────────────────────────────────────────
    drawPath(
        path  = path,
        color = color.copy(alpha = 0.2f),
        style = Stroke(
            width = 12.dp.toPx(),
            cap   = StrokeCap.Round,
            join  = StrokeJoin.Round
        )
    )

    // ── Main curve line ───────────────────────────────────────────────────────
    drawPath(
        path  = path,
        color = color,
        style = Stroke(
            width = 2.5.dp.toPx(),
            cap   = StrokeCap.Round,
            join  = StrokeJoin.Round
        )
    )

    // ── Dashed center baseline ────────────────────────────────────────────────
    drawLine(
        color       = color.copy(alpha = 0.12f),
        start       = Offset(0f, midY),
        end         = Offset(width, midY),
        strokeWidth = 1.dp.toPx(),
        pathEffect  = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
    )

    // ── Glowing dots at each band ─────────────────────────────────────────────
    points.forEach { point ->
        // Outer glow ring
        drawCircle(
            color  = color.copy(alpha = 0.3f),
            radius = 8.dp.toPx(),
            center = point
        )
        // Solid filled dot
        drawCircle(
            color  = color,
            radius = 4.dp.toPx(),
            center = point
        )
        // White center pinpoint
        drawCircle(
            color  = Color.White,
            radius = 2.dp.toPx(),
            center = point
        )
    }
}
//
//@Composable
//fun EqCurveCanvas( gains: List<Int>,modifier: Modifier = Modifier) {
//       Log.d("MMM","gains${gains}")
//    val accentColor = AccentGreen
//    Box(
//        modifier = modifier
//            .clip(RoundedCornerShape(12.dp))
//            .background(SurfaceCard)
//            .drawBehind {
//                if (gains.size < 2) return@drawBehind
//                drawEqCurve(gains, accentColor)
//            }
//    )
//
//}
//
//
//private fun DrawScope.drawEqCurve(gains: List<Int>, color: Color) {
//    val maxGain = 1500f
//    val width = size.width-6f
//    val height = size.height
//    val midY = height / 2f
//    val stepX = width / (gains.size - 1).toFloat()
//
//    val points = gains.mapIndexed { i, gain ->
//        val x = i * stepX
//        val y = midY - (gain / maxGain) * (midY - 12.dp.toPx())
//        Offset(x, y)
//    }
//
//    // Draw glow line (thick, low alpha)
//    for (i in 0 until points.size - 1) {
//        drawLine(
//            color = color.copy(alpha = 0.15f),
//            start = points[i],
//            end = points[i + 1],
//            strokeWidth = 12.dp.toPx(),
//            cap = StrokeCap.Round
//        )
//    }
//
//    // Draw main line
//    for (i in 0 until points.size - 1) {
//        drawLine(
//            color = color.copy(alpha = 0.9f),
//            start = points[i],
//            end = points[i + 1],
//            strokeWidth = 2.dp.toPx(),
//            cap = StrokeCap.Round
//        )
//    }
//
//    // Draw dots at each band
//    points.forEach { point ->
//        drawCircle(
//            color = color,
//            radius = 4.dp.toPx(),
//            center = point
//        )
//    }
//
//    // Draw center baseline
////    drawLine(
////        color = color.copy(alpha = 0.15f),
////        start = Offset(0f, midY),
////        end = Offset(width, midY),
////        strokeWidth = 1.dp.toPx()
////    )
//}
//
//@Preview
//@Composable
//private fun EqCurveCanvasPreview() {
//
// //val list=List<Int> =listOf(1, 2, 3, 4)
//    val numbers: List<Int> = listOf(11, 22, 33, 34)
//    //EqCurveCanvas(numbers)
//
//}