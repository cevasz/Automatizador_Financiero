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
