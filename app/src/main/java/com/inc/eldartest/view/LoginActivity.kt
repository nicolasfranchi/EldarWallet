package com.inc.eldartest.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.inc.eldartest.R
import com.inc.eldartest.model.User
import com.inc.eldartest.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            loginViewModel.loginUser(username, password)
        }

        loginViewModel.loginResult.observe(this, Observer { user ->
            if (user != null) {
                saveUserData(user)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        })

        btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    private fun saveUserData(user: User) {
        val editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
        editor.putString("username", user.username)
        editor.putString("first_name", user.firstName)
        editor.putString("last_name", user.lastName)
        editor.putFloat("balance", user.balance.toFloat())
        editor.putInt("userId", user.userId)
        editor.apply()
    }
}
