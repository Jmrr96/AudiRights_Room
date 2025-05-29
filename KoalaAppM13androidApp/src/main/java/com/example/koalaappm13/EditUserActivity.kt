package com.example.koalaappm13

import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.koalaappm13.database.User
import com.example.koalaappm13.database.UserViewModel
import com.example.koalaappm13.database.UserViewModelFactory

class EditUserActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etDni: EditText
    private lateinit var btnUpdateUser: Button
    private lateinit var btnBackToAdmin: Button
    private lateinit var rbEditor: RadioButton
    private lateinit var rbAdmin: RadioButton
    private lateinit var rgEditRole: RadioGroup

    private lateinit var userViewModel: UserViewModel
    private var userId: Int = -1
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etDni = findViewById(R.id.etDni)
        btnUpdateUser = findViewById(R.id.btnUpdateUser)
        btnBackToAdmin = findViewById(R.id.btnBackToAdmin)

        rgEditRole = findViewById(R.id.rgEditRole)
        rbEditor = findViewById(R.id.rbEditor)
        rbAdmin = findViewById(R.id.rbAdmin)

        userViewModel = ViewModelProvider(
            this, UserViewModelFactory((application as KoalaApp).userRepository)
        )[UserViewModel::class.java]

        userId = intent.getIntExtra("user_id", -1)

        if (userId != -1) {
            userViewModel.getUserById(userId) { user ->
                runOnUiThread {
                    if (user != null) {
                        currentUser = user
                        etUsername.setText(user.username)
                        etEmail.setText(user.email)
                        etDni.setText(user.dni)

                        // Establecer el rol seleccionado en el radio group
                        when (user.role.lowercase()) {
                            "admin" -> rbAdmin.isChecked = true
                            else -> rbEditor.isChecked = true
                        }
                    } else {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }

        btnUpdateUser.setOnClickListener {
            currentUser?.let { user ->
                val selectedRole = when (rgEditRole.checkedRadioButtonId) {
                    R.id.rbAdmin -> "admin"
                    else -> "editor"
                }

                val updatedUser = user.copy(
                    username = etUsername.text.toString().trim(),
                    email = etEmail.text.toString().trim(),
                    dni = etDni.text.toString().trim(),
                    role = selectedRole
                )

                userViewModel.updateUser(updatedUser) {
                    Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } ?: run {
                Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
            }
        }

        btnBackToAdmin.setOnClickListener {
            finish()
        }
    }
}
