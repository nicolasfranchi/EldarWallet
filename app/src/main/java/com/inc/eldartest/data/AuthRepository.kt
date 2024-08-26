package com.inc.eldartest.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String): LiveData<Result<FirebaseUser>> {
        val result = MutableLiveData<Result<FirebaseUser>>()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = Result.success(auth.currentUser!!)
                } else {
                    result.value =
                        Result.failure(task.exception ?: Exception("Authentication failed"))
                }
            }

        return result
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun register(email: String, password: String): LiveData<Result<FirebaseUser>> {
        val result = MutableLiveData<Result<FirebaseUser>>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = Result.success(auth.currentUser!!)
                } else {
                    result.value =
                        Result.failure(task.exception ?: Exception("Registration failed"))
                }
            }

        return result
    }

    fun logout() {
        auth.signOut()
    }
}
