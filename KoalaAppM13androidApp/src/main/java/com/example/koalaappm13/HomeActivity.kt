package com.example.koalaappm13

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.koalaappm13.database.AppDatabase
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var btnChangePassword: Button
    private lateinit var btnSearchProductions: Button
    private lateinit var btnLogout: Button
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager(this)

        btnChangePassword = findViewById(R.id.btnChangePassword)
        btnSearchProductions = findViewById(R.id.btnSearchProductions)
        btnLogout = findViewById(R.id.btnLogout)

        btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        // 🔍 Ocultamos el botón de grabaciones hasta comprobar el rol
        btnSearchProductions.visibility = Button.GONE

        val currentUsername = sessionManager.getCurrentUsername()

        // Actualizar el texto de bienvenida
        findViewById<TextView>(R.id.tvCurrentUser).text = "¡Bienvenido, $currentUsername!"
        
        // Actualizar el usuario en la tarjeta
        findViewById<TextView>(R.id.tvCurrentUser).text = currentUsername

        // ✅ Consultamos el rol desde la base de datos
        val userDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).userDao()
        lifecycleScope.launch {
            val user = userDao.getUserByUsername(currentUsername)
            if (user?.role == "admin" || user?.role == "editor") {
                btnSearchProductions.visibility = Button.VISIBLE
                btnSearchProductions.setOnClickListener {
                    val intent = Intent(this@HomeActivity, ProductionSearchActivity::class.java)
                    intent.putExtra("current_username", currentUsername)
                    startActivity(intent)
                }
            }
        }
    }

    private fun showLogoutConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cerrar Sesión")
        builder.setMessage("¿Estás seguro de que quieres cerrar sesión?")
        builder.setPositiveButton("Sí") { _: DialogInterface, _: Int ->
            sessionManager.logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.show()
    }
}
