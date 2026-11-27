package com.duoc.corpaliv.viewmodel

// viewmodel simple que carga uf, utm y dolar desde mindicador.cl para mostrarlos en inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.corpaliv.data.IndicadoresResumen
import com.duoc.corpaliv.data.MindicadorRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IndicadoresUiState(
    val cargando: Boolean = false,
    val resumen: IndicadoresResumen? = null,
    val mensajeEstado: String? = null,
    val esError: Boolean = false
)

class IndicadoresViewModel(
    private val repository: MindicadorRepository = MindicadorRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow(IndicadoresUiState(cargando = true))
    val estado: StateFlow<IndicadoresUiState> = _estado

    init {
        cargarIndicadores(manual = false)
    }

    // solicita los valores economicos y muestra aviso temporal de exito o error al recargar
    fun cargarIndicadores(manual: Boolean = true) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, mensajeEstado = null, esError = false) }
            if (manual) {
                delay(350)
            }
            try {
                val datos = repository.obtenerResumen()
                _estado.value = IndicadoresUiState(
                    cargando = false,
                    resumen = datos,
                    mensajeEstado = if (manual) "Indicadores actualizados" else null,
                    esError = false
                )
                if (manual) {
                    delay(2500)
                    _estado.update { if (!it.esError) it.copy(mensajeEstado = null) else it }
                }
            } catch (_: Exception) {
                val hayDatosPrevios = _estado.value.resumen != null
                _estado.update {
                    it.copy(
                        cargando = false,
                        mensajeEstado = if (hayDatosPrevios) {
                            "Sin conexión · mostrando últimos valores"
                        } else {
                            "Sin conexión para consultar mindicador.cl"
                        },
                        esError = true
                    )
                }
                if (hayDatosPrevios) {
                    delay(3000)
                    _estado.update { if (it.esError) it.copy(mensajeEstado = null) else it }
                }
            }
        }
    }
}
