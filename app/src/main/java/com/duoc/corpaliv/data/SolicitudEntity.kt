package com.duoc.corpaliv.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// entidad room que define la tabla solicitudes en la base de datos sqlite del telefono
@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    // clave primaria que se incrementa sola con cada nuevo registro
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val folio: String,
    val nombre: String,
    val correo: String,
    val direccion: String,
    // guarda si la solicitud esta pendiente sin internet, sincronizada o en revision
    val estadoEnvio: String
)