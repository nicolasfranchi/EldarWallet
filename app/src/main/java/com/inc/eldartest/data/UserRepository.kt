package com.inc.eldartest.data

import com.google.firebase.firestore.FirebaseFirestore
import com.inc.eldartest.model.User

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    fun saveUserDetails(
        userId: String,
        email: String,
        firstName: String,
        lastName: String,
        callback: (Boolean) -> Unit
    ) {
        val user = mapOf(
            "userId" to userId,
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "balance" to 0.0  // Firestore utiliza Double para números decimales
        )

        // Guardar el documento en Firestore
        usersCollection.document(userId).set(user)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getUserDetails(userId: String, callback: (User?) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val email = document.getString("email") ?: ""
                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    val balance = document.getDouble("balance") ?: 0.0

                    val user = User(userId, email, firstName, lastName, balance)
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                // Manejar el error si es necesario
                callback(null)
            }
    }
}
