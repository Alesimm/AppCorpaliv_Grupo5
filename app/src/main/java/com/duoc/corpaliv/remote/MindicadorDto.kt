package com.duoc.corpaliv.remote

// modelos json simples para leer uf, utm y dolar desde la api publica mindicador.cl

data class IndicadorItemDto(
    val codigo: String = "",
    val nombre: String = "",
    val valor: Double = 0.0
)

data class MindicadorResponse(
    val fecha: String = "",
    val uf: IndicadorItemDto? = null,
    val dolar: IndicadorItemDto? = null,
    val utm: IndicadorItemDto? = null
)
