package com.duoc.corpaliv

// punto de entrada de la app en android
// configura el canal de notificaciones, tamano de pantalla, tema con cortina y navegacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.corpaliv.navigation.AppNavigation
import com.duoc.corpaliv.ui.theme.AppCorpaliv_Grupo5Theme
import com.duoc.corpaliv.ui.utils.LocalWindowWidthSizeClass
import com.duoc.corpaliv.ui.utils.NotificacionHelper
import com.duoc.corpaliv.viewmodel.EstadoViewModel

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificacionHelper.asegurarCanal(this)

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val estadoViewModel: EstadoViewModel = viewModel()
            val esTemaOscuro by estadoViewModel.esTemaOscuro.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalWindowWidthSizeClass provides windowSizeClass.widthSizeClass
            ) {
                AppCorpaliv_Grupo5Theme(
                    esTemaOscuro = esTemaOscuro,
                    alAlternarTema = { estadoViewModel.alternarTema() }
                ) {
                    AppNavigation(estadoViewModel = estadoViewModel)
                }
            }
        }
    }
}
