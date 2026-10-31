package com.duoc.corpaliv.ui.utils

// mide el ancho de la pantalla del dispositivo para acomodar el diseno
// permite saber si el usuario esta usando un celular normal o una tablet

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// calcula el tamano de pantalla: compacta para celular y expandida para tablet
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun obtenerWindowSizeClass(): WindowSizeClass {
    // toma la pantalla actual del telefono para medir sus dimensiones
    val actividad = LocalContext.current as Activity
    return calculateWindowSizeClass(actividad)
}
