package com.duoc.corpaliv.api.repository

// repositorio jpa que consulta, guarda y elimina solicitudes en la base h2

import com.duoc.corpaliv.api.model.Solicitud
import org.springframework.data.jpa.repository.JpaRepository

interface SolicitudRepository : JpaRepository<Solicitud, Long> {

    // devuelve las solicitudes ordenadas desde la mas reciente igual que en android
    fun findAllByOrderByIdDesc(): List<Solicitud>
}
