package com.duoc.corpaliv.viewmodel

// archivo de pruebas unitarias para MainViewModel (controla eventos de navegacion en SharedFlow)
// recolecta los eventos emitidos usando UnconfinedTestDispatcher y verifica que correspondan a la accion

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

    // fija Dispatchers.Main de prueba y crea un MainViewModel limpio antes de cada test
    @Before
    fun configurar() {
        Dispatchers.setMain(dispatcher)
        viewModel = MainViewModel()
    }

    // restaura Dispatchers.Main al finalizar cada test
    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    // --- navegarA ---

    // al llamar navegarA(Detalle), espera que el flujo emita exactamente NavigateTo(Screen.Detalle)
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

    // al llamar navegarA(Perfil), espera que el flujo emita exactamente NavigateTo(Screen.Perfil)
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

    // verifica que navegarA(AcercaDe) no emita por error una ruta distinta como Screen.Home
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

    // --- navegarBottom ---

    // al tocar la pestaña Catalogo en barra inferior, emite NavigateBottom(Screen.Catalogo)
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

    // al tocar la pestaña Contacto en barra inferior, emite NavigateBottom(Screen.Contacto)
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

    // verifica que navegarBottom emita NavigateBottom y no se confunda con NavigateTo normal
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

    // --- ingresarAlHome y cerrarSesion ---

    // al iniciar sesion con exito, espera el evento LoginExitoso
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

    // al cerrar sesion, espera el evento CerrarSesion
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

    // caso borde: emitir cerrarSesion cuando nadie esta escuchando el SharedFlow no debe lanzar excepcion
    @Test
    fun cerrarSesion_caminoMalo_sinSuscriptoresNoFalla() = runTest {
        viewModel.cerrarSesion()
        advanceUntilIdle()
        assertTrue(true)
    }

    // --- volverAtras ---

    // al llamar volverAtras(), espera el evento PopBackStack
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

    // si se llama dos veces seguidas, debe emitir dos eventos PopBackStack en el mismo orden
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

    // verifica que volverAtras() nunca emita LoginExitoso por error
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
