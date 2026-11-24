package com.duoc.corpaliv.ui.utils

// utilidad nativa para emitir notificaciones locales reales en android
// se activa con el modo colaborador para avisar confirmaciones de solicitudes y alertas

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.duoc.corpaliv.MainActivity
import com.duoc.corpaliv.R

object NotificacionHelper {

    private const val CANAL_ID = "corpaliv_colaborador"
    private const val CANAL_NOMBRE = "Alertas CORPALIV"
    private const val CANAL_DESCRIPCION = "Notificaciones de modo colaborador y confirmación de solicitudes."

    // crea el canal obligatorio desde android 8 para mostrar avisos en la barra de estado
    fun asegurarCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                CANAL_NOMBRE,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CANAL_DESCRIPCION
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    // verifica si el sistema autorizo mostrar notificaciones (requerido en android 13+)
    fun tienePermiso(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // dispara una notificacion local real que al tocarla abre la aplicacion
    fun enviarNotificacion(
        context: Context,
        titulo: String,
        mensaje: String,
        id: Int = (System.currentTimeMillis() % 10000).toInt()
    ) {
        if (!tienePermiso(context)) return

        asegurarCanal(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(id, notificacion)
        } catch (_: SecurityException) {
            // si el usuario revoco el permiso desde ajustes del telefono evitamos cierres
        }
    }
}
