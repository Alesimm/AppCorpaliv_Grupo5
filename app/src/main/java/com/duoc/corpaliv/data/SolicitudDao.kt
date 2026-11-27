package com.duoc.corpaliv.data

// interfaz con las consultas sql para manejar las solicitudes guardadas
// realiza las operaciones de crear, leer, actualizar y borrar en sqlite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {

    // operacion create: guarda una solicitud y devuelve el numero de id generado
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSolicitud(solicitud: SolicitudEntity): Long

    // guarda varias solicitudes traidas desde el servidor sin duplicar ids
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(solicitudes: List<SolicitudEntity>)

    // operacion read: trae todas las solicitudes desde la mas nueva a la mas vieja
    @Query("SELECT * FROM solicitudes ORDER BY id DESC")
    fun obtenerSolicitudes(): Flow<List<SolicitudEntity>>

    // consulta las solicitudes que quedaron guardadas sin conexion para reenviarlas
    @Query("SELECT * FROM solicitudes WHERE estadoEnvio LIKE 'PENDIENTE%'")
    suspend fun obtenerPendientes(): List<SolicitudEntity>

    // operacion update: cambia datos o actualiza el estado de una solicitud
    @Update
    suspend fun actualizarSolicitud(solicitud: SolicitudEntity)

    // operacion delete: borra una solicitud de la base de datos del telefono
    @Delete
    suspend fun eliminarSolicitud(solicitud: SolicitudEntity)
}
