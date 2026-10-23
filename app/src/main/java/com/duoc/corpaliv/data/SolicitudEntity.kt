package com.duoc.corpaliv.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Define la tabla local "solicitudes" en SQLite mediante Room sin datos demo por defecto.
// Se conecta con SolicitudDao, AppDatabase y UsuarioViewModel para persistir los envíos.
@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val folio: String = "",
    val fecha: String = "",
    val colaboracion: String = "",
    val programa: String = "",
    val nombre: String = "",
    val correo: String = "",
    val direccion: String = "",
    val rutComercial: String = "",
    val mensaje: String = "",
    val estadoEnvio: String = ""
)