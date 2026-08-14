package com.finanzas.automatica.presentation.ui.components

import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.finanzas.automatica.R
import com.finanzas.automatica.service.BiometricAccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Full-screen lock: mientras esta bloqueado, el candado se dibuja ENCIMA de [content]
 * como overlay opaco -- [content] sigue compuesto debajo, nunca se desmonta.
 *
 * Antes [content] (que incluye AppNavHost y su NavController) se sacaba de la
 * composicion por completo al bloquear (`if (locked) lock else content()`), así que se
 * perdía toda la pila de navegación. Cualquier acción que dispara un Activity externo
 * (selector de archivos para importar un extracto, cámara/galería para escanear una
 * factura o un movimiento) manda la app a segundo plano -- eso dispara ON_STOP, que
 * bloquea la app -- y al volver, con biometría activada, la pantalla se reconstruía
 * desde cero en el Inicio en vez de volver a donde el usuario estaba. Con el overlay,
 * el NavController sobrevive y el usuario vuelve exactamente a la pantalla/diálogo
 * donde se quedó. Se relockea solo cuando la app va a segundo plano. Usa BiometricPrompt
 * con fallback a la credencial del dispositivo (PIN/patrón); si no hay ninguna
 * credencial configurada, la app queda usable en vez de dejar al usuario bloqueado.
 */
@Composable
fun BiometricLockGate(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var locked by remember { mutableStateOf(true) }
    var available by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        available = withContext(Dispatchers.Default) {
            BiometricAccess.isAvailable(context)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                locked = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()
        if (available && locked) {
            BiometricLockPrompt(onSuccess = { locked = false })
        }
    }
}

@Composable
private fun BiometricLockPrompt(onSuccess: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    LaunchedEffect(Unit) {
        activity?.let { showPrompt(it, onSuccess) }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.biometric_lock_illustration),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .appearFromBelow()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Kivo está bloqueado",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Verifica tu identidad para ver tus movimientos, planes y metas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                activity?.let { showPrompt(it, onSuccess) }
            }) {
                Icon(Icons.Outlined.Lock, contentDescription = null)
                Text("Desbloquear", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

private fun showPrompt(activity: FragmentActivity, onSuccess: () -> Unit) {
    val executor = ContextCompat.getMainExecutor(activity)
    val prompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                // Cancelación o error: permanece bloqueado, el usuario puede reintentar.
            }
        }
    )
    prompt.authenticate(
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Desbloquear Kivo")
            .setSubtitle("Usa tu huella, rostro o PIN para ver tus datos financieros")
            .setAllowedAuthenticators(BiometricAccess.AUTHENTICATORS)
            .build()
    )
}