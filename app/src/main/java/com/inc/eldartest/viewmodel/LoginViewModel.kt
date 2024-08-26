package com.inc.eldartest.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.inc.eldartest.data.AuthRepository
import com.inc.eldartest.data.UserRepository

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()

    val loginResult: MutableLiveData<Result<FirebaseUser>> = MutableLiveData()
    val registrationResult: MutableLiveData<Result<FirebaseUser>> = MutableLiveData()
    private val userSaveResult: MutableLiveData<Boolean> = MutableLiveData()

    fun login(email: String, password: String) {
        authRepository.login(email, password).observeForever {
            loginResult.value = it
        }
    }

    fun register(email: String, password: String, firstName: String, lastName: String) {
        authRepository.register(email, password).observeForever {
            if (it.isSuccess) {
                val user: FirebaseUser = it.getOrNull()!!
                saveUserDetails(user.uid, user.email!!,firstName, lastName)
            }
            registrationResult.value = it
        }

    }

    private fun saveUserDetails(userId: String, email: String, firstName: String, lastName: String) {
        userRepository.saveUserDetails(userId, email, firstName, lastName) { success ->
            userSaveResult.value = success
        }
    }
}
