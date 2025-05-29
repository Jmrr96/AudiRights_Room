package com.example.koalaappm13.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TermsAndConditionsScreen(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Encabezado con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF3A3A6A),
                                Color(0xFF23235B)
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "Términos y Condiciones",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Contenido principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(32.dp)
                    ),
                shape = RoundedCornerShape(32.dp),
                elevation = 0.dp,
                backgroundColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
                            TÉRMINOS Y CONDICIONES

                            Este documento regula la cesión de derechos de imagen de personas adultas y menores de edad en España.

                            1. CONSENTIMIENTO Y AUTORIZACIÓN
                            - El consentimiento debe ser otorgado libremente, con información clara del uso de las imágenes.
                            - En el caso de menores, debe ser firmado por el padre, madre o tutor legal.
                            - Se requiere identificación válida de todas las partes involucradas.

                            2. USO DE IMÁGENES
                            - Las imágenes no podrán utilizarse para fines no autorizados.
                            - No se permitirá la difusión fuera de los contextos establecidos.
                            - Se respetará la dignidad y privacidad de los participantes.

                            3. DURACIÓN Y REVOCACIÓN
                            - Este acuerdo puede revocarse en cualquier momento por escrito.
                            - La revocación no afectará a los usos ya realizados.
                            - Se notificará por escrito la finalización del acuerdo.

                            4. RESPONSABILIDADES
                            - El participante garantiza que tiene derecho a ceder los derechos de imagen.
                            - En caso de menores, los tutores legales asumen la responsabilidad.
                            - Se mantendrá la confidencialidad de los datos personales.

                            5. DISPOSICIONES FINALES
                            - Este documento se rige por la legislación española.
                            - Cualquier modificación debe ser por escrito y firmada.
                            - Los términos no autorizados serán nulos de pleno derecho.

                            Nota: Este es un texto de muestra y no sustituye el asesoramiento legal profesional.
                        """.trimIndent(),
                        style = MaterialTheme.typography.body1,
                        color = Color(0xFF3A3A6A),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF3A3A6A)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Volver",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
} 