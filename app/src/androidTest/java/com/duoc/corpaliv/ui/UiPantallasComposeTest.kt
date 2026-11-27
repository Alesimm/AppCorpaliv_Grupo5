package com.duoc.corpaliv.ui

// pruebas instrumentadas de interfaz grafica con jetpack compose en androidTest
// cubren 2 caminos felices y 1 camino malo por pantalla y componente principal

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duoc.corpaliv.ui.components.BotonPrincipal
import com.duoc.corpaliv.ui.components.TarjetaContactoDesplegable
import com.duoc.corpaliv.ui.screens.AcercaDeScreen
import com.duoc.corpaliv.ui.screens.ContactScreen
import com.duoc.corpaliv.ui.screens.LoginScreen
import com.duoc.corpaliv.ui.theme.AppCorpaliv_Grupo5Theme
import com.duoc.corpaliv.viewmodel.EstadoViewModel
import com.duoc.corpaliv.viewmodel.IndicadoresViewModel
import com.duoc.corpaliv.viewmodel.UsuarioViewModel
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UiPantallasComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun obtenerApp(): Application = ApplicationProvider.getApplicationContext()

    // --- AcercaDeScreen (2 felices, 1 malo) ---

    @Test
    fun acercaDe_caminoFeliz1_muestraTituloYMision() {
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                AcercaDeScreen()
            }
        }
        composeRule.onNodeWithText("CORPALIV").assertIsDisplayed()
        composeRule.onNodeWithText("Área de Formación Integral").assertIsDisplayed()
    }

    @Test
    fun acercaDe_caminoFeliz2_muestraSeccionAlbaLab() {
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                AcercaDeScreen()
            }
        }
        composeRule.onNodeWithText("Alba Lab").assertIsDisplayed()
    }

    @Test
    fun acercaDe_caminoMalo_noMuestraCorreoRealCorpalivCl() {
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                AcercaDeScreen()
            }
        }
        composeRule.onNodeWithText("contacto@corpaliv.cl", substring = true).assertDoesNotExist()
    }

    // --- Componentes reutilizables (2 felices, 1 malo) ---

    @Test
    fun botonPrincipal_caminoFeliz1_muestraTextoEnMayusculasYEjecutaClick() {
        var fuePresionado = false
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                BotonPrincipal(
                    texto = "Enviar prueba",
                    alHacerClick = { fuePresionado = true }
                )
            }
        }
        composeRule.onNodeWithText("ENVIAR PRUEBA").assertIsDisplayed().performClick()
        assertTrue(fuePresionado)
    }

    @Test
    fun tarjetaContacto_caminoFeliz2_despliegaDatosAlTocar() {
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                TarjetaContactoDesplegable(
                    area = "Coordinación General",
                    encargado = "Ana Pérez",
                    telefono = "+56 9 0000 0000",
                    correo = "ana@ejemplo.cl",
                    alLlamar = {}
                )
            }
        }
        composeRule.onNodeWithText("Coordinación General").performClick()
        composeRule.onNodeWithText("Encargado: Ana Pérez").assertIsDisplayed()
        composeRule.onNodeWithText("Correo: ana@ejemplo.cl").assertIsDisplayed()
    }

    @Test
    fun tarjetaContacto_caminoMalo_ocultaDetalleAntesDeTocar() {
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                TarjetaContactoDesplegable(
                    area = "Alianzas",
                    encargado = "Luis Rojas",
                    telefono = "+56 9 0000 0001",
                    correo = "luis@ejemplo.cl",
                    alLlamar = {}
                )
            }
        }
        composeRule.onNodeWithText("Encargado: Luis Rojas").assertDoesNotExist()
    }

    // --- LoginScreen (2 felices, 1 malo) ---

    @Test
    fun loginScreen_caminoFeliz1_muestraBotonIngresarYRecuperar() {
        val vm = UsuarioViewModel(obtenerApp())
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                LoginScreen(viewModel = vm)
            }
        }
        composeRule.onNodeWithText("INGRESAR").assertIsDisplayed()
        composeRule.onNodeWithText("¿Olvidaste tu contraseña?").assertIsDisplayed()
    }

    @Test
    fun loginScreen_caminoFeliz2_abreDialogoRecuperacionAlTocarEnlace() {
        val vm = UsuarioViewModel(obtenerApp())
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                LoginScreen(viewModel = vm)
            }
        }
        composeRule.onNodeWithText("¿Olvidaste tu contraseña?").performClick()
        composeRule.onNodeWithText("Recuperar contraseña").assertIsDisplayed()
    }

    @Test
    fun loginScreen_caminoMalo_muestraErrorSiCorreoVacioAlIngresar() {
        val vm = UsuarioViewModel(obtenerApp())
        vm.onCorreoLoginChange("")
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                LoginScreen(viewModel = vm)
            }
        }
        composeRule.onNodeWithText("INGRESAR").performClick()
        composeRule.onNodeWithText("Por favor, ingresa tu correo electrónico.").assertIsDisplayed()
    }

    // --- ContactScreen (2 felices, 1 malo) ---

    @Test
    fun contactScreen_caminoFeliz1_muestraTarjetasYDesplegableUf() {
        val app = obtenerApp()
        val usuarioVm = UsuarioViewModel(app)
        val estadoVm = EstadoViewModel(app)
        val indicadoresVm = IndicadoresViewModel()
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                ContactScreen(
                    viewModel = usuarioVm,
                    estadoViewModel = estadoVm,
                    indicadoresViewModel = indicadoresVm
                )
            }
        }
        composeRule.onNodeWithText("Datos de contacto").assertIsDisplayed()
        composeRule.onNodeWithText("Tu propuesta").assertIsDisplayed()
        composeRule.onNodeWithText("Valores de referencia (UF · UTM · Dólar)").assertIsDisplayed()
    }

    @Test
    fun contactScreen_caminoFeliz2_despliegaIndicadoresAlTocarBarra() {
        val app = obtenerApp()
        val usuarioVm = UsuarioViewModel(app)
        val estadoVm = EstadoViewModel(app)
        val indicadoresVm = IndicadoresViewModel()
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                ContactScreen(
                    viewModel = usuarioVm,
                    estadoViewModel = estadoVm,
                    indicadoresViewModel = indicadoresVm
                )
            }
        }
        composeRule.onNodeWithText("Valores de referencia (UF · UTM · Dólar)")
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithText("Actualizar").assertIsDisplayed()
    }

    @Test
    fun contactScreen_caminoMalo_muestraErroresAlEnviarFormularioVacio() {
        val app = obtenerApp()
        val usuarioVm = UsuarioViewModel(app)
        val estadoVm = EstadoViewModel(app)
        val indicadoresVm = IndicadoresViewModel()
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                ContactScreen(
                    viewModel = usuarioVm,
                    estadoViewModel = estadoVm,
                    indicadoresViewModel = indicadoresVm
                )
            }
        }
        composeRule.onNodeWithText("ENVIAR SOLICITUD").performScrollTo().performClick()
        composeRule.onNodeWithText("Ingresa tu nombre").assertIsDisplayed()
    }
}
