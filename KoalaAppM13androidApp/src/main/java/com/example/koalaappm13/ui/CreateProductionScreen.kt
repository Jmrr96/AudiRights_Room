package com.example.koalaappm13.ui

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Patterns
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.koalaappm13.database.Production
import com.example.koalaappm13.database.ProductionViewModel
import com.example.koalaappm13.database.ProgramaTvViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import androidx.compose.ui.Alignment
import java.util.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material.OutlinedButton
import androidx.compose.ui.draw.shadow

import androidx.compose.ui.platform.LocalContext
import com.example.koalaappm13.TermsAndConditionsActivity
import com.example.koalaappm13.R
import androidx.compose.ui.res.painterResource



@Composable
fun CreateProductionScreen(
    viewModel: ProductionViewModel,
    programaViewModel: ProgramaTvViewModel,
    currentUsername: String,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var fechaNacimientoTexto by remember { mutableStateOf("") }
    var aceptoCondiciones by remember { mutableStateOf(false) }

    var esMayor by remember { mutableStateOf(false) }
    var esMenor by remember { mutableStateOf(false) }
    var errorEdad by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var firmaGuardada by remember { mutableStateOf(false) }

    var prog1Nombre by remember { mutableStateOf("") }
    var prog1Apellidos by remember { mutableStateOf("") }
    var prog1Dni by remember { mutableStateOf("") }
    var prog1Telefono by remember { mutableStateOf("") }
    var prog1Direccion by remember { mutableStateOf("") }
    var prog1Consentimiento by remember { mutableStateOf(false) }

    var prog2Nombre by remember { mutableStateOf("") }
    var prog2Apellidos by remember { mutableStateOf("") }
    var prog2Dni by remember { mutableStateOf("") }
    var prog2Telefono by remember { mutableStateOf("") }
    var prog2Direccion by remember { mutableStateOf("") }
    var prog2Consentimiento by remember { mutableStateOf(false) }

    var soloUnProgenitor by remember { mutableStateOf(false) }
    var quiereCederImagenes by remember { mutableStateOf(false) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var descripcionImagen by remember { mutableStateOf("") }
    var imagenGuardada by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
        imagenGuardada = false
    }


    val paths = remember { mutableStateListOf<List<Offset>>() }
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }

    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val fechaNacimiento = remember(fechaNacimientoTexto) {
        try { sdf.parse(fechaNacimientoTexto)?.time } catch (e: Exception) { null }
    }

    val edad = remember(fechaNacimiento) {
        fechaNacimiento?.let {
            val birth = Calendar.getInstance().apply { timeInMillis = it }
            val today = Calendar.getInstance()
            var years = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) years--
            years
        }
    }

    val requiereDNI = edad?.let { it >= 14 } ?: false
    val dniValido = !requiereDNI || (dni.matches(Regex("^[0-9]{8}[A-Za-z]$")) && validarLetraDNI(dni))
    val prog1DniValido = prog1Dni.isBlank() || (prog1Dni.matches(Regex("^[0-9]{8}[A-Za-z]$")) && validarLetraDNI(prog1Dni))
    val prog2DniValido = prog2Dni.isBlank() || (prog2Dni.matches(Regex("^[0-9]{8}[A-Za-z]$")) && validarLetraDNI(prog2Dni))

    val emailValido = email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val fechaValida = fechaNacimiento != null

    errorEdad = when {
        edad != null && esMayor && edad < 18 -> "Edad no válida para mayor"
        edad != null && esMenor && edad >= 18 -> "Edad no válida para menor"
        else -> null
    }
    val programas by programaViewModel.allProgramas.collectAsState()
    var selectedPrograma by remember { mutableStateOf<String?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val isFormValid = nombre.isNotBlank() &&
            apellidos.isNotBlank() &&
            telefono.isNotBlank() &&
            direccion.isNotBlank() &&
            (!requiereDNI || (dni.isNotBlank() && dniValido)) &&
            emailValido &&
            fechaValida &&
            aceptoCondiciones &&
            firmaGuardada &&
            (!quiereCederImagenes || imagenGuardada) &&
            errorEdad == null &&
            (!esMenor || (
                    prog1Consentimiento &&
                            prog1Nombre.isNotBlank() &&
                            prog1Apellidos.isNotBlank() &&
                            prog1Dni.isNotBlank() && prog1DniValido &&
                            prog1Telefono.isNotBlank() &&
                            prog1Direccion.isNotBlank() &&
                            (soloUnProgenitor || (
                                    prog2Consentimiento &&
                                            prog2Nombre.isNotBlank() &&
                                            prog2Apellidos.isNotBlank() &&
                                            prog2Dni.isNotBlank() && prog2DniValido &&
                                            prog2Telefono.isNotBlank() &&
                                            prog2Direccion.isNotBlank()
                                    ))
                    ))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8EAF6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A237E),
                                Color(0xFF0D47A1)
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
                    IconButton(onClick = { (context as? android.app.Activity)?.finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "Nueva Cesión de Derechos",
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
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = 8.dp,
                backgroundColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre", color = Color(0xFF1A237E)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1A237E),
                            unfocusedBorderColor = Color(0xFF3949AB),
                            textColor = Color(0xFF000000),
                            cursorColor = Color(0xFF1A237E)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = { apellidos = it },
                        label = { Text("Apellidos", color = Color(0xFF1A237E)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1A237E),
                            unfocusedBorderColor = Color(0xFF3949AB),
                            textColor = Color(0xFF000000),
                            cursorColor = Color(0xFF1A237E)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dni,
                        onValueChange = { dni = it },
                        label = { Text("DNI (si mayor de 14)", color = Color(0xFF1A237E)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1A237E),
                            unfocusedBorderColor = Color(0xFF3949AB),
                            textColor = Color(0xFF000000),
                            cursorColor = Color(0xFF1A237E)
                        )
                    )
                    if (!dniValido && dni.isNotBlank() && requiereDNI) {
                        Text("DNI no válido. Revisa la letra.", color = Color(0xFFD32F2F))
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fechaNacimientoTexto,
                        onValueChange = { fechaNacimientoTexto = it },
                        label = { Text("Fecha de nacimiento (dd/MM/yyyy)", color = Color(0xFF1A237E)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1A237E),
                            unfocusedBorderColor = Color(0xFF3949AB),
                            textColor = Color(0xFF000000),
                            cursorColor = Color(0xFF1A237E)
                        )
                    )
                    if (!fechaValida && fechaNacimientoTexto.isNotBlank()) {
                        Text("Fecha inválida. Usa formato dd/MM/yyyy", color = Color(0xFFD32F2F))
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = esMayor,
                            onCheckedChange = {
                                esMayor = it
                                esMenor = !it
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF1A237E),
                                uncheckedColor = Color(0xFF3949AB)
                            )
                        )
                        Text("Soy mayor de edad", color = Color(0xFF000000))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = esMenor,
                            onCheckedChange = {
                                esMenor = it
                                esMayor = !it
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF1A237E),
                                uncheckedColor = Color(0xFF3949AB)
                            )
                        )
                        Text("Sesión para menor de edad", color = Color(0xFF000000))
                    }

                    errorEdad?.let {
                        Text(it, color = Color(0xFFD32F2F))
                    }

                    if (esMenor) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Progenitor 1", style = MaterialTheme.typography.subtitle1, color = Color(0xFF1A237E))
                        OutlinedTextField(
                            value = prog1Nombre,
                            onValueChange = { prog1Nombre = it },
                            label = { Text("Nombre", color = Color(0xFF1A237E)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF1A237E),
                                unfocusedBorderColor = Color(0xFF3949AB),
                                textColor = Color(0xFF000000),
                                cursorColor = Color(0xFF1A237E)
                            )
                        )
                        OutlinedTextField(
                            value = prog1Apellidos,
                            onValueChange = { prog1Apellidos = it },
                            label = { Text("Apellidos", color = Color(0xFF1A237E)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF1A237E),
                                unfocusedBorderColor = Color(0xFF3949AB),
                                textColor = Color(0xFF000000),
                                cursorColor = Color(0xFF1A237E)
                            )
                        )
                        OutlinedTextField(
                            value = prog1Dni,
                            onValueChange = { prog1Dni = it },
                            label = { Text("DNI", color = Color(0xFF1A237E)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF1A237E),
                                unfocusedBorderColor = Color(0xFF3949AB),
                                textColor = Color(0xFF000000),
                                cursorColor = Color(0xFF1A237E)
                            )
                        )
                        if (!prog1DniValido && prog1Dni.isNotBlank()) {
                            Text("DNI no válido para el progenitor 1", color = Color(0xFFD32F2F))
                        }
                        OutlinedTextField(
                            value = prog1Telefono,
                            onValueChange = { prog1Telefono = it },
                            label = { Text("Teléfono", color = Color(0xFF1A237E)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF1A237E),
                                unfocusedBorderColor = Color(0xFF3949AB),
                                textColor = Color(0xFF000000),
                                cursorColor = Color(0xFF1A237E)
                            )
                        )
                        OutlinedTextField(
                            value = prog1Direccion,
                            onValueChange = { prog1Direccion = it },
                            label = { Text("Dirección", color = Color(0xFF1A237E)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF1A237E),
                                unfocusedBorderColor = Color(0xFF3949AB),
                                textColor = Color(0xFF000000),
                                cursorColor = Color(0xFF1A237E)
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = prog1Consentimiento,
                                onCheckedChange = { prog1Consentimiento = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF1A237E),
                                    uncheckedColor = Color(0xFF3949AB)
                                )
                            )
                            Text("Progenitor 1 consiente la sesión", color = Color(0xFF000000))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = soloUnProgenitor,
                                onCheckedChange = { soloUnProgenitor = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF1A237E),
                                    uncheckedColor = Color(0xFF3949AB)
                                )
                            )
                            Text("El menor solo tiene un progenitor", color = Color(0xFF000000))
                        }

                        if (!soloUnProgenitor) {
                            Text("Progenitor 2", style = MaterialTheme.typography.subtitle1, color = Color(0xFF1A237E))
                            OutlinedTextField(
                                value = prog2Nombre,
                                onValueChange = { prog2Nombre = it },
                                label = { Text("Nombre", color = Color(0xFF1A237E)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF1A237E),
                                    unfocusedBorderColor = Color(0xFF3949AB),
                                    textColor = Color(0xFF000000),
                                    cursorColor = Color(0xFF1A237E)
                                )
                            )
                            OutlinedTextField(
                                value = prog2Apellidos,
                                onValueChange = { prog2Apellidos = it },
                                label = { Text("Apellidos", color = Color(0xFF1A237E)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF1A237E),
                                    unfocusedBorderColor = Color(0xFF3949AB),
                                    textColor = Color(0xFF000000),
                                    cursorColor = Color(0xFF1A237E)
                                )
                            )
                            OutlinedTextField(
                                value = prog2Dni,
                                onValueChange = { prog2Dni = it },
                                label = { Text("DNI", color = Color(0xFF1A237E)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF1A237E),
                                    unfocusedBorderColor = Color(0xFF3949AB),
                                    textColor = Color(0xFF000000),
                                    cursorColor = Color(0xFF1A237E)
                                )
                            )
                            if (!prog2DniValido && prog2Dni.isNotBlank()) {
                                Text("DNI no válido para el progenitor 2", color = Color(0xFFD32F2F))
                            }
                            OutlinedTextField(
                                value = prog2Telefono,
                                onValueChange = { prog2Telefono = it },
                                label = { Text("Teléfono", color = Color(0xFF1A237E)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF1A237E),
                                    unfocusedBorderColor = Color(0xFF3949AB),
                                    textColor = Color(0xFF000000),
                                    cursorColor = Color(0xFF1A237E)
                                )
                            )
                            OutlinedTextField(
                                value = prog2Direccion,
                                onValueChange = { prog2Direccion = it },
                                label = { Text("Dirección", color = Color(0xFF1A237E)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF1A237E),
                                    unfocusedBorderColor = Color(0xFF3949AB),
                                    textColor = Color(0xFF000000),
                                    cursorColor = Color(0xFF1A237E)
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = prog2Consentimiento,
                                    onCheckedChange = { prog2Consentimiento = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF1A237E),
                                        uncheckedColor = Color(0xFF3949AB)
                                    )
                                )
                                Text("Progenitor 2 consiente la sesión", color = Color(0xFF000000))
                            }
                        }
                    }
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono", color = Color(0xFF1A237E)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1A237E),
                            unfocusedBorderColor = Color(0xFF3949AB),
                            textColor = Color(0xFF000000),
                            cursorColor = Color(0xFF1A237E)
                        )
                    )
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección", color = Color(0xFF1A237E)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1A237E),
                            unfocusedBorderColor = Color(0xFF3949AB),
                            textColor = Color(0xFF000000),
                            cursorColor = Color(0xFF1A237E)
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email (opcional)", color = Color(0xFF1A237E)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1A237E),
                            unfocusedBorderColor = Color(0xFF3949AB),
                            textColor = Color(0xFF000000),
                            cursorColor = Color(0xFF1A237E)
                        )
                    )
                    if (!emailValido && email.isNotBlank()) {
                        Text("Correo electrónico inválido", color = Color(0xFFD32F2F))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Programa de televisión:", style = MaterialTheme.typography.subtitle1, color = Color(0xFF1A237E))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedPrograma ?: "Selecciona un programa",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF1A237E),
                                unfocusedBorderColor = Color(0xFF3949AB),
                                textColor = Color(0xFF000000),
                                cursorColor = Color(0xFF1A237E)
                            ),
                            trailingIcon = {
                                IconButton(onClick = { dropdownExpanded = !dropdownExpanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF1A237E))
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            programas.forEach { programa ->
                                DropdownMenuItem(onClick = {
                                    selectedPrograma = programa.nombre
                                    dropdownExpanded = false
                                }) {
                                    Text(programa.nombre, color = Color(0xFF000000))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = lugar,
                        onValueChange = { lugar = it },
                        label = { Text("Lugar de grabación", color = Color(0xFF1A237E)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1A237E),
                            unfocusedBorderColor = Color(0xFF3949AB),
                            textColor = Color(0xFF000000),
                            cursorColor = Color(0xFF1A237E)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = quiereCederImagenes,
                            onCheckedChange = {
                                quiereCederImagenes = it
                                if (!it) {
                                    imagenUri = null
                                    descripcionImagen = ""
                                    imagenGuardada = false
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF1A237E),
                                uncheckedColor = Color(0xFF3949AB)
                            )
                        )
                        Text("Quiero ceder imágenes/fotografías", color = Color(0xFF000000))
                    }

                    if (quiereCederImagenes) {
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                imagePickerLauncher.launch("image/*")
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF1A237E)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF1A237E))
                        ) {
                            Text("Seleccionar imagen de la galería")
                        }

                        imagenUri?.let { uri ->
                            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Imagen seleccionada",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = descripcionImagen,
                                onValueChange = { descripcionImagen = it },
                                label = { Text("Descripción de la imagen", color = Color(0xFF1A237E)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF1A237E),
                                    unfocusedBorderColor = Color(0xFF3949AB),
                                    textColor = Color(0xFF000000),
                                    cursorColor = Color(0xFF1A237E)
                                )
                            )

                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = {
                                        imagenUri = null
                                        descripcionImagen = ""
                                        imagenGuardada = false
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF1A237E)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF1A237E))
                                ) {
                                    Text("Volver a subir imagen")
                                }

                                OutlinedButton(
                                    onClick = {
                                        if (imagenUri != null) {
                                            imagenGuardada = true
                                        }
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF1A237E)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFF1A237E))
                                ) {
                                    Text("Guardar imagen")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = aceptoCondiciones,
                            onCheckedChange = { aceptoCondiciones = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF1A237E),
                                uncheckedColor = Color(0xFF3949AB)
                            )
                        )

                        val context = LocalContext.current
                        val annotatedString = buildAnnotatedString {
                            append("Acepto los términos y condiciones. ")
                            pushStringAnnotation(tag = "TERMS", annotation = "go")
                            withStyle(style = SpanStyle(color = Color(0xFF1A237E), textDecoration = TextDecoration.Underline)) {
                                append("Consúltelos aquí")
                            }
                            pop()
                        }

                        ClickableText(
                            text = annotatedString,
                            onClick = { offset ->
                                annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                                    .firstOrNull()?.let {
                                        context.startActivity(android.content.Intent(context, TermsAndConditionsActivity::class.java))
                                    }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Firma digital del participante:", style = MaterialTheme.typography.subtitle1)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .border(1.dp, colors.primary, RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(8.dp)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { currentPath = listOf(it) },
                                    onDragEnd = {
                                        if (currentPath.isNotEmpty()) {
                                            paths.add(currentPath)
                                        }
                                        currentPath = emptyList()
                                    },
                                    onDrag = { change, _ ->
                                        val offset = change.position
                                        if (offset.x in 0f..800f && offset.y in 0f..400f) {
                                            currentPath = currentPath + offset
                                        }
                                    }
                                )
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            paths.forEach { path ->
                                for (i in 1 until path.size) {
                                    drawLine(
                                        color = Color(0xFF1976D2),
                                        start = path[i - 1],
                                        end = path[i],
                                        strokeWidth = 4f
                                    )
                                }
                            }
                            for (i in 1 until currentPath.size) {
                                drawLine(
                                    color = Color(0xFF1976D2),
                                    start = currentPath[i - 1],
                                    end = currentPath[i],
                                    strokeWidth = 4f
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { paths.clear(); firmaGuardada = false },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colors.primary
                            )
                        ) {
                            Text("Repetir firma")
                        }
                        OutlinedButton(
                            onClick = {
                                if (currentPath.isNotEmpty()) {
                                    paths.add(currentPath)
                                    currentPath = emptyList()
                                }
                                if (paths.isNotEmpty()) {
                                    firmaGuardada = true
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colors.primary
                            )
                        ) {
                            Text("Guardar firma")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showDialog = true },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar sesión de derechos")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { (context as? android.app.Activity)?.finish() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
                    ) {
                        Text("Volver", color = Color.Black)
                    }
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar registro") },
            text = { Text("¿Deseas guardar esta sesión de derechos?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val creador = "$nombre $apellidos"
                    val filename = "firma_${System.currentTimeMillis()}.png"
                    val imagePath = saveSignatureImage(context, paths, filename)
                    val edadCategoria = when {
                        esMayor -> "adulto"
                        esMenor -> "menor"
                        else -> "desconocido"
                    }
                    val imagenPathFinal = if (quiereCederImagenes && imagenUri != null) {
                        saveSelectedImageToInternalStorage(context, imagenUri!!)
                    } else null
                    viewModel.addProduction(
                        Production(
                            nombre = nombre,
                            apellidos = apellidos,
                            dni = dni,
                            telefono = telefono,
                            direccion = direccion,
                            email = email.takeIf { it.isNotBlank() },
                            lugarGrabacion = lugar.takeIf { it.isNotBlank() },
                            edadCategoria = edadCategoria,
                            descripcion = "Registro de derechos para $nombre $apellidos",
                            fechaFin = null,
                            usuarioCreador = "$nombre $apellidos",
                            created_by = "$nombre $apellidos",
                            programa = selectedPrograma ?: "Sin especificar",
                            firmaPath = imagePath,
                            imagenPath = imagenPathFinal,
                            descripcionImagen = descripcionImagen.takeIf { it.isNotBlank() },
                            progenitor1Nombre = prog1Nombre.takeIf { it.isNotBlank() },
                            progenitor1Apellidos = prog1Apellidos.takeIf { it.isNotBlank() },
                            progenitor1Dni = prog1Dni.takeIf { it.isNotBlank() },
                            progenitor1Telefono = prog1Telefono.takeIf { it.isNotBlank() },
                            progenitor1Direccion = prog1Direccion.takeIf { it.isNotBlank() },
                            progenitor1Consentimiento = prog1Consentimiento,
                            progenitor2Nombre = prog2Nombre.takeIf { it.isNotBlank() },
                            progenitor2Apellidos = prog2Apellidos.takeIf { it.isNotBlank() },
                            progenitor2Dni = prog2Dni.takeIf { it.isNotBlank() },
                            progenitor2Telefono = prog2Telefono.takeIf { it.isNotBlank() },
                            progenitor2Direccion = prog2Direccion.takeIf { it.isNotBlank() },
                            progenitor2Consentimiento = prog2Consentimiento,
                            soloUnProgenitor = soloUnProgenitor
                        )
                    )


                    onSaved()
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ✅ Guardar imagen de la firma
fun saveSignatureImage(context: Context, paths: List<List<Offset>>, filename: String): String {
    val bitmap = Bitmap.createBitmap(800, 400, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        strokeWidth = 5f
        isAntiAlias = true
    }
    paths.forEach { path ->
        for (i in 1 until path.size) {
            canvas.drawLine(path[i - 1].x, path[i - 1].y, path[i].x, path[i].y, paint)
        }
    }

    val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(dir, filename)
    FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }

    return file.absolutePath
}
fun saveSelectedImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val timestamp = System.currentTimeMillis()
        val filename = "foto_$timestamp.jpg"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// ✅ Validar letra del DNI
fun validarLetraDNI(dni: String): Boolean {
    val letras = "TRWAGMYFPDXBNJZSQVHLCKE"
    val dniUpper = dni.uppercase()

    if (!dniUpper.matches(Regex("^[0-9]{8}[A-Z]$"))) return false

    val numero = dniUpper.substring(0, 8).toIntOrNull() ?: return false
    val letra = dniUpper.last()
    val letraCorrecta = letras[numero % 23]

    return letra == letraCorrecta
}
