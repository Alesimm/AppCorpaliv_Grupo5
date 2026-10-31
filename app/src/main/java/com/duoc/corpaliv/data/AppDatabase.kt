package com.duoc.corpaliv.data

// base de datos local del telefono usando room y sqlite
// guarda las solicitudes para que no se borren al cerrar la app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SolicitudEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // entrega las funciones para guardar, leer, editar y borrar solicitudes
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        // asegura que exista una sola conexion a la base de datos para no gastar memoria
        fun obtenerBaseDatos(context: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "corpaliv_alba_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}
