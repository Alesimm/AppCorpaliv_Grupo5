package com.duoc.corpaliv

// es la pantalla de inicio de la app en android
// carga el diseno visual, lee el tema oscuro o claro y abre el menu de navegacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.corpaliv.navigation.AppNavigation
import com.duoc.corpaliv.ui.theme.AppCorpaliv_Grupo5Theme
import com.duoc.corpaliv.viewmodel.EstadoViewModel

class MainActivity : ComponentActivity() {

    // arranca la app en pantalla completa y conecta el tema elegido con las vistas
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val estadoViewModel: EstadoViewModel = viewModel()
            val esTemaOscuro by estadoViewModel.esTemaOscuro.collectAsState()

            AppCorpaliv_Grupo5Theme(
                esTemaOscuro = esTemaOscuro,
                alAlternarTema = { estadoViewModel.alternarTema() }
            ) {
                AppNavigation(estadoViewModel = estadoViewModel)
            }
        }
    }
}
