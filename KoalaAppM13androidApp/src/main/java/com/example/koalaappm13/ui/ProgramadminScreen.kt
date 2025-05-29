package com.example.koalaappm13.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.koalaappm13.database.ProgramaTvViewModel
import com.example.koalaappm13.database.ProgramaTV

@Composable
fun ProgramaAdminScreen(
    viewModel: ProgramaTvViewModel,
    onBack: () -> Unit
) {
    val programas by viewModel.allProgramas.collectAsState()
    var nuevoPrograma by remember { mutableStateOf("") }
    var editandoPrograma: ProgramaTV? by remember { mutableStateOf(null) }
    var nombreEditado by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
            .padding(16.dp)
    ) {
        Column {
            // Botón de retroceso
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF6C63FF)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Volver",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Encabezado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                backgroundColor = Color(0xFFEDE7F6),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = null,
                            tint = Color(0xFF3A3A6A)
                        )
                        Text(
                            text = "Gestión de Programas de TV",
                            style = MaterialTheme.typography.h5,
                            color = Color(0xFF3A3A6A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Formulario de entrada
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                backgroundColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = if (editandoPrograma != null) nombreEditado else nuevoPrograma,
                        onValueChange = {
                            if (editandoPrograma != null) nombreEditado = it else nuevoPrograma = it
                        },
                        label = {
                            Text(
                                if (editandoPrograma != null) "Editar nombre del programa" else "Nuevo programa",
                                color = Color(0xFF3A3A6A)
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (editandoPrograma != null) {
                            TextButton(
                                onClick = {
                                    editandoPrograma = null
                                    nombreEditado = ""
                                }
                            ) {
                                Text(
                                    "Cancelar",
                                    color = Color(0xFF3A3A6A)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(
                            onClick = {
                                if (editandoPrograma != null) {
                                    val actualizado = editandoPrograma!!.copy(nombre = nombreEditado.trim())
                                    viewModel.updatePrograma(actualizado)
                                    editandoPrograma = null
                                    nombreEditado = ""
                                } else {
                                    viewModel.addPrograma(nuevoPrograma.trim())
                                    nuevoPrograma = ""
                                }
                            },
                            enabled = if (editandoPrograma != null) nombreEditado.isNotBlank() else nuevoPrograma.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF3A3A6A),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                if (editandoPrograma != null) "Guardar cambios" else "Añadir",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Lista de programas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                backgroundColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Programas actuales",
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFF3A3A6A),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(programas) { programa ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = Color(0xFFF8FAFF),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = programa.nombre,
                                        style = MaterialTheme.typography.body1,
                                        color = Color(0xFF3A3A6A)
                                    )

                                    Row {
                                        IconButton(
                                            onClick = {
                                                editandoPrograma = programa
                                                nombreEditado = programa.nombre
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Editar",
                                                tint = Color(0xFF3A3A6A)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                viewModel.deletePrograma(programa)
                                            }
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = Color(0xFFE57373)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
