package com.duoc.corpaliv.viewmodel

// pruebas unitarias de MainViewModel para verificar todos los eventos de navegacion
// valida 2 caminos felices y 1 caso borde/negativo por cada bloque de acciones

import com.duoc.corpaliv.navigation.NavigationEvent
import com.duoc.corpaliv.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MainViewModel

    @Before
    fun configurar() {
        Dispatchers.setMain(dispatcher)
        viewModel = MainViewModel()
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    // --- navegarA (2 felices, 1 negativo) ---

    @Test
    fun navegarA_caminoFeliz1_emiteEventoHaciaDetalle() = runTest {
        val eventos = mutableListOf<NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.navegarA(Screen.Detalle)
        advanceUntilIdle()

        assertEquals(listOf(NavigationEvent.NavigateTo(Screen.Detalle)), eventos)
        job.cancel()
    }

    @Test
    fun navegarA_caminoFeliz2_emiteEventoHaciaPerfil() = runTest {
        val eventos = mutableListOf<NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.navegarA(Screen.Perfil)
        advanceUntilIdle()

        assertEquals(listOf(NavigationEvent.NavigateTo(Screen.Perfil)), eventos)
        job.cancel()
    }

    @Test
    fun navegarA_caminoMalo_noEmiteRutaDistintaALaPedida() = runTest {
        val eventos = mutableListOf<NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.navegarA(Screen.AcercaDe)
        advanceUntilIdle()

        assertNotEquals(NavigationEvent.NavigateTo(Screen.Home), eventos.first())
        job.cancel()
    }

    // --- navegarBottom (2 felices, 1 negativo) ---

    @Test
    fun navegarBottom_caminoFeliz1_emiteEventoHaciaCatalogo() = runTest {
        val eventos = mutableListOf<NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.navegarBottom(Screen.Catalogo)
        advanceUntilIdle()

        assertEquals(listOf(NavigationEvent.NavigateBottom(Screen.Catalogo)), eventos)
        job.cancel()
    }

    @Test
    fun navegarBottom_caminoFeliz2_emiteEventoHaciaContacto() = runTest {
        val eventos = mutableListOf<NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.navegarBottom(Screen.Contacto)
        advanceUntilIdle()

        assertEquals(listOf(NavigationEvent.NavigateBottom(Screen.Contacto)), eventos)
        job.cancel()
    }

    @Test
    fun navegarBottom_caminoMalo_noSeConfundeConNavigateTo() = runTest {
        val eventos = mutableListOf<NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.navegarBottom(Screen.Home)
        advanceUntilIdle()

        assertTrue(eventos.first() is NavigationEvent.NavigateBottom)
        job.cancel()
    }

    // --- ingresarAlHome y cerrarSesion (2 felices, 1 borde) ---

    @Test
    fun ingresarAlHome_caminoFeliz1_emiteLoginExitoso() = runTest {
        val eventos = mutableListOf<NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.ingresarAlHome()
        advanceUntilIdle()

        assertEquals(listOf(NavigationEvent.LoginExitoso), eventos)
        job.cancel()
    }

    @Test
    fun cerrarSesion_caminoFeliz2_emiteCerrarSesion() = runTest {
        val eventos = mutableListOf<NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.cerrarSesion()
        advanceUntilIdle()

        assertEquals(listOf(NavigationEvent.CerrarSesion), eventos)
        job.cancel()
    }

    @Test
    fun cerrarSesion_caminoMalo_sinSuscriptoresNoFalla() = runTest {
        viewModel.cerrarSesion()
        advanceUntilIdle()
        assertTrue(true)
    }

    // --- volverAtras (2 felices, 1 borde) ---

    @Test
    fun volverAtras_caminoFeliz1_emitePopBackStack() = runTest {
        val eventos = mutableListOf<NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.volverAtras()
        advanceUntilIdle()

        assertEquals(listOf(NavigationEvent.PopBackStack), eventos)
        job.cancel()
    }

    @Test
    fun volverAtras_caminoFeliz2_dosLlamadasEmitenDosEventosEnOrden() = runTest {
        val eventos = mutableListOf<NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.volverAtras()
        viewModel.volverAtras()
        advanceUntilIdle()

        assertEquals(2, eventos.size)
        assertTrue(eventos.all { it == NavigationEvent.PopBackStack })
        job.cancel()
    }

    @Test
    fun volverAtras_caminoMalo_noEmiteLoginExitoso() = runTest {
        val eventos = mutableListOf<NavigationEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvents.collect { eventos.add(it) }
        }

        viewModel.volverAtras()
        advanceUntilIdle()

        assertNotEquals(NavigationEvent.LoginExitoso, eventos.first())
        job.cancel()
    }
}
