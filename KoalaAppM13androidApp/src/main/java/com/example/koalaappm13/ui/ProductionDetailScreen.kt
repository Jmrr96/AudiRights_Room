package com.example.koalaappm13.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.koalaappm13.database.Production
import com.example.koalaappm13.database.ProductionViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProductionDetailScreen(
    production: Production,
    viewModel: ProductionViewModel,
    onDelete: () -> Unit,
    onEstadoChange: (String) -> Unit,
    onBack: () -> Unit,
    isEditable: Boolean = true,
    showDeleteButton: Boolean = true
) {
    var currentProduction by remember { mutableStateOf(production) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    var selectedEstado by remember { mutableStateOf(currentProduction.estadoSesion ?: "") }
    val context = LocalContext.current

    // Edición grabación
    val isEditingGrabacion = remember { mutableStateOf(false) }
    val editedGrabacion = remember { mutableStateOf(currentProduction.grabacion ?: "") }

    // Edición lugar de grabación
    val isEditingLugar = remember { mutableStateOf(false) }
    val editedLugar = remember { mutableStateOf(currentProduction.lugarGrabacion ?: "") }

    val estadoTexto = when (currentProduction.estadoSesion) {
        "válida" -> "Validada"
        "no_válida" -> "Nula"
        else -> "Sin validar"
    }

    val estadoColor = when (currentProduction.estadoSesion) {
        "válida" -> Color(0xFF2E7D32)
        "no_válida" -> Color(0xFFC62828)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                    
                    Column {
                        Text(
                            text = "Detalle de Producción",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ID: ${currentProduction.id}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Contenido principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                elevation = 0.dp,
                backgroundColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    // Sección de datos personales
                    SectionHeader(
                        icon = Icons.Default.Person,
                        title = "Datos Personales",
                        subtitle = "Información básica del participante"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow("Nombre", currentProduction.nombre ?: "")
                    InfoRow("Apellidos", currentProduction.apellidos ?: "")
                    InfoRow("DNI", currentProduction.dni ?: "")
                    InfoRow("Teléfono", currentProduction.telefono ?: "")
                    InfoRow("Email", currentProduction.email ?: "No especificado")
                    InfoRow("Categoría Edad", currentProduction.edadCategoria ?: "")
                    InfoRow("Programa", currentProduction.programa ?: "")
                    InfoRow("Dirección", currentProduction.direccion ?: "")

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sección de grabación
                    SectionHeader(
                        icon = Icons.Default.VideoCameraFront,
                        title = "Detalles de Grabación",
                        subtitle = "Información de la cesión"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow("Fecha inicio", dateFormat.format(Date(currentProduction.fechaInicio)))
                    currentProduction.fechaFin?.let {
                        InfoRow("Fecha fin", dateFormat.format(Date(it)))
                    }

                    // Lugar de grabación editable
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        backgroundColor = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Lugar de grabación",
                                    style = MaterialTheme.typography.subtitle1,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { isEditingLugar.value = !isEditingLugar.value }) {
                                    Icon(
                                        imageVector = if (isEditingLugar.value) Icons.Default.Close else Icons.Default.Edit,
                                        contentDescription = if (isEditingLugar.value) "Cancelar" else "Editar"
                                    )
                                }
                            }

                            AnimatedVisibility(visible = isEditingLugar.value) {
                                Column {
                                    OutlinedTextField(
                                        value = editedLugar.value,
                                        onValueChange = { editedLugar.value = it },
                                        label = { Text("Editar lugar de grabación") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    if (editedLugar.value != currentProduction.lugarGrabacion) {
                                        Button(
                                            onClick = {
                                                val updated = currentProduction.copy(lugarGrabacion = editedLugar.value)
                                                viewModel.updateProduction(updated)
                                                currentProduction = updated
                                                Toast.makeText(context, "Lugar de grabación actualizado", Toast.LENGTH_SHORT).show()
                                                isEditingLugar.value = false
                                            },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text("Guardar")
                                        }
                                    }
                                }
                            }

                            AnimatedVisibility(visible = !isEditingLugar.value) {
                                Text(
                                    text = editedLugar.value.ifBlank { "Sin especificar" },
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }

                    // Sección de estado
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader(
                        icon = Icons.Default.Info,
                        title = "Estado de la Cesión",
                        subtitle = "Validación y términos"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        backgroundColor = estadoColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Estado actual: $estadoTexto",
                                color = estadoColor,
                                fontWeight = FontWeight.Bold
                            )
                            currentProduction.estadoValidadoPor?.let {
                                Text(
                                    text = "Validado por: $it",
                                    color = estadoColor.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    if (isEditable) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = { selectedEstado = "válida" },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF2E7D32)
                                ),
                                border = BorderStroke(1.dp, Color(0xFF2E7D32))
                            ) {
                                Text("Marcar como válida")
                            }

                            OutlinedButton(
                                onClick = { selectedEstado = "no_válida" },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFC62828)
                                ),
                                border = BorderStroke(1.dp, Color(0xFFC62828))
                            ) {
                                Text("Marcar como no válida")
                            }
                        }

                        if (selectedEstado.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val updated = currentProduction.copy(
                                        estadoSesion = selectedEstado,
                                        estadoValidadoPor = viewModel.currentUsername
                                    )
                                    viewModel.updateProduction(updated)
                                    currentProduction = updated
                                    Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFF3A3A6A)
                                )
                            ) {
                                Text("Guardar estado")
                            }
                        }
                    }

                    // Sección de archivos adjuntos
                    if (currentProduction.imagenPath != null || currentProduction.firmaPath != null) {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeader(
                            icon = Icons.Default.AttachFile,
                            title = "Archivos Adjuntos",
                            subtitle = "Imágenes y firmas"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        currentProduction.imagenPath?.let { path ->
                            val file = File(path)
                            if (file.exists()) {
                                val bitmap = remember(path) { BitmapFactory.decodeFile(path) }
                                bitmap?.let {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Imagen cedida",
                                                style = MaterialTheme.typography.subtitle1
                                            )
                                            currentProduction.descripcionImagen?.takeIf { it.isNotBlank() }?.let { desc ->
                                                Text(
                                                    text = desc,
                                                    style = MaterialTheme.typography.body2,
                                                    modifier = Modifier.padding(vertical = 8.dp)
                                                )
                                            }
                                            Image(
                                                bitmap = it.asImageBitmap(),
                                                contentDescription = "Imagen cedida",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(180.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        currentProduction.firmaPath?.let { path ->
                            val file = File(path)
                            if (file.exists()) {
                                val bitmap = remember(path) { BitmapFactory.decodeFile(path) }
                                bitmap?.let {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Text(
                                                text = "Firma digital",
                                                style = MaterialTheme.typography.subtitle1
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Image(
                                                bitmap = it.asImageBitmap(),
                                                contentDescription = "Firma",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Botones de acción
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                generatePdfToDownloads(context, currentProduction)
                                Toast.makeText(context, "PDF generado en Descargas", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF3A3A6A)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Descargar PDF")
                        }

                        if (showDeleteButton) {
                            Button(
                                onClick = onDelete,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFFC62828)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.subtitle2,
            color = Color(0xFF666666),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(0.6f)
        )
    }
}

fun generatePdfToDownloads(context: Context, production: Production) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint().apply {
        textSize = 12f
        color = android.graphics.Color.BLACK
    }

    var y = 30
    fun drawLine(text: String) {
        canvas.drawText(text, 30f, y.toFloat(), paint)
        y += 20
    }

    drawLine("Detalles de la Producción")
    drawLine("Nombre: ${production.nombre}")
    drawLine("Apellidos: ${production.apellidos}")
    drawLine("DNI: ${production.dni}")
    drawLine("Teléfono: ${production.telefono}")
    drawLine("Dirección: ${production.direccion}")
    drawLine("Email: ${production.email}")
    drawLine("Categoría Edad: ${production.edadCategoria}")
    drawLine("Programa: ${production.programa}")
    drawLine("Lugar de grabación: ${production.lugarGrabacion}")
    drawLine("Descripción: ${production.descripcion}")
    drawLine("Grabación: ${production.grabacion ?: ""}")
    drawLine("Estado: ${production.estadoSesion ?: "Sin validar"}")
    drawLine("Validado por: ${production.estadoValidadoPor ?: "No asignado"}")
    drawLine("Términos y condiciones: Aceptados")

    if (production.edadCategoria == "menor") {
        drawLine("Progenitor 1:")
        drawLine(" - Nombre: ${production.progenitor1Nombre}")
        drawLine(" - Apellidos: ${production.progenitor1Apellidos}")
        drawLine(" - DNI: ${production.progenitor1Dni}")
        drawLine(" - Teléfono: ${production.progenitor1Telefono}")
        drawLine(" - Dirección: ${production.progenitor1Direccion}")
        drawLine(" - Consentimiento: ${if (production.progenitor1Consentimiento == true) "Sí" else "No"}")

        if (production.soloUnProgenitor != true) {
            drawLine("Progenitor 2:")
            drawLine(" - Nombre: ${production.progenitor2Nombre}")
            drawLine(" - Apellidos: ${production.progenitor2Apellidos}")
            drawLine(" - DNI: ${production.progenitor2Dni}")
            drawLine(" - Teléfono: ${production.progenitor2Telefono}")
            drawLine(" - Dirección: ${production.progenitor2Direccion}")
            drawLine(" - Consentimiento: ${if (production.progenitor2Consentimiento == true) "Sí" else "No"}")
        }
    }

    production.firmaPath?.let { path ->
        val file = File(path)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            bitmap?.let {
                val scaled = Bitmap.createScaledBitmap(it, 200, 100, true)
                canvas.drawBitmap(scaled, 30f, y.toFloat(), null)
                y += 120
                drawLine("Firma digital incluida")
            }
        }
    }
    production.imagenPath?.let { path ->
        val file = File(path)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            bitmap?.let {
                val scaledImage = Bitmap.createScaledBitmap(it, 400, 300, true)
                canvas.drawBitmap(scaledImage, 30f, y.toFloat(), null)
                y += 320
                drawLine("Imagen cedida incluida")
            }
        }


    }
    production.descripcionImagen?.takeIf { it.isNotBlank() }?.let { descripcion ->
        drawLine("Descripción de la imagen:")
        // Si es larga, divídela en líneas de texto de 60 caracteres aprox
        val wrappedLines = descripcion.chunked(60)
        for (line in wrappedLines) {
            drawLine("  $line")
        }
    }

    pdfDocument.finishPage(page)

    val fileName = "Cesion_${production.usuarioCreador}_${System.currentTimeMillis()}.pdf"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/")
    }

    val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }
    }

    pdfDocument.close()
}
