package com.inc.eldartest.viewmodel

import android.app.Application
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

    private val cardRepository = CardRepository(application)
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    val cards = MutableLiveData<List<Card>>()


    private val _userDetails = MutableLiveData<User?>()
    val userDetails: LiveData<User?> get() = _userDetails

    fun deposit(userId: String, amount: Double) {
        userRepository.deposit(userId, amount) { user ->
            _userDetails.value = user
        }
    }

    fun send(userId: String, email: String, amount: Double) {
        userRepository.send(userId, email, amount) { user ->
            _userDetails.value = user
        }
    }

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

    fun addCard(card: Card) {
        viewModelScope.launch(Dispatchers.IO) {
            cardRepository.saveCard(card)
            loadCreditCards(card.ownerId)
        }
    }

    fun formatBalance(amount: Double): String {
        return String.format("$%,.2f", amount)
    }

}
