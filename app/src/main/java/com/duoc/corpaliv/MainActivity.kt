package com.duoc.corpaliv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.duoc.corpaliv.ui.screens.HomeScreen
import com.duoc.corpaliv.ui.theme.AppCorpaliv_Grupo5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppCorpaliv_Grupo5Theme {
                // carga nuestra pantalla inicial en vez del texto por defecto
                HomeScreen()
            }
        }
    }
}