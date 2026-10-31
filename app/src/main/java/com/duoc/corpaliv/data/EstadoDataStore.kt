package com.duoc.corpaliv.data

// archivo de preferencias del telefono usando datastore
// guarda datos pequenos como el modo oscuro, favoritos, recordarme y notificaciones

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "preferencias_usuario")

// clase que lee y escribe las preferencias del usuario de forma simple
class EstadoDataStore(private val context: Context) {

    private val ESTADO_ACTIVADO = booleanPreferencesKey("modo_activado")
    private val FAVORITOS_GUARDADOS = stringSetPreferencesKey("programas_favoritos")
    private val CORREO_RECORDADO = stringPreferencesKey("correo_recordado")
    private val TEMA_OSCURO = booleanPreferencesKey("tema_oscuro")

    // guarda si el usuario activo las alertas del modo colaborador
    suspend fun guardarEstado(valor: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[ESTADO_ACTIVADO] = valor
        }
    }

    // lee si las alertas del modo colaborador estan prendidas o apagadas
    fun obtenerEstado(): Flow<Boolean?> {
        return context.dataStore.data.map { preferencias ->
            preferencias[ESTADO_ACTIVADO]
        }
    }

    // guarda o saca un programa de la lista de favoritos con un solo toque
    suspend fun alternarFavorito(idPrograma: String) {
        context.dataStore.edit { preferencias ->
            val actuales = preferencias[FAVORITOS_GUARDADOS] ?: emptySet()
            preferencias[FAVORITOS_GUARDADOS] = if (actuales.contains(idPrograma)) {
                actuales - idPrograma
            } else {
                actuales + idPrograma
            }
        }
    }

    // entrega la lista de todos los programas que el usuario marco como favoritos
    fun obtenerFavoritos(): Flow<Set<String>> {
        return context.dataStore.data.map { preferencias ->
            preferencias[FAVORITOS_GUARDADOS] ?: emptySet()
        }
    }

    // guarda el correo si marco recordarme para no escribirlo cada vez
    suspend fun guardarCorreoRecordado(correo: String) {
        context.dataStore.edit { preferencias ->
            if (correo.isBlank()) {
                preferencias.remove(CORREO_RECORDADO)
            } else {
                preferencias[CORREO_RECORDADO] = correo
            }
        }
    }

    // lee el correo recordado para rellenarlo automaticamente en el login
    fun obtenerCorreoRecordado(): Flow<String> {
        return context.dataStore.data.map { preferencias ->
            preferencias[CORREO_RECORDADO] ?: ""
        }
    }

    // guarda si el usuario prefiere ver la app en modo claro o modo oscuro
    suspend fun guardarTemaOscuro(oscuro: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[TEMA_OSCURO] = oscuro
        }
    }

    // lee si la app debe mostrarse en modo claro o modo oscuro
    fun obtenerTemaOscuro(): Flow<Boolean> {
        return context.dataStore.data.map { preferencias ->
            preferencias[TEMA_OSCURO] ?: false
        }
    }
}
