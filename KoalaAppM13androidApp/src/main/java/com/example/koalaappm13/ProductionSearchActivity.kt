package com.example.koalaappm13

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.koalaappm13.database.*
import com.example.koalaappm13.ui.KoalaAppM13Theme

class ProductionSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as KoalaApp
        val sessionManager = SessionManager(this)
        val currentUsername = sessionManager.getCurrentUsername()

        setContent {
            KoalaAppM13Theme {
                val productionViewModel: ProductionViewModel = viewModel(
                    factory = ProductionViewModelFactory(app.productionRepository, currentUsername)
                )
                val programaViewModel: ProgramaTvViewModel = viewModel(
                    factory = ProgramaTvViewModelFactory(app.programaTvRepository)
                )
                ProductionSearchScreen(productionViewModel, programaViewModel) {
                    finish()
                }
            }
        }
    }
}

@Composable
fun DropdownMenuToggle(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Ordenar por") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF3A3A6A),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onSelect(option)
                    expanded = false
                }) {
                    Text(option)
                }
            }
        }
    }
}

@Composable
fun ProductionSearchScreen(
    viewModel: ProductionViewModel,
    programaViewModel: ProgramaTvViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var soloMenores by remember { mutableStateOf(false) }
    var soloAdultos by remember { mutableStateOf(false) }
    var selectedPrograma by remember { mutableStateOf<String?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var soloConImagen by remember { mutableStateOf(false) }
    var soloSinImagen by remember { mutableStateOf(false) }

    val programas by programaViewModel.allProgramas.collectAsState(initial = emptyList())

    val puedeBuscar = nombre.isNotBlank() || apellidos.isNotBlank() || dni.isNotBlank() ||
            telefono.isNotBlank() || selectedPrograma != null || soloMenores || soloAdultos ||
            soloConImagen || soloSinImagen

    var filtroTrigger by remember { mutableStateOf(0) }
    var mostrarTodoTrigger by remember { mutableStateOf(0) }
    var resultados by remember { mutableStateOf<List<Production>>(emptyList()) }

    val conImagen: Boolean? = when {
        soloConImagen -> true
        soloSinImagen -> false
        else -> null
    }

    LaunchedEffect(filtroTrigger) {
        viewModel.searchProductionsWithFilters(
            nombre = nombre.takeIf { it.isNotBlank() },
            apellidos = apellidos.takeIf { it.isNotBlank() },
            dni = dni.takeIf { it.isNotBlank() },
            telefono = telefono.takeIf { it.isNotBlank() },
            mayorDeEdad = when {
                soloMenores -> false
                soloAdultos -> true
                else -> null
            },
            programa = selectedPrograma,
            conImagen = conImagen
        ).collect {
            resultados = it
        }
    }

    LaunchedEffect(mostrarTodoTrigger) {
        viewModel.getAllProductions().collect {
            resultados = it
        }
    }

    var ordenSeleccionado by remember { mutableStateOf("Más nuevas") }

    val resultadosOrdenados = remember(resultados, ordenSeleccionado) {
        when (ordenSeleccionado) {
            "Orden alfabético" -> resultados.sortedBy { it.usuarioCreador }
            "Más antiguas" -> resultados.sortedBy { it.created_at }
            else -> resultados.sortedByDescending { it.created_at }
        }
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
                    
                    Text(
                        text = "Búsqueda de Sesiones",
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
                ) {
                    Text(
                        text = "Filtros de búsqueda",
                        style = MaterialTheme.typography.h6,
                        color = Color(0xFF3A3A6A),
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = { apellidos = it },
                        label = { Text("Apellidos") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dni,
                        onValueChange = { dni = it },
                        label = { Text("DNI") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF3A3A6A),
                            unfocusedBorderColor = Color(0xFFBDBDBD)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtros de edad
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
                            Text(
                                text = "Filtros de edad",
                                style = MaterialTheme.typography.subtitle1,
                                color = Color(0xFF3A3A6A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Primera opción
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = soloAdultos,
                                    onCheckedChange = {
                                        soloAdultos = it
                                        if (it) soloMenores = false
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF3A3A6A),
                                        uncheckedColor = Color(0xFF3A3A6A)
                                    )
                                )
                                Text(
                                    "Solo adultos",
                                    color = Color(0xFF3A3A6A),
                                    fontSize = 16.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Segunda opción
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = soloMenores,
                                    onCheckedChange = {
                                        soloMenores = it
                                        if (it) soloAdultos = false
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF3A3A6A),
                                        uncheckedColor = Color(0xFF3A3A6A)
                                    )
                                )
                                Text(
                                    "Solo menores de edad",
                                    color = Color(0xFF3A3A6A),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtros de imagen
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
                            Text(
                                text = "Filtros de imagen",
                                style = MaterialTheme.typography.subtitle1,
                                color = Color(0xFF3A3A6A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Primera opción
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = soloConImagen,
                                    onCheckedChange = {
                                        soloConImagen = it
                                        if (it) soloSinImagen = false
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF3A3A6A),
                                        uncheckedColor = Color(0xFF3A3A6A)
                                    )
                                )
                                Text(
                                    "Sesiones con imagen",
                                    color = Color(0xFF3A3A6A),
                                    fontSize = 16.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Segunda opción
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = soloSinImagen,
                                    onCheckedChange = {
                                        soloSinImagen = it
                                        if (it) soloConImagen = false
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF3A3A6A),
                                        uncheckedColor = Color(0xFF3A3A6A)
                                    )
                                )
                                Text(
                                    "Sesiones sin imagen",
                                    color = Color(0xFF3A3A6A),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de programa
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
                            Text(
                                text = "Programa",
                                style = MaterialTheme.typography.subtitle1,
                                color = Color(0xFF3A3A6A),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = selectedPrograma ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Seleccionar programa") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFF3A3A6A),
                                        unfocusedBorderColor = Color(0xFFBDBDBD)
                                    ),
                                    trailingIcon = {
                                        IconButton(onClick = { dropdownExpanded = !dropdownExpanded }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
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
                                            Text(programa.nombre)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ordenar por
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
                            Text(
                                text = "Ordenar resultados",
                                style = MaterialTheme.typography.subtitle1,
                                color = Color(0xFF3A3A6A),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DropdownMenuToggle(
                                options = listOf("Más nuevas", "Más antiguas", "Orden alfabético"),
                                selected = ordenSeleccionado,
                                onSelect = { ordenSeleccionado = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { filtroTrigger++ },
                            modifier = Modifier.weight(1f),
                            enabled = puedeBuscar,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF4A4A8A),
                                disabledBackgroundColor = Color(0xFFBDBDBD)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "Filtrar",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { mostrarTodoTrigger++ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF4A4A8A)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "Mostrar todo",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Resultados
                    if (resultadosOrdenados.isEmpty()) {
                        Text(
                            "No se encontraron resultados.",
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray
                        )
                    } else {
                        Text(
                            "Resultados encontrados: ${resultadosOrdenados.size}",
                            style = MaterialTheme.typography.subtitle1,
                            color = Color(0xFF3A3A6A),
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        resultadosOrdenados.forEach { production ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        val intent = Intent(context, ProductionDetailActivity::class.java)
                                        intent.putExtra("production_id", production.id)
                                        intent.putExtra("current_username", viewModel.currentUsername)
                                        context.startActivity(intent)
                                    },
                                elevation = 4.dp,
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = Color.White
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = production.usuarioCreador,
                                        style = MaterialTheme.typography.subtitle1,
                                        color = Color(0xFF3A3A6A),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = production.programa ?: "Sin programa",
                                        style = MaterialTheme.typography.body2,
                                        color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when (production.estadoSesion) {
                                            "válida" -> "✅ Validada"
                                            "no_válida" -> "❌ No válida"
                                            else -> "⚪ Sin validar"
                                        },
                                        style = MaterialTheme.typography.caption,
                                        color = when (production.estadoSesion) {
                                            "válida" -> Color(0xFF2E7D32)
                                            "no_válida" -> Color(0xFFC62828)
                                            else -> Color.Gray
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                            (context as? ComponentActivity)?.finish()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF4A4A8A)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Volver a Menú",
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
