package com.duoc.corpaliv.ui.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri

// recurso nativo 1: consulta al sistema android si hay conexion activa a internet
fun hayConexionInternet(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val redActiva = connectivityManager.activeNetwork ?: return false
    val capacidades = connectivityManager.getNetworkCapabilities(redActiva) ?: return false
    return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// recurso nativo 2: abre la app de telefono del dispositivo con el numero listo para llamar
fun llamarPorTelefono(context: Context, numero: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$numero")
    }
    context.startActivity(intent)
}

// recurso nativo 3: abre el menu nativo de android para compartir texto por whatsapp o correo
fun compartirTextoNativo(context: Context, texto: String) {
    val enviarIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, texto)
        type = "text/plain"
    }
    val selector = Intent.createChooser(enviarIntent, "Compartir información de CORPALIV")
    context.startActivity(selector)
}