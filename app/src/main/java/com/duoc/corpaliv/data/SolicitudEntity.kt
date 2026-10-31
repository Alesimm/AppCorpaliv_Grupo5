package com.duoc.corpaliv.data

// tabla de la base de datos que guarda las solicitudes de contacto
// tiene campos como id, folio, nombre, correo, fecha y estado de envio

import androidx.room.Entity
import androidx.room.PrimaryKey

// estructura de datos de cada fila guardada en la tabla solicitudes
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
    val rutComercial: String = "",
    val mensaje: String = "",
    val estadoEnvio: String = ""
)
