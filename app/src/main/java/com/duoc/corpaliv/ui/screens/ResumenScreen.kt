package com.duoc.corpaliv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.corpaliv.navigation.Screen
import com.duoc.corpaliv.ui.components.AppBottomBar
import com.duoc.corpaliv.ui.components.AppTopBar
import com.duoc.corpaliv.ui.components.BotonPrincipal
import com.duoc.corpaliv.ui.components.BotonSecundario
import com.duoc.corpaliv.ui.theme.AlbaBlack
import com.duoc.corpaliv.ui.theme.AlbaBorder
import com.duoc.corpaliv.ui.theme.AlbaCard
import com.duoc.corpaliv.ui.theme.AlbaOrange
import com.duoc.corpaliv.ui.theme.AlbaTextSecondary
import com.duoc.corpaliv.ui.theme.AlbaWhite
import com.duoc.corpaliv.viewmodel.UsuarioViewModel

// pantalla 7 solicitud enviada con comprobante y folio oficial del mockup
@Composable
fun ResumenScreen(
    viewModel: UsuarioViewModel,
    alIrAInicio: () -> Unit,
    alIrAPerfil: () -> Unit,
    alAbrirMenu: () -> Unit,
    alNavegarBottom: (Screen) -> Unit
) {
    val solicitud by viewModel.solicitudSeleccionada.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "SOLICITUD",
                alAbrirMenu = alAbrirMenu,
                alIrAPerfil = alIrAPerfil
            )
        },
        bottomBar = {
            AppBottomBar(
                rutaActual = Screen.Contacto.route,
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
                .padding(20.dp)
        ) {
            // icono circular naranja con check de confirmacion
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .border(2.dp, AlbaOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = AlbaOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Solicitud enviada",
                    color = AlbaWhite,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                text = "El equipo de vinculación de CORPALIV revisará tu registro y te contactará.",
                color = AlbaTextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            val item = solicitud

            // tarjeta destacada con borde naranja que resalta el folio y estado
            Card(
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = AlbaCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AlbaOrange, RoundedCornerShape(2.dp))
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FOLIO",
                            color = AlbaTextSecondary,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item?.folio ?: "AL-2026-0042",
                            color = AlbaWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // etiqueta de estado recibida o pendiente
                    Box(
                        modifier = Modifier
                            .border(1.dp, AlbaOrange, RoundedCornerShape(2.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item?.estadoEnvio ?: "RECIBIDA",
                            color = AlbaOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // tabla de detalles con lineas divisorias finas igual al mockup
            Card(
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = AlbaCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AlbaBorder, RoundedCornerShape(2.dp))
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    FilaComprobante(titulo = "Fecha", valor = item?.fecha ?: "21-10-2026")
                    HorizontalDivider(color = AlbaBorder)
                    FilaComprobante(titulo = "Colaboración", valor = item?.colaboracion ?: "Alianza empresarial")
                    HorizontalDivider(color = AlbaBorder)
                    FilaComprobante(titulo = "Programa", valor = item?.programa ?: "Taller de Oficios")
                    HorizontalDivider(color = AlbaBorder)
                    FilaComprobante(titulo = "Nombre", valor = item?.nombre ?: "Marta Ejemplo")
                    HorizontalDivider(color = AlbaBorder)
                    FilaComprobante(titulo = "Correo", valor = item?.correo ?: "contacto@ejemplo.cl")
                    HorizontalDivider(color = AlbaBorder)
                    FilaComprobante(titulo = "RUT comercial", valor = item?.rutComercial ?: "11.111.111-1")
                    HorizontalDivider(color = AlbaBorder)
                    FilaComprobante(titulo = "Mensaje", valor = item?.mensaje ?: "Queremos abrir un puesto de práctica inclusivo.")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // botones inferiores de accion del mockup
            BotonSecundario(
                texto = "MIS SOLICITUDES",
                alHacerClick = alIrAPerfil
            )

            Spacer(modifier = Modifier.height(12.dp))

            BotonPrincipal(
                texto = "INICIO",
                alHacerClick = alIrAInicio
            )
        }
    }
}

// fila reutilizable para la tabla de datos del comprobante
@Composable
private fun FilaComprobante(titulo: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            color = AlbaTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            color = AlbaWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1.5f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}