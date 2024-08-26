package com.inc.eldartest.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.inc.eldartest.data.AuthRepository
import com.inc.eldartest.model.Card
import com.inc.eldartest.data.CardRepository
import com.inc.eldartest.data.UserRepository
import com.inc.eldartest.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val cardRepository = CardRepository(application)
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    val cards = MutableLiveData<List<Card>>()


    private val _userDetails = MutableLiveData<User?>()
    val userDetails: LiveData<User?> get() = _userDetails

    fun getUserDetails(userId: String) {
        userRepository.getUserDetails(userId) { details ->
            _userDetails.value = details
        }
    }

    fun logOut() {
        authRepository.logout()
    }

    fun getCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }

    fun loadCreditCards(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val cards = cardRepository.getCreditCard(userId)
            this@MainViewModel.cards.postValue(cards)
        }
    }

    fun deleteCreditCard(cardId: Int, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            cardRepository.deleteCard(cardId)
            loadCreditCards(userId)
        }
    }

    fun verifyUserFirstName(firstName: String): Boolean {
        val savedFirstName =
            getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("first_name", "")
        return firstName == savedFirstName
    }

    fun verifyUserLastName(lastName: String): Boolean {
        val savedLastName =
            getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("last_name", "")
        return lastName == savedLastName
    }

    fun addCard(card: Card) {
        viewModelScope.launch(Dispatchers.IO) {
            cardRepository.saveCard(card)
            loadCreditCards(card.ownerId)
        }
    }
}
