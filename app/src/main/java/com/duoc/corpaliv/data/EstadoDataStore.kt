package com.duoc.corpaliv.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Gestiona la persistencia local ligera con DataStore Preferences.
// Se conecta con EstadoViewModel (modo colaborador y favoritos) y UsuarioViewModel (recordar correo).
val Context.dataStore by preferencesDataStore(name = "preferencias_usuario")

class EstadoDataStore(private val context: Context) {

    private val ESTADO_ACTIVADO = booleanPreferencesKey("modo_activado")
    private val FAVORITOS_GUARDADOS = stringSetPreferencesKey("programas_favoritos")
    private val CORREO_RECORDADO = stringPreferencesKey("correo_recordado")
    private val TEMA_OSCURO = booleanPreferencesKey("tema_oscuro")

    // Guarda en el teléfono si el modo colaborador está activo o inactivo.
    suspend fun guardarEstado(valor: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[ESTADO_ACTIVADO] = valor
        }
    }

    // Expone un flujo reactivo con el estado del modo colaborador.
    fun obtenerEstado(): Flow<Boolean?> {
        return context.dataStore.data.map { preferencias ->
            preferencias[ESTADO_ACTIVADO]
        }
    }

    // Agrega o quita el identificador de un programa en el conjunto de favoritos locales.
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

    // Devuelve en tiempo real el conjunto de programas marcados como favoritos.
    fun obtenerFavoritos(): Flow<Set<String>> {
        return context.dataStore.data.map { preferencias ->
            preferencias[FAVORITOS_GUARDADOS] ?: emptySet()
        }
    }

    // Guarda solo el correo del usuario si marca Recordarme, o lo limpia si desmarca la opción.
    suspend fun guardarCorreoRecordado(correo: String) {
        context.dataStore.edit { preferencias ->
            if (correo.isBlank()) {
                preferencias.remove(CORREO_RECORDADO)
            } else {
                preferencias[CORREO_RECORDADO] = correo
            }
        }
    }

    // Recupera el correo guardado para precargarlo al abrir la pantalla de login.
    fun obtenerCorreoRecordado(): Flow<String> {
        return context.dataStore.data.map { preferencias ->
            preferencias[CORREO_RECORDADO] ?: ""
        }
    }

    // Guarda la preferencia de tema oscuro (true) o claro (false).
    suspend fun guardarTemaOscuro(oscuro: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[TEMA_OSCURO] = oscuro
        }
    }

    // Devuelve un flujo reactivo con la preferencia de tema visual (por defecto falso: modo claro institucional).
    fun obtenerTemaOscuro(): Flow<Boolean> {
        return context.dataStore.data.map { preferencias ->
            preferencias[TEMA_OSCURO] ?: false
        }
    }
}