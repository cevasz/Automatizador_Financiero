package com.finanzas.automatica.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finanzas.automatica.presentation.ui.format.Money
import com.finanzas.automatica.presentation.ui.theme.KivoText
import com.finanzas.automatica.presentation.ui.theme.KivoSpacing

/** Radio de esquina compartido por tarjetas -- estilo "rounded-2xl", mas moderno que el
 * 8dp anterior (que se sentia chato/anticuado, ver skill mobile-app-ui-design). */
private val CardCornerRadius = 20.dp
private val CardShape = RoundedCornerShape(CardCornerRadius)

@Composable
fun FinanceCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    // Una tarjeta con color translucido NO puede llevar sombra de elevacion: Android
    // dibuja la sombra debajo de toda la silueta y, si lo de encima deja pasar la
    // luz, esa silueta se ve *a traves* de la tarjeta como un rectangulo mas oscuro
    // dentro de ella. Se notaba sobre todo en Ingresos y Gastos del Inicio, que son
    // las mas translucidas de la app (10% de opacidad).
    //
    // La solucion no es quitar el color ni la sombra, sino **aplanar** el color
    // contra el fondo: compositeOver da exactamente el mismo tono, pero opaco, y
    // entonces la sombra vuelve a quedar donde corresponde (por fuera).
    val fondo = if (containerColor.alpha < 1f) {
        containerColor.compositeOver(MaterialTheme.colorScheme.surface)
    } else {
        containerColor
    }

    val cardModifier = modifier
        .fillMaxWidth()
        // Sombra suave "tintada" con el color de marca en vez de negro/gris puro --
        // el skill de diseño pide igualar el color de la sombra al fondo/marca, nunca
        // gris/negro plano sobre un fondo con color. Antes las tarjetas no tenian
        // ninguna sombra (elevation = 0.dp + solo un borde de 1dp), se veian planas.
        .shadow(
            elevation = 8.dp,
            shape = CardShape,
            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        )
        .let { base -> if (onClick != null) base.pressFeedback(onClick) else base }
    Card(
        modifier = cardModifier,
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = fondo),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // 24 y no 20: el relleno de tarjeta es token, no una decision por
                // pantalla. Ver KivoSpacing.card.
                .padding(KivoSpacing.card),
            verticalArrangement = Arrangement.spacedBy(KivoSpacing.betweenGroups),
            content = content
        )
    }
}

@Composable
fun FinanceTag(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = color,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, color.copy(alpha = 0.18f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = KivoSpacing.md, vertical = KivoSpacing.xs),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun IconBadge(
    icon: ImageVector,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier,
    containerColor: Color = tint.copy(alpha = 0.14f)
) {
    // 44dp: tamaño minimo de zona de toque recomendado (antes 40dp).
    Surface(
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = containerColor,
        contentColor = tint
    ) {
        Column(
            modifier = Modifier.size(44.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            // Sin fontWeight suelto: titleMedium ya trae el peso de enfasis. Antes
            // cada pantalla lo repetia y era parte de por que SemiBold aparecia 60
            // veces sin crear ninguna jerarquia.
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (action != null) {
            action()
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    // Ilustracion real (ver res/drawable-nodpi/*.jpg) en vez del icono generico -- estas
    // imagenes ya existian en el proyecto pero ningun composable las usaba. El skill de
    // diseno marca "estado vacio generico sin guia" como anti-patron; una ilustracion
    // propia de marca convierte el estado vacio en una oportunidad, no un vacio.
    illustrationRes: Int? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .appearFromBelow()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (illustrationRes != null) {
            Image(
                painter = painterResource(illustrationRes),
                contentDescription = title,
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            IconBadge(
                icon = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(actionLabel)
            }
        }
    }
}

/**
 * Texto de dinero.
 *
 * Existe para que ningun monto se pinte con un `Text` suelto: todos pasan por
 * [KivoText.amount], que activa las cifras tabulares. Sin eso los digitos tienen
 * anchos distintos y un saldo que cambia de $1.111 a $8.888 **mueve** lo que
 * tiene al lado; en una columna de cifras, ademas, los numeros no alinean.
 */
@Composable
fun AmountText(
    cents: Long,
    modifier: Modifier = Modifier,
    size: AmountSize = AmountSize.Normal,
    color: Color = Color.Unspecified,
    signedAsIncome: Boolean? = null
) {
    Text(
        text = signedAsIncome?.let { Money.formatSigned(cents, it) } ?: Money.format(cents),
        modifier = modifier,
        style = when (size) {
            AmountSize.Hero -> KivoText.amountLarge
            AmountSize.Normal -> KivoText.amount
            AmountSize.Small -> KivoText.amountSmall
        },
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

enum class AmountSize { Hero, Normal, Small }

/**
 * Etiqueta de seccion en mayusculas. Da jerarquia por tracking y tamaño en vez de
 * gastar otro peso tipografico, que es lo que aplanaba las pantallas.
 */
@Composable
fun Eyebrow(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = KivoText.eyebrow,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
