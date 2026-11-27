package com.duoc.corpaliv.ui

// archivo de pruebas instrumentadas de interfaz grafica con jetpack compose (paquete androidTest)
// se ejecutan en emulador o dispositivo real usando createComposeRule para renderizar y simular toques
// cubren pantallas (AcercaDe, Login, Contact) y componentes reutilizables (BotonPrincipal, TarjetaContacto)

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

    // regla de compose que levanta cada pantalla en aislamiento y permite buscar nodos por texto y hacer clic
    @get:Rule
    val composeRule = createComposeRule()

    private fun obtenerApp(): Application = ApplicationProvider.getApplicationContext()

    // --- AcercaDeScreen ---

    // renderiza AcercaDeScreen y verifica que muestre el titulo principal y el area
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

    // verifica que en AcercaDeScreen se muestre la seccion ficticia "Alba Lab"
    @Test
    fun acercaDe_caminoFeliz2_muestraSeccionAlbaLab() {
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                AcercaDeScreen()
            }
        }
        composeRule.onNodeWithText("Alba Lab").assertIsDisplayed()
    }

    // verifica que no aparezca ningun correo o dominio real de corpaliv en pantalla
    @Test
    fun acercaDe_caminoMalo_noMuestraCorreoRealCorpalivCl() {
        composeRule.setContent {
            AppCorpaliv_Grupo5Theme {
                AcercaDeScreen()
            }
        }
        composeRule.onNodeWithText("corpaliv.cl", substring = true).assertDoesNotExist()
    }

    // --- Componentes reutilizables ---

    // verifica que BotonPrincipal muestre su texto en mayusculas y dispare el callback al hacer clic
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

    // verifica que al tocar la tarjeta de contacto se desplieguen encargado y correo
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

    // verifica que antes de tocar la tarjeta, el detalle del encargado permanezca oculto
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

    // --- LoginScreen ---

    // renderiza LoginScreen y verifica que muestre el boton INGRESAR y el enlace de recuperacion
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

    // al tocar "¿Olvidaste tu contraseña?", verifica que se abra el dialogo "Recuperar contraseña"
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

    // camino malo: al presionar INGRESAR con correo vacio, muestra el mensaje de error en pantalla
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

    // --- ContactScreen ---

    // renderiza ContactScreen y verifica las tarjetas de contacto, formulario y barra de indicadores uf
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

    // hace scroll hasta la barra de indicadores, la toca y verifica que aparezca el boton "Actualizar"
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

    // camino malo: hace scroll al boton ENVIAR SOLICITUD con formulario vacio y verifica error "Ingresa tu nombre"
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
