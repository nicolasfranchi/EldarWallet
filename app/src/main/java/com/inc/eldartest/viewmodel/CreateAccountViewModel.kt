package com.inc.eldartest.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import com.inc.eldartest.model.User
import com.inc.eldartest.data.UserRepository

class CreateAccountViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)

    fun isInputValid(username: String, password: String, firstName: String, lastName: String): Boolean {
        return username.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()
    }

    fun createUser(username: String, password: String, firstName: String, lastName: String) {
        // Utiliza viewModelScope para gestionar la coroutine con el ciclo de vida del ViewModel
        viewModelScope.launch(Dispatchers.IO) {  // Lanza la coroutine en el contexto de I/O
            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            val user = User(0, username, hashedPassword, firstName, lastName)
            userRepository.saveUser(user)
        }
    }
}

