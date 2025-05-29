package com.example.koalaappm13

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.koalaappm13.database.User
import com.example.koalaappm13.database.UserViewModel
import com.example.koalaappm13.database.UserViewModelFactory

class AddUserActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etEmail: EditText
    private lateinit var etDni: EditText
    private lateinit var btnAddUser: Button
    private lateinit var btnBackToAdmin: Button
    private lateinit var rgRole: RadioGroup
    private lateinit var rbEditor: RadioButton
    private lateinit var rbAdmin: RadioButton

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etEmail = findViewById(R.id.etEmail)
        etDni = findViewById(R.id.etDni)
        btnAddUser = findViewById(R.id.btnAddUser)
        btnBackToAdmin = findViewById(R.id.btnBackToAdmin)
        rgRole = findViewById(R.id.rgRole)
        rbEditor = findViewById(R.id.rbEditor)
        rbAdmin = findViewById(R.id.rbAdmin)

        userViewModel = ViewModelProvider(
            this, UserViewModelFactory((application as KoalaApp).userRepository)
        )[UserViewModel::class.java]

        btnAddUser.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val dni = etDni.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || dni.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val role = if (rbAdmin.isChecked) "admin" else "editor"

            val newUser = User.createUser(
                username = username,
                password = password,
                email = email,
                dni = dni,
                role = role
            )

            userViewModel.insertUser(newUser)
            Toast.makeText(this, "Usuario agregado correctamente", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }

        btnBackToAdmin.setOnClickListener {
            finish()
        }
    }
}
