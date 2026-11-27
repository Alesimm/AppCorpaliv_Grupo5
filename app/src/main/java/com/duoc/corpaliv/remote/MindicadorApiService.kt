package com.duoc.corpaliv.remote

// interfaz retrofit para consultar indicadores economicos de chile en mindicador.cl

import retrofit2.http.GET

interface MindicadorApiService {

    // obtiene los valores diarios de uf, utm y dolar observado
    @GET("api")
    suspend fun obtenerIndicadores(): MindicadorResponse
}
