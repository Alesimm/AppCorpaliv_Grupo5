package com.duoc.corpaliv.data

// repositorio independiente para obtener uf, utm y dolar desde mindicador.cl sin tocar room

import com.duoc.corpaliv.remote.MindicadorApiService
import com.duoc.corpaliv.remote.RetrofitClient

data class IndicadoresResumen(
    val uf: Double = 0.0,
    val utm: Double = 0.0,
    val dolar: Double = 0.0,
    val fecha: String = ""
)

class MindicadorRepository(
    private val api: MindicadorApiService = RetrofitClient.mindicadorApi
) {

    // consulta los indicadores diarios y devuelve solo los tres valores principales
    suspend fun obtenerResumen(): IndicadoresResumen {
        val respuesta = api.obtenerIndicadores()
        val uf = respuesta.uf?.valor ?: 0.0
        val utm = respuesta.utm?.valor ?: 0.0
        val dolar = respuesta.dolar?.valor ?: 0.0
        if (uf <= 0.0 && utm <= 0.0 && dolar <= 0.0) {
            throw IllegalStateException("La API no devolvio valores validos")
        }
        return IndicadoresResumen(
            uf = uf,
            utm = utm,
            dolar = dolar,
            fecha = respuesta.fecha.take(10)
        )
    }
}
