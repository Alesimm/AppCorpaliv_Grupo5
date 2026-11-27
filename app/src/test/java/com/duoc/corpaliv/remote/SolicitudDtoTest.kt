package com.duoc.corpaliv.remote

// pruebas unitarias de mapeo entre dtos de red, entidades room y catalogo ficticio
// cada grupo cubre 2 caminos felices y 1 camino malo o borde

import com.duoc.corpaliv.data.SolicitudEntity
import com.duoc.corpaliv.model.listaProgramasAlbaLab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SolicitudDtoTest {

    // --- SolicitudDto.toEntity (2 felices, 1 borde) ---

    @Test
    fun toEntity_caminoFeliz1_mapeaTodosLosCampos() {
        val dto = SolicitudDto(
            id = 7L,
            folio = "ALBA-0007",
            fecha = "26/11/2026",
            colaboracion = "Donación",
            programa = "Taller de Oficios",
            nombre = "Ana",
            apellido = "Pérez",
            correo = "ana@ejemplo.cl",
            rutComercial = "No aplica",
            mensaje = "Aporte mensual",
            estadoEnvio = "RECIBIDA"
        )

        val entity = dto.toEntity()

        assertEquals(7, entity.id)
        assertEquals("ALBA-0007", entity.folio)
        assertEquals("ana@ejemplo.cl", entity.correo)
        assertEquals("RECIBIDA", entity.estadoEnvio)
    }

    @Test
    fun toEntity_caminoFeliz2_estadoVacioUsaRecibidaPorDefecto() {
        val dto = SolicitudDto(id = 3L, nombre = "Luis", estadoEnvio = "")
        assertEquals("RECIBIDA", dto.toEntity().estadoEnvio)
    }

    @Test
    fun toEntity_caminoMalo_idNuloMapeaACeroSinFallar() {
        val dto = SolicitudDto(id = null, nombre = "Sofía")
        assertEquals(0, dto.toEntity().id)
    }

    // --- SolicitudEntity.toDto (2 felices, 1 borde) ---

    @Test
    fun toDto_caminoFeliz1_incluyeIdPositivo() {
        val entity = SolicitudEntity(id = 12, nombre = "Carlos", correo = "carlos@ejemplo.cl")
        val dto = entity.toDto(incluirId = true)
        assertEquals(12L, dto.id)
        assertEquals("carlos@ejemplo.cl", dto.correo)
    }

    @Test
    fun toDto_caminoFeliz2_omiteIdCuandoSeCreaEnServidor() {
        val entity = SolicitudEntity(id = 12, nombre = "Carlos")
        val dto = entity.toDto(incluirId = false)
        assertNull(dto.id)
    }

    @Test
    fun toDto_caminoMalo_idCeroEnviaNullParaAutogenerarEnH2() {
        val entity = SolicitudEntity(id = 0, nombre = "Elena")
        assertNull(entity.toDto(incluirId = true).id)
    }

    // --- MindicadorResponse (2 felices, 1 borde) ---

    @Test
    fun mindicadorResponse_caminoFeliz1_almacenaUfUtmYDolar() {
        val resp = MindicadorResponse(
            fecha = "2026-11-26T03:00:00.000Z",
            uf = IndicadorItemDto("uf", "UF", 38500.5),
            dolar = IndicadorItemDto("dolar", "Dólar", 950.0),
            utm = IndicadorItemDto("utm", "UTM", 67294.0)
        )
        assertEquals(38500.5, resp.uf?.valor ?: 0.0, 0.01)
        assertEquals(950.0, resp.dolar?.valor ?: 0.0, 0.01)
        assertEquals(67294.0, resp.utm?.valor ?: 0.0, 0.01)
    }

    @Test
    fun mindicadorResponse_caminoFeliz2_itemPorDefectoInicializaLimpio() {
        val item = IndicadorItemDto()
        assertEquals("", item.codigo)
        assertEquals(0.0, item.valor, 0.0)
    }

    @Test
    fun mindicadorResponse_caminoMalo_respuestaVaciaTieneIndicadoresNulos() {
        val resp = MindicadorResponse()
        assertNull(resp.uf)
        assertNull(resp.utm)
        assertNull(resp.dolar)
    }

    // --- Catalogo programas ficticios (2 felices, 1 validacion negativa) ---

    @Test
    fun catalogoProgramas_caminoFeliz1_contieneLos3Programas() {
        assertEquals(3, listaProgramasAlbaLab.size)
        assertTrue(listaProgramasAlbaLab.all { it.titulo.isNotBlank() && it.descripcion.isNotBlank() })
    }

    @Test
    fun catalogoProgramas_caminoFeliz2_idsUnicosYTelefonosSimulados() {
        val idsUnicos = listaProgramasAlbaLab.map { it.id }.toSet()
        assertEquals(listaProgramasAlbaLab.size, idsUnicos.size)
        assertTrue(listaProgramasAlbaLab.all { it.telefonoContacto.startsWith("+56") })
    }

    @Test
    fun catalogoProgramas_caminoMalo_noIncluyeDominioRealCorpalivCl() {
        val contieneReal = listaProgramasAlbaLab.any {
            it.descripcion.contains("corpaliv.cl", ignoreCase = true)
        }
        assertFalse(contieneReal)
    }
}
