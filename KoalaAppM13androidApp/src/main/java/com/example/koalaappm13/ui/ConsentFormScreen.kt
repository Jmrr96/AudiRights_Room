package com.example.koalaappm13.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.koalaappm13.ConsentForm
import com.example.koalaappm13.database.ConsentFormViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConsentFormScreen(
    viewModel: ConsentFormViewModel,
    onConsentSaved: () -> Unit
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()

    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var lugarGrabacion by remember { mutableStateOf("") }
    var descripcionUso by remember { mutableStateOf("") }
    var fechaNacimientoTexto by remember { mutableStateOf("") }
    var esMayor by remember { mutableStateOf(false) }
    var esMenor by remember { mutableStateOf(false) }
    var errorEdad by remember { mutableStateOf<String?>(null) }

    // Estados de progenitores
    var progenitor1Nombre by remember { mutableStateOf("") }
    var progenitor1Apellidos by remember { mutableStateOf("") }
    var progenitor1Dni by remember { mutableStateOf("") }
    var progenitor1Telefono by remember { mutableStateOf("") }
    var progenitor1Direccion by remember { mutableStateOf("") }
    var progenitor1Consentimiento by remember { mutableStateOf(false) }
    var progenitor2Nombre by remember { mutableStateOf("") }
    var progenitor2Apellidos by remember { mutableStateOf("") }
    var progenitor2Dni by remember { mutableStateOf("") }
    var progenitor2Telefono by remember { mutableStateOf("") }
    var progenitor2Direccion by remember { mutableStateOf("") }
    var progenitor2Consentimiento by remember { mutableStateOf(false) }
    var soloUnProgenitor by remember { mutableStateOf(false) }

    // Firma
    var firmaPath by remember { mutableStateOf<String?>(null) }
    var signatureBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Configuración de la cámara
    val photoFile = remember {
        File.createTempFile("firma_", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
    }
    val photoUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) firmaPath = photoFile.absolutePath
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) cameraLauncher.launch(photoUri)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp)
        ) {
            // Encabezado con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF3A3A6A),  // Color primario más oscuro
                                Color(0xFF23235B)   // Color secundario más oscuro
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = Color(0xFF6C63FF).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(32.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Formulario",
                            tint = Color(0xFF6C63FF),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "Formulario de Consentimiento",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                        )
                        Text(
                            text = "Complete todos los campos requeridos",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
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
                        .padding(36.dp)
                        .fillMaxWidth()
                ) {
                    // Datos personales
                    SectionHeader(
                        icon = Icons.Default.Person,
                        title = "Datos Personales",
                        subtitle = "Información básica del participante"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF3A3A6A)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )

                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = { apellidos = it },
                        label = { Text("Apellidos") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF3A3A6A)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )

                    OutlinedTextField(
                        value = dni,
                        onValueChange = { dni = it },
                        label = { Text("DNI") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = Color(0xFF3A3A6A)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color(0xFF3A3A6A)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico (opcional)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF3A3A6A)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )

                    OutlinedTextField(
                        value = fechaNacimientoTexto,
                        onValueChange = { fechaNacimientoTexto = it },
                        label = { Text("Fecha de nacimiento (dd/MM/yyyy)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Color(0xFF3A3A6A)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )

                    // Estado de edad
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        backgroundColor = Color(0xFFEDE7F6),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Estado de Edad",
                                color = Color(0xFF3A3A6A),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Checkbox(
                                        checked = esMayor,
                                        onCheckedChange = {
                                            esMayor = it
                                            esMenor = !it
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF3A3A6A),
                                            uncheckedColor = Color(0xFFBDBDBD)
                                        )
                                    )
                                    Text(
                                        text = "Soy mayor de edad",
                                        color = Color(0xFF23235B)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Checkbox(
                                        checked = esMenor,
                                        onCheckedChange = {
                                            esMenor = it
                                            esMayor = !it
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF3A3A6A),
                                            uncheckedColor = Color(0xFFBDBDBD)
                                        )
                                    )
                                    Text(
                                        text = "Sesión para menor",
                                        color = Color(0xFF23235B)
                                    )
                                }
                            }
                        }
                    }

                    val fechaNacimientoEpoch = remember(fechaNacimientoTexto) {
                        try {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaNacimientoTexto)?.time
                        } catch (e: Exception) { null }
                    }

                    val edad = remember(fechaNacimientoEpoch) {
                        if (fechaNacimientoEpoch != null) {
                            val birth = Calendar.getInstance().apply { timeInMillis = fechaNacimientoEpoch }
                            val today = Calendar.getInstance()
                            var years = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
                            if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) years--
                            years
                        } else null
                    }

                    errorEdad = when {
                        fechaNacimientoTexto.isNotBlank() && edad != null && esMayor && edad < 18 -> "No coincide: eres menor"
                        fechaNacimientoTexto.isNotBlank() && edad != null && esMenor && edad >= 18 -> "No coincide: eres mayor"
                        else -> null
                    }

                    if (errorEdad != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            backgroundColor = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color(0xFFF87171)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorEdad!!,
                                    color = Color(0xFFF87171)
                                )
                            }
                        }
                    }

                    // Sección de progenitores
                    if (esMenor) {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeader(
                            icon = Icons.Default.FamilyRestroom,
                            title = "Datos del Progenitor 1",
                            subtitle = "Información del tutor legal"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = progenitor1Nombre,
                            onValueChange = { progenitor1Nombre = it },
                            label = { Text("Nombre") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF3A3A6A)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3A3A6A),
                                unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )

                        OutlinedTextField(
                            value = progenitor1Apellidos,
                            onValueChange = { progenitor1Apellidos = it },
                            label = { Text("Apellidos") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF3A3A6A)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3A3A6A),
                                unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )

                        OutlinedTextField(
                            value = progenitor1Dni,
                            onValueChange = { progenitor1Dni = it },
                            label = { Text("DNI") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = Color(0xFF3A3A6A)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3A3A6A),
                                unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )

                        OutlinedTextField(
                            value = progenitor1Telefono,
                            onValueChange = { progenitor1Telefono = it },
                            label = { Text("Teléfono") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = Color(0xFF3A3A6A)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3A3A6A),
                                unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )

                        OutlinedTextField(
                            value = progenitor1Direccion,
                            onValueChange = { progenitor1Direccion = it },
                            label = { Text("Dirección") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    tint = Color(0xFF3A3A6A)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF3A3A6A),
                                unfocusedBorderColor = Color(0xFFBDBDBD)
                            )
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            backgroundColor = Color(0xFFEDE7F6),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Checkbox(
                                        checked = progenitor1Consentimiento,
                                        onCheckedChange = { progenitor1Consentimiento = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF3A3A6A),
                                            uncheckedColor = Color(0xFFBDBDBD)
                                        )
                                    )
                                    Text(
                                        text = "Progenitor 1 consiente la cesión",
                                        color = Color(0xFF23235B)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = soloUnProgenitor,
                                        onCheckedChange = { soloUnProgenitor = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF3A3A6A),
                                            uncheckedColor = Color(0xFFBDBDBD)
                                        )
                                    )
                                    Text(
                                        text = "El menor solo tiene un progenitor",
                                        color = Color(0xFF23235B)
                                    )
                                }
                            }
                        }

                        if (!soloUnProgenitor) {
                            Spacer(modifier = Modifier.height(24.dp))
                            SectionHeader(
                                icon = Icons.Default.FamilyRestroom,
                                title = "Datos del Progenitor 2",
                                subtitle = "Información del segundo tutor legal"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = progenitor2Nombre,
                                onValueChange = { progenitor2Nombre = it },
                                label = { Text("Nombre") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF3A3A6A)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF3A3A6A),
                                    unfocusedBorderColor = Color(0xFFBDBDBD)
                                )
                            )

                            OutlinedTextField(
                                value = progenitor2Apellidos,
                                onValueChange = { progenitor2Apellidos = it },
                                label = { Text("Apellidos") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF3A3A6A)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF3A3A6A),
                                    unfocusedBorderColor = Color(0xFFBDBDBD)
                                )
                            )

                            OutlinedTextField(
                                value = progenitor2Dni,
                                onValueChange = { progenitor2Dni = it },
                                label = { Text("DNI") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = Color(0xFF3A3A6A)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF3A3A6A),
                                    unfocusedBorderColor = Color(0xFFBDBDBD)
                                )
                            )

                            OutlinedTextField(
                                value = progenitor2Telefono,
                                onValueChange = { progenitor2Telefono = it },
                                label = { Text("Teléfono") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = Color(0xFF3A3A6A)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF3A3A6A),
                                    unfocusedBorderColor = Color(0xFFBDBDBD)
                                )
                            )

                            OutlinedTextField(
                                value = progenitor2Direccion,
                                onValueChange = { progenitor2Direccion = it },
                                label = { Text("Dirección") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null,
                                        tint = Color(0xFF3A3A6A)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF3A3A6A),
                                    unfocusedBorderColor = Color(0xFFBDBDBD)
                                )
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                backgroundColor = Color(0xFFEDE7F6),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Checkbox(
                                        checked = progenitor2Consentimiento,
                                        onCheckedChange = { progenitor2Consentimiento = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color(0xFF3A3A6A),
                                            uncheckedColor = Color(0xFFBDBDBD)
                                        )
                                    )
                                    Text(
                                        text = "Progenitor 2 consiente la cesión",
                                        color = Color(0xFF23235B)
                                    )
                                }
                            }
                        }
                    }

                    // Detalles de la grabación
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader(
                        icon = Icons.Default.VideoCameraFront,
                        title = "Detalles de la Grabación",
                        subtitle = "Información sobre la cesión"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = lugarGrabacion,
                        onValueChange = { lugarGrabacion = it },
                        label = { Text("Lugar de la grabación") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF3A3A6A)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )

                    OutlinedTextField(
                        value = descripcionUso,
                        onValueChange = { descripcionUso = it },
                        label = { Text("Descripción del uso") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color(0xFF3A3A6A)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        minLines = 3,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )

                    // Firma
                    SectionHeader(
                        icon = Icons.Default.Draw,
                        title = "Firma Digital",
                        subtitle = "Firme en el espacio proporcionado"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val permission = Manifest.permission.CAMERA
                            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch(photoUri)
                            } else {
                                permissionLauncher.launch(permission)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF3A3A6A)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Foto de la firma",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        backgroundColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        elevation = 4.dp
                    ) {
                        SignatureCanvas(onSigned = { signatureBitmap = it })
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de guardar
                    val camposValidos = nombre.isNotBlank() && apellidos.isNotBlank() &&
                            telefono.isNotBlank() &&
                            (edad == null || (esMenor && edad < 14) || dni.isNotBlank()) &&
                            errorEdad == null &&
                            (!esMenor || progenitor1Consentimiento)

                    Button(
                        onClick = {
                            val form = ConsentForm(
                                nombre = nombre,
                                apellidos = apellidos,
                                dni = dni.takeIf { it.isNotBlank() },
                                fechaNacimiento = fechaNacimientoEpoch ?: 0L,
                                mayorEdad = esMayor,
                                menorEdad = esMenor,
                                telefono = telefono,
                                email = email.takeIf { it.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(it).matches() },
                                fechaGrabacion = System.currentTimeMillis(),
                                lugarGrabacion = lugarGrabacion,
                                firmaPath = firmaPath,
                                progenitor1Nombre = progenitor1Nombre.takeIf { it.isNotBlank() },
                                progenitor1Apellidos = progenitor1Apellidos.takeIf { it.isNotBlank() },
                                progenitor1Dni = progenitor1Dni.takeIf { it.isNotBlank() },
                                progenitor1Telefono = progenitor1Telefono.takeIf { it.isNotBlank() },
                                progenitor1Direccion = progenitor1Direccion.takeIf { it.isNotBlank() },
                                progenitor1Consentimiento = progenitor1Consentimiento,
                                progenitor2Nombre = progenitor2Nombre.takeIf { it.isNotBlank() },
                                progenitor2Apellidos = progenitor2Apellidos.takeIf { it.isNotBlank() },
                                progenitor2Dni = progenitor2Dni.takeIf { it.isNotBlank() },
                                progenitor2Telefono = progenitor2Telefono.takeIf { it.isNotBlank() },
                                progenitor2Direccion = progenitor2Direccion.takeIf { it.isNotBlank() },
                                progenitor2Consentimiento = progenitor2Consentimiento,
                                soloUnProgenitor = soloUnProgenitor,
                                descripcionUso = descripcionUso,
                                usuarioCreador = viewModel.currentUsername,
                                created_by = viewModel.currentUsername
                            )
                            viewModel.insertConsentForm(form)
                            onConsentSaved()
                        },
                        enabled = camposValidos,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF3A3A6A),
                            disabledBackgroundColor = Color(0xFFBDBDBD)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Guardar formulario",
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

@Composable
fun SignatureCanvas(onSigned: (Bitmap) -> Unit) {
    val path = remember { Path() }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
            .border(1.dp, Color(0xFF3A3A6A))
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    path.lineTo(change.position.x, change.position.y)
                }
            }
    ) {
        drawPath(path, Color(0xFF3A3A6A))
    }
}
