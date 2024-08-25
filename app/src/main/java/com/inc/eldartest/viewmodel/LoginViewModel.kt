package com.inc.eldartest.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import com.inc.eldartest.model.User
import com.inc.eldartest.data.UserRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)
    val loginResult = MutableLiveData<User?>()  // Cambio para pasar el usuario cuando el inicio es exitoso

    fun loginUser(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepository.getUser(username)
            if (user != null && BCrypt.checkpw(password, user.password)) {
                loginResult.postValue(user)
            } else {
                loginResult.postValue(null)
            }
        }
    }

    private fun checkPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
}
