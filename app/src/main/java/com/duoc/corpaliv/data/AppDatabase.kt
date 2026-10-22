package com.duoc.corpaliv.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// clase principal que crea el archivo fisico sqlite usando room
@Database(entities = [SolicitudEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // conecta la base de datos con el dao de solicitudes
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        // evita que se abran dos conexiones al mismo tiempo en memoria
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        // devuelve la base de datos existente o la crea si es la primera vez
        fun obtenerBaseDatos(context: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "corpaliv_alba_db"
                ).build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}