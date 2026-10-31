package com.duoc.corpaliv.ui.utils

// funciones que usan herramientas del telefono como internet, llamadas y compartir
// conecta la app con los servicios propios del sistema android

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.widget.Toast

// funcion que revisa si el telefono tiene wifi o datos moviles funcionando
fun hayConexionInternet(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val cap = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
    return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// funcion que abre el teclado de llamadas del telefono con el numero listo para marcar
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

// funcion que abre la ventana para enviar el texto por whatsapp, correo o mensaje
fun compartirTextoNativo(context: Context, texto: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    context.startActivity(Intent.createChooser(intent, "compartir informacion de corpaliv"))
}
