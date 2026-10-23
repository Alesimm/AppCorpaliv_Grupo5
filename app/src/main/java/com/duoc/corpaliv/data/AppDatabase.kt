package com.duoc.corpaliv.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// configuracion principal de la base de datos local sqlite con room
@Database(entities = [SolicitudEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun solicitudDao(): SolicitudDao

    companion object {
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        // devuelve la instancia unica de la base de datos o la crea si no existe
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