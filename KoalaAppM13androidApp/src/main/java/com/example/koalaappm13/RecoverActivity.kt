package com.example.koalaappm13

import com.example.koalaappm13.database.UserViewModel
import com.example.koalaappm13.database.UserViewModelFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class RecoverActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etDni: EditText
    private lateinit var btnRecover: Button
    private lateinit var btnBack: Button
    private lateinit var tvResult: TextView

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as KoalaApp).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover)

        etEmail = findViewById(R.id.etEmail)
        etDni = findViewById(R.id.etDni)
        btnRecover = findViewById(R.id.btnRecover)
        btnBack = findViewById(R.id.btnBack)
        tvResult = findViewById(R.id.tvResult)

        btnRecover.setOnClickListener {
            val email = etEmail.text.toString()
            val dni = etDni.text.toString()

            if (email.isNotEmpty() && dni.isNotEmpty()) {
                userViewModel.findUserByEmailAndDni(email, dni) { user ->
                    runOnUiThread {
                        if (user != null) {
                            tvResult.text = "Tu usuario es: ${user.username}"
                        } else {
                            tvResult.text = "No se encontró ninguna cuenta con esos datos"
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
