package com.finanzas.automatica.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.material3.ExperimentalMaterial3Api
import com.finanzas.automatica.data.local.FinanzasDatabase
import com.finanzas.automatica.presentation.navigation.AppNavHost
import com.finanzas.automatica.presentation.ui.theme.FinanzasAutomaticaTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = FinanzasDatabase.getInstance(applicationContext)
        setContent {
            FinanzasAutomaticaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(database = database)
                }
            }
        }
    }
}
