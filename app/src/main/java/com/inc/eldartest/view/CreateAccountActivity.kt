package com.inc.eldartest.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.inc.eldartest.R
import com.inc.eldartest.viewmodel.CreateAccountViewModel
import kotlinx.coroutines.launch

class CreateAccountActivity : AppCompatActivity() {

    private val createAccountViewModel: CreateAccountViewModel by viewModels()

    companion object {
        private const val TAG = "CreateAccountActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)

        btnCreateAccount.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()

            if (createAccountViewModel.isInputValid(username, password, firstName, lastName)) {
                try {
                    createAccountViewModel.createUser(username, password, firstName, lastName)
                    Toast.makeText(
                        this@CreateAccountActivity,
                        "Account created successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                    Toast.makeText(
                        this@CreateAccountActivity,
                        "Error creating account: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(
                    this@CreateAccountActivity,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}