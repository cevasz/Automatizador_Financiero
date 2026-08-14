package com.finanzas.automatica.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.presentation.navigation.AppNavHost
import com.finanzas.automatica.presentation.ui.components.BiometricLockGate
import com.finanzas.automatica.presentation.ui.screen.SplashScreen
import com.finanzas.automatica.presentation.ui.theme.FinanzasAutomaticaTheme
import com.finanzas.automatica.presentation.viewmodel.SettingsViewModel

@ExperimentalMaterial3Api
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = FinanzasDatabase.getInstance(applicationContext)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                key = "biometricSettings",
                factory = BiometricSettingsViewModelFactory(database, application)
            )
            val biometricEnabled by settingsViewModel.biometricEnabled.collectAsState()
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val themePalette by settingsViewModel.themePalette.collectAsState()

            var showSplash by rememberSaveable { mutableStateOf(true) }

            FinanzasAutomaticaTheme(
                themeMode = themeMode,
                palette = themePalette
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Crossfade(
                        targetState = showSplash,
                        animationSpec = tween(durationMillis = 500),
                        label = "splashCrossfade"
                    ) { isSplash ->
                        if (isSplash) {
                            SplashScreen(
                                onSplashFinished = { showSplash = false }
                            )
                        } else {
                            if (biometricEnabled) {
                                BiometricLockGate {
                                    AppNavHost(database = database)
                                }
                            } else {
                                AppNavHost(database = database)
                            }
                        }
                    }
                }
            }
        }
    }
}

private class BiometricSettingsViewModelFactory(
    private val database: FinanzasDatabase,
    private val context: android.content.Context
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(database, context) as T
    }
}