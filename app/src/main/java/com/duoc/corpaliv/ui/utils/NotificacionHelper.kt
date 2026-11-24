package com.duoc.corpaliv.ui.utils

// utilidad nativa para emitir notificaciones emergentes (heads-up) sin sonido en android
// usa canal IMPORTANCE_HIGH con setSound(null, null) y micro-vibracion para habilitar el banner visual

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.duoc.corpaliv.MainActivity
import com.duoc.corpaliv.R

object NotificacionHelper {

    // canal v4: alta prioridad sin sonido, con pulso minimo para que android muestre el banner en pantalla
    private const val CANAL_ID = "corpaliv_alertas_v4"
    private const val CANAL_NOMBRE = "Alertas CORPALIV"
    private const val CANAL_DESCRIPCION = "Avisos visuales sin sonido del modo colaborador y solicitudes."
    private val PATRON_PULSO_SUAVE = longArrayOf(0L, 40L)

    // registra el canal heads-up silencioso desde android 8
    fun asegurarCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                CANAL_NOMBRE,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CANAL_DESCRIPCION
                setSound(null, null)
                enableVibration(true)
                vibrationPattern = PATRON_PULSO_SUAVE
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    // verifica el permiso POST_NOTIFICATIONS en android 13+
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

    // revisa tanto el permiso como el interruptor general de notificaciones del sistema
    fun estaHabilitadoEnSistema(context: Context): Boolean {
        return tienePermiso(context) && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    // abre directamente la pantalla de notificaciones de esta app en ajustes de android
    fun abrirAjustesNotificaciones(context: Context) {
        val intent = Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            } else {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = android.net.Uri.parse("package:${context.packageName}")
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            // evita cierres si el dispositivo no soporta el intent directo
        }
    }

    // dispara banner emergente heads-up sin sonido (sin setSilent para que android no oculte el popup)
    fun enviarNotificacion(
        context: Context,
        titulo: String,
        mensaje: String,
        id: Int = (System.currentTimeMillis() % 100000).toInt()
    ): Boolean {
        if (!estaHabilitadoEnSistema(context)) return false

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
            .setSmallIcon(R.drawable.ic_notificacion_corpaliv)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setSound(null)
            .setVibrate(PATRON_PULSO_SUAVE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(id, notificacion)
            true
        } catch (_: SecurityException) {
            false
        }
    }
}
