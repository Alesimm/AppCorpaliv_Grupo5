package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.navigation.Screen
import com.duoc.corpaliv.ui.components.AppBottomBar
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.components.TarjetaContactoDesplegable
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaPurple
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.utils.llamarPorTelefono

// informacion institucional sobre la fundacion corpaliv y la red alba lab
// explica su labor social e incluye telefonos para llamar desde el telefono

// vista con la mision de la fundacion y tarjetas de contacto telefonico
@Composable
fun AcercaDeScreen(
    alAbrirMenu: () -> Unit = {},
    alIrAPerfil: () -> Unit = {},
    alNavegarBottom: (Screen) -> Unit = {}
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "ACERCA DE",
                alAbrirMenu = alAbrirMenu,
                alIrAPerfil = alIrAPerfil
            )
        },
        bottomBar = {
            AppBottomBar(
                rutaActual = Screen.AcercaDe.route,
                alNavegar = alNavegarBottom
            )
        },
        containerColor = AlbaBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "CORPALIV",
                color = AlbaWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = "Organización sin fines de lucro que promueve la autonomía, la participación y la inclusión social y laboral de personas con discapacidad múltiple.",
                color = AlbaTextSecondary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            // tarjeta que explica el area educativa de educacion especial
            Card(
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = AlbaCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "EDUCACIÓN ESPECIAL",
                        color = AlbaPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Área de Formación Integral",
                        color = AlbaWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Acompañamiento pedagógico y desarrollo de habilidades comunicativas y de autonomía personal.",
                        color = AlbaTextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // tarjeta que explica la red de trabajo e inclusion sociolaboral
            Card(
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = AlbaCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "RED SOCIOLABORAL",
                        color = AlbaOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Alba Lab",
                        color = AlbaWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Formación en oficios, preparación para el empleo, elaboración de productos y vinculación con empresas, familias y donantes.",
                        color = AlbaTextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Text(
                text = "Canales de Contacto (Ficticios)",
                color = AlbaWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // tarjetas desplegables para tocar y llamar directamente a los encargados
            TarjetaContactoDesplegable(
                area = "Coordinación Alba Lab",
                encargado = "Equipo de Vinculación (Dato ficticio)",
                telefono = "+56900000000",
                correo = "contacto@ejemplo.cl",
                alLlamar = { numero -> llamarPorTelefono(context, numero) }
            )

            TarjetaContactoDesplegable(
                area = "Área de Voluntariado y Alianzas",
                encargado = "Coordinación Social (Dato ficticio)",
                telefono = "+56200000000",
                correo = "alianzas@ejemplo.cl",
                alLlamar = { numero -> llamarPorTelefono(context, numero) }
            )
        }
    }
}