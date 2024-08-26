package com.inc.eldartest.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.inc.eldartest.R
import com.inc.eldartest.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel.loginResult.observe(this, Observer { result ->
            result.onSuccess { user ->
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.onFailure { exception ->
                Toast.makeText(this, exception.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.registrationResult.observe(this, Observer { result ->
            result.onSuccess { user ->
                Toast.makeText(this, "User successfully registered!", Toast.LENGTH_SHORT).show()
            }.onFailure { exception ->
                Toast.makeText(this, exception.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val etEmail = findViewById<EditText>(R.id.etEmail)
            val etPassword = findViewById<EditText>(R.id.etPassword)

            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()
            ) {
                viewModel.login(email, password)
            }
        }

        findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_register_user, null)
            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(bottomSheetView)

            bottomSheetView.findViewById<Button>(R.id.btnCreateAccount).setOnClickListener {
                val email = bottomSheetView.findViewById<EditText>(R.id.etEmail).text.toString()
                val password =
                    bottomSheetView.findViewById<EditText>(R.id.etPassword).text.toString()
                val firstName =
                    bottomSheetView.findViewById<EditText>(R.id.etFirstName).text.toString()
                val lastName =
                    bottomSheetView.findViewById<EditText>(R.id.etLastName).text.toString()

                if (email.isNotEmpty() &&
                    password.isNotEmpty() &&
                    firstName.isNotEmpty() &&
                    lastName.isNotEmpty()
                ) {
                    viewModel.register(
                        email,
                        password,
                        firstName.replace(" ", ""),
                        lastName.replace(" ", "")
                    )
                    bottomSheetDialog.dismiss()
                }


            }
            bottomSheetDialog.show()
        }
    }
}
