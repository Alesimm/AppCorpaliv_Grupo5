package com.duoc.corpaliv.ui.screens

// informacion institucional simulada sobre la organizacion y la red alba lab
// mantiene todos los datos de contacto y programas de forma ficticia

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.ui.components.BotonSecundario
import com.duoc.corpaliv.ui.components.TarjetaContactoDesplegable
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaGreen
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaPurple
import com.duoc.corpaliv.ui.theme.AlbaShapes
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.ui.utils.abrirEnlaceWeb
import com.duoc.corpaliv.ui.utils.llamarPorTelefono

// vista institucional con informacion y canales de contacto ficticios
@Composable
fun AcercaDeScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlbaBlack)
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

        Card(
            shape = AlbaShapes.tarjeta,
            colors = CardDefaults.cardColors(containerColor = AlbaCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
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
                    text = "Acompañamiento pedagógico y desarrollo de habilidades comunicativas, autonomía personal y participación comunitaria.",
                    color = AlbaTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Card(
            shape = AlbaShapes.tarjeta,
            colors = CardDefaults.cardColors(containerColor = AlbaCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
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
                    text = "Formación en oficios, preparación para el empleo, elaboración de artículos artesanales y vinculación con empresas, familias y donantes.",
                    color = AlbaTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Card(
            shape = AlbaShapes.tarjeta,
            colors = CardDefaults.cardColors(containerColor = AlbaCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AlbaBorder, AlbaShapes.tarjeta)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "EMPRESAS ALIADAS",
                    color = AlbaGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Redes de Colaboración",
                    color = AlbaWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Espacio de articulación para voluntariado corporativo, capacitaciones y aportes destinados a programas de inclusión.",
                    color = AlbaTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        BotonSecundario(
            texto = "VISITAR SITIO WEB EJEMPLO.CL",
            alHacerClick = { abrirEnlaceWeb(context, "https://ejemplo.cl") }
        )

        Text(
            text = "Canales de Contacto",
            color = AlbaWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        TarjetaContactoDesplegable(
            area = "Coordinación Alba Lab",
            encargado = "Equipo de Vinculación",
            telefono = "+56900000000",
            correo = "contacto@ejemplo.cl",
            alLlamar = { numero -> llamarPorTelefono(context, numero) }
        )

        TarjetaContactoDesplegable(
            area = "Área de Voluntariado y Alianzas",
            encargado = "Coordinación Social",
            telefono = "+56200000000",
            correo = "alianzas@ejemplo.cl",
            alLlamar = { numero -> llamarPorTelefono(context, numero) }
        )
    }
}
