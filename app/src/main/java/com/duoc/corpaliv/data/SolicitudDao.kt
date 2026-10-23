package com.duoc.corpaliv.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// interfaz dao con las cuatro operaciones crud para sqlite room
@Dao
interface SolicitudDao {

    // operacion create: inserta una solicitud nueva en sqlite
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSolicitud(solicitud: SolicitudEntity)

    // operacion read: consulta todas las solicitudes guardadas en el telefono
    @Query("SELECT * FROM solicitudes ORDER BY id DESC")
    fun obtenerSolicitudes(): Flow<List<SolicitudEntity>>

    // operacion update: actualiza los datos o el estado de una solicitud
    @Update
    suspend fun actualizarSolicitud(solicitud: SolicitudEntity)

    // operacion update masiva: pasa las solicitudes pendientes sin red a recibidas
    @Query("UPDATE solicitudes SET estadoEnvio = 'RECIBIDA' WHERE estadoEnvio LIKE 'PENDIENTE%'")
    suspend fun sincronizarPendientes()

    // operacion delete: elimina una solicitud de la base de datos local
    @Delete
    suspend fun eliminarSolicitud(solicitud: SolicitudEntity)
}