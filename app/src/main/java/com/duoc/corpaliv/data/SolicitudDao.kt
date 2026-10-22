package com.duoc.corpaliv.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// dao que contiene las cuatro operaciones crud para manejar la tabla en sqlite
@Dao
interface SolicitudDao {

    // operacion create: guarda una solicitud nueva en el telefono
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSolicitud(solicitud: SolicitudEntity)

    // operacion read: entrega la lista de solicitudes en tiempo real usando flow
    @Query("SELECT * FROM solicitudes ORDER BY id DESC")
    fun obtenerSolicitudes(): Flow<List<SolicitudEntity>>

    // operacion update: modifica el estado de una solicitud especifica
    @Update
    suspend fun actualizarSolicitud(solicitud: SolicitudEntity)

    // operacion update masiva: pasa las solicitudes pendientes a sincronizadas
    @Query("UPDATE solicitudes SET estadoEnvio = 'Sincronizado' WHERE estadoEnvio LIKE 'Pendiente%'")
    suspend fun sincronizarPendientes()

    // operacion delete: borra una solicitud de la base de datos local
    @Delete
    suspend fun eliminarSolicitud(solicitud: SolicitudEntity)
}