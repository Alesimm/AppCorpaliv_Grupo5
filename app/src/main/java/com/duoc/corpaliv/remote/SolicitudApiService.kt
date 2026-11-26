package com.duoc.corpaliv.remote

// interfaz retrofit con los endpoints crud del microservicio de solicitudes

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SolicitudApiService {

    // obtiene todas las solicitudes registradas en el servidor
    @GET("api/v1/solicitudes")
    suspend fun listarSolicitudes(): List<SolicitudDto>

    // consulta una solicitud puntual por su id
    @GET("api/v1/solicitudes/{id}")
    suspend fun obtenerSolicitud(@Path("id") id: Long): SolicitudDto

    // registra una nueva solicitud en el microservicio
    @POST("api/v1/solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): SolicitudDto

    // actualiza una solicitud existente en el microservicio
    @PUT("api/v1/solicitudes/{id}")
    suspend fun actualizarSolicitud(
        @Path("id") id: Long,
        @Body solicitud: SolicitudDto
    ): SolicitudDto

    // elimina una solicitud por id en el microservicio
    @DELETE("api/v1/solicitudes/{id}")
    suspend fun eliminarSolicitud(@Path("id") id: Long): Response<Unit>
}
