package com.finanzas.automatica.presentation.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finanzas.automatica.presentation.ui.theme.FinancePrimary
import com.finanzas.automatica.presentation.ui.theme.FinanceSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alphaAnim"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1000),
        label = "scaleAnim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAnim"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        TopographicBackground(modifier = Modifier.fillMaxSize())
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim)
                .scale(scaleAnim * pulseAnim)
        ) {
            KivoLogo(modifier = Modifier.size(120.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Kivo",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = FinancePrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tu dinero, en orden",
                fontSize = 18.sp,
                color = FinanceSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Fondo del splash: curvas de contorno como un mapa topografico, dibujadas a mano con
 * Canvas (no una ilustracion generada) -- deriva muy lenta e imperceptible. Reemplaza el
 * fondo anterior (una ilustracion tipo stock-AI, ver docs/PENDIENTES.md) por un patron
 * geometrico sobrio propio, coherente con la paleta Barro & Ocre. Dos focos de anillos
 * concentricos en esquinas opuestas, igual que el concepto elegido en la vista previa.
 */
@Composable
private fun TopographicBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "topoDrift")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 90_000, easing = LinearEasing)
        ),
        label = "topoRotation"
    )
    val ringColorA = FinanceSecondary
    val ringColorB = FinancePrimary

    Canvas(modifier = modifier) {
        rotate(degrees = rotation, pivot = Offset(size.width * 0.5f, size.height * 0.5f)) {
            drawContourCluster(
                center = Offset(size.width * 0.22f, size.height * 0.18f),
                baseRadius = size.minDimension * 0.10f,
                ringGap = size.minDimension * 0.075f,
                ringCount = 4,
                color = ringColorA
            )
            drawContourCluster(
                center = Offset(size.width * 0.82f, size.height * 0.86f),
                baseRadius = size.minDimension * 0.08f,
                ringGap = size.minDimension * 0.065f,
                ringCount = 5,
                color = ringColorB
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawContourCluster(
    center: Offset,
    baseRadius: Float,
    ringGap: Float,
    ringCount: Int,
    color: Color
) {
    for (i in 0 until ringCount) {
        val alpha = 0.16f - (i * 0.024f)
        if (alpha <= 0f) continue
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = baseRadius + (ringGap * i),
            center = center,
            style = Stroke(width = 1.6.dp.toPx())
        )
    }
}

@Composable
fun KivoLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.15f

        // Draw the vertical line of 'K'
        drawLine(
            color = FinanceSecondary,
            start = Offset(size.width * 0.25f, size.height * 0.1f),
            end = Offset(size.width * 0.25f, size.height * 0.9f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Draw the top diagonal of 'K'
        val pathTop = Path().apply {
            moveTo(size.width * 0.25f, size.height * 0.5f)
            lineTo(size.width * 0.75f, size.height * 0.15f)
        }
        drawPath(
            path = pathTop,
            color = FinancePrimary,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw the bottom diagonal of 'K'
        val pathBottom = Path().apply {
            moveTo(size.width * 0.42f, size.height * 0.38f)
            lineTo(size.width * 0.75f, size.height * 0.85f)
        }
        drawPath(
            path = pathBottom,
            color = FinancePrimary,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
