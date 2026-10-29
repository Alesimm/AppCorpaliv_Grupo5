package com.duoc.corpaliv.ui.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.widget.Toast

// recurso nativo 1: consulta al sistema si hay conexion activa a internet
fun hayConexionInternet(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val cap = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
    return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// recurso nativo 2: abre el marcador telefonico con proteccion ante dispositivos sin app de llamadas
fun llamarPorTelefono(context: Context, numero: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$numero")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "no hay aplicacion para realizar llamadas", Toast.LENGTH_SHORT).show()
    }
}

// recurso nativo 3: abre el menu nativo para compartir informacion por mensaje o redes
fun compartirTextoNativo(context: Context, texto: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir información de CORPALIV"))
}
