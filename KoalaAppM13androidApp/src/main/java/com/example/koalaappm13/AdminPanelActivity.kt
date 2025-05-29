package com.example.koalaappm13

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.koalaappm13.database.*
import com.example.koalaappm13.ui.KoalaAppM13Theme
import com.example.koalaappm13.ui.ProgramaAdminScreen
import androidx.lifecycle.lifecycleScope

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddUser: Button
    private lateinit var btnLogout: Button
    private lateinit var btnGestionProgramas: Button
    private lateinit var btnGestionarSesiones: Button
    private lateinit var composeView: ComposeView
    private lateinit var userAdapter: UserAdapter

    private lateinit var userViewModel: UserViewModel
    private lateinit var programaTvViewModel: ProgramaTvViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        recyclerView = findViewById(R.id.recyclerViewUsers)
        btnAddUser = findViewById(R.id.btnAddUser)
        btnLogout = findViewById(R.id.btnLogout)
        btnGestionProgramas = findViewById(R.id.btnGestionProgramas)
        btnGestionarSesiones = findViewById(R.id.btnGestionarSesiones)
        composeView = findViewById(R.id.composeViewProgramas)

        userViewModel = ViewModelProvider(
            this, UserViewModelFactory((application as KoalaApp).userRepository)
        )[UserViewModel::class.java]

        val programaDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).programaTvDao()
        val programaRepo = ProgramaTvRepository(programaDao)
        programaTvViewModel = ViewModelProvider(
            this,
            ProgramaTvViewModelFactory(programaRepo)
        )[ProgramaTvViewModel::class.java]

        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(
            emptyList(),
            onDeleteClick = { user -> confirmDeleteUser(user) },
            onEditClick = { user -> editUser(user) }
        )
        recyclerView.adapter = userAdapter

        loadUsers()

        btnAddUser.setOnClickListener {
            addUserLauncher.launch(Intent(this, AddUserActivity::class.java))
        }

        btnLogout.setOnClickListener {
            logout()
        }

        btnGestionProgramas.setOnClickListener {
            composeView.setContent {
                KoalaAppM13Theme {
                    Surface {
                        ProgramaAdminScreen(
                            viewModel = programaTvViewModel,
                            onBack = {
                                composeView.setContent { }
                            }
                        )
                    }
                }
            }
        }

        btnGestionarSesiones.setOnClickListener {
            val intent = Intent(this, ProductionSearchActivity::class.java)
            startActivity(intent)
        }

        // Botón Home
        findViewById<Button>(R.id.btnGoToHome)?.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadUsers() {
        userViewModel.getAllUsers { users: List<User> ->
            runOnUiThread {
                userAdapter.updateUsers(users)
            }
        }
    }

    private fun confirmDeleteUser(user: User) {
        if (user.username == "admin") {
            Toast.makeText(this, "No puedes eliminar al usuario administrador", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de que deseas eliminar a ${user.username}? Esta acción no se puede deshacer.")
            .setPositiveButton("Sí") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteUser(user: User) {
        userViewModel.deleteUser(user) {
            runOnUiThread {
                Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                loadUsers()
            }
        }
    }

    private fun editUser(user: User) {
        if (user.username == "admin") {
            Toast.makeText(this, "No puedes editar el usuario administrador", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("user_id", user.id)
        editUserLauncher.launch(intent)
    }

    private fun logout() {
        val sessionManager = SessionManager(this)
        sessionManager.logout()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private val addUserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadUsers()
        }
    }

    private val editUserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadUsers()
        }
    }
}
