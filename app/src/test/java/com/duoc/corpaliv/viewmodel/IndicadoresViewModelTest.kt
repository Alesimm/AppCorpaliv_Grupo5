package com.duoc.corpaliv.viewmodel

// archivo de pruebas unitarias para IndicadoresViewModel (estado de uf, utm y dolar en contacto)
// usa mockk para simular MindicadorRepository y StandardTestDispatcher para controlar el tiempo

import com.duoc.corpaliv.data.IndicadoresResumen
import com.duoc.corpaliv.data.MindicadorRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IndicadoresViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: MindicadorRepository

    private val resumenEjemplo = IndicadoresResumen(
        uf = 38500.0,
        utm = 67294.0,
        dolar = 950.0,
        fecha = "2026-11-26"
    )

    // reemplaza Dispatchers.Main por un dispatcher de prueba para que viewModelScope funcione en junit
    @Before
    fun configurar() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
    }

    // restaura Dispatchers.Main al terminar cada test para no afectar otras pruebas
    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    // --- carga inicial ---

    // al instanciar el viewmodel carga los indicadores, guarda el resumen y apaga el spinner cargando
    @Test
    fun init_caminoFeliz1_cargaIndicadoresYApagaCargando() = runTest {
        coEvery { repository.obtenerResumen() } returns resumenEjemplo

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        assertFalse(vm.estado.value.cargando)
        assertEquals(resumenEjemplo, vm.estado.value.resumen)
        assertFalse(vm.estado.value.esError)
    }

    // en la carga automatica inicial no debe aparecer el banner verde de "Indicadores actualizados"
    @Test
    fun init_caminoFeliz2_cargaAutomaticaNoMuestraBannerManual() = runTest {
        coEvery { repository.obtenerResumen() } returns resumenEjemplo

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        assertNull(vm.estado.value.mensajeEstado)
    }

    // si no hay internet al iniciar, marca esError=true, deja resumen en null y muestra aviso de sin conexion
    @Test
    fun init_caminoMalo_sinRedDejaErrorVisibleYSinResumen() = runTest {
        coEvery { repository.obtenerResumen() } throws RuntimeException("Sin internet")

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        assertFalse(vm.estado.value.cargando)
        assertNull(vm.estado.value.resumen)
        assertTrue(vm.estado.value.esError)
        assertEquals("Sin conexión para consultar mindicador.cl", vm.estado.value.mensajeEstado)
    }

    // --- recarga manual ---

    // al tocar "Actualizar" (manual=true) muestra el aviso verde antes de que pasen los 2500ms del delay
    @Test
    fun cargarIndicadores_caminoFeliz1_muestraAvisoVerdeAlActualizar() = runTest {
        coEvery { repository.obtenerResumen() } returns resumenEjemplo

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        vm.cargarIndicadores(manual = true)
        advanceTimeBy(400)

        assertEquals("Indicadores actualizados", vm.estado.value.mensajeEstado)
        assertFalse(vm.estado.value.esError)
        advanceUntilIdle()
    }

    // al avanzar todo el tiempo pendiente, el aviso verde se oculta automaticamente tras 2500ms
    @Test
    fun cargarIndicadores_caminoFeliz2_avisoVerdeSeOcultaTras2500ms() = runTest {
        coEvery { repository.obtenerResumen() } returns resumenEjemplo

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        vm.cargarIndicadores(manual = true)
        advanceUntilIdle()

        assertNull(vm.estado.value.mensajeEstado)
        assertNotNull(vm.estado.value.resumen)
    }

    // si falla la recarga manual (ej. modo avion), conserva los ultimos valores cargados y avisa sin conexion
    @Test
    fun cargarIndicadores_caminoMalo_enModoAvionConservaUltimosValoresYAvisa() = runTest {
        coEvery { repository.obtenerResumen() } returns resumenEjemplo

        val vm = IndicadoresViewModel(repository)
        advanceUntilIdle()

        coEvery { repository.obtenerResumen() } throws RuntimeException("Modo avion")
        vm.cargarIndicadores(manual = true)
        advanceTimeBy(400)

        assertTrue(vm.estado.value.esError)
        assertEquals(resumenEjemplo, vm.estado.value.resumen)
        assertEquals("Sin conexión · mostrando últimos valores", vm.estado.value.mensajeEstado)
        advanceUntilIdle()
    }
}
