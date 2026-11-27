package com.duoc.corpaliv.remote

// cliente retrofit centralizado para conectarse al microservicio spring boot desde el emulador

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // 10.0.2.2 apunta al localhost del computador cuando la app corre en el emulador android
    private const val BASE_URL_MICROSERVICIO = "http://10.0.2.2:8080/"

    // url publica de la api externa de indicadores economicos de chile
    private const val BASE_URL_MINDICADOR = "https://mindicador.cl/"

    // timeouts cortos para pasar rapido al respaldo local si el servidor esta apagado
    private val clienteHttp = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    // instancia unica del servicio de solicitudes
    val solicitudApi: SolicitudApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_MICROSERVICIO)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SolicitudApiService::class.java)
    }

    // instancia unica de la api externa mindicador independiente del microservicio propio
    val mindicadorApi: MindicadorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_MINDICADOR)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MindicadorApiService::class.java)
    }
}
