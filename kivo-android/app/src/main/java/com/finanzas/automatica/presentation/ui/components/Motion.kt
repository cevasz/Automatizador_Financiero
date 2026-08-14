package com.finanzas.automatica.presentation.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Animaciones sutiles y naturales, pensadas para sentirse fluidas sin
 * distraer: duraciones cortas, movimientos pequeños y sin rebotes bruscos.
 */

/** Anima un valor numerico hasta el objetivo (efecto "contador"). */
@Composable
fun rememberAnimatedLong(target: Long, durationMillis: Int = 700): Long {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(target) {
        anim.animateTo(
            targetValue = target.toFloat(),
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        )
    }
    return anim.value.toLong()
}

/** Anima un progreso (0f..1f) desde 0 hasta el valor objetivo. */
@Composable
fun rememberAnimatedFloat(target: Float, durationMillis: Int = 700): Float {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(target) {
        anim.animateTo(
            targetValue = target.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis, easing = FastOutSlowInEasing)
        )
    }
    return anim.value
}

/** Texto de cantidad que "cuenta" hacia arriba/abajo hasta el valor objetivo. */
@Composable
fun AnimatedAmountText(
    target: Long,
    format: (Long) -> String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    val animated = rememberAnimatedLong(target)
    Text(
        text = format(animated),
        style = style,
        color = color,
        fontWeight = fontWeight,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}

/**
 * Entrada suave: aparece desvaneciendose y deslizandose desde abajo.
 * Usar con un delay progresivo para escalonar varios elementos.
 */
@Composable
fun Modifier.appearFromBelow(
    delayMillis: Int = 0,
    slideDistance: Dp = 16.dp
): Modifier {
    val density = LocalDensity.current
    val slidePx = with(density) { slideDistance.toPx() }
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(slidePx) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        launch { alpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing)) }
        launch { offsetY.animateTo(0f, tween(400, easing = FastOutSlowInEasing)) }
    }
    return this.graphicsLayer {
        this.alpha = alpha.value
        translationY = offsetY.value
    }
}

/**
 * Feedback tactil: el elemento se encoge muy levemente mientras se presiona y
 * vibra con un tick suave al soltar. Sustituye al clickable simple para que
 * las tarjetas interactivas se sientan vivas.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.pressFeedback(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = 0.9f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "pressFeedback"
    )
    return this
        .clickable(interactionSource, LocalIndication.current) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
}
