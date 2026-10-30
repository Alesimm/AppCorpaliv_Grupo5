package com.duoc.corpaliv.data

// operaciones crud en la base de datos local sqlite mediante room
// se conecta con solicitudentity, appdatabase y usuarioviewmodel

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {

    // operacion create: inserta una solicitud y retorna el id autonumerico generado
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSolicitud(solicitud: SolicitudEntity): Long

    // operacion read: consulta todas las solicitudes ordenadas de la mas reciente a la mas antigua
    @Query("SELECT * FROM solicitudes ORDER BY id DESC")
    fun obtenerSolicitudes(): Flow<List<SolicitudEntity>>

    // operacion update: actualiza los datos o el estado de una solicitud existente
    @Update
    suspend fun actualizarSolicitud(solicitud: SolicitudEntity)

    // operacion update masiva: pasa las solicitudes pendientes sin red a estado recibida
    @Query("UPDATE solicitudes SET estadoEnvio = 'RECIBIDA' WHERE estadoEnvio LIKE 'PENDIENTE%'")
    suspend fun sincronizarPendientes()

    // operacion delete: elimina una solicitud de la base de datos local
    @Delete
    suspend fun eliminarSolicitud(solicitud: SolicitudEntity)
}
