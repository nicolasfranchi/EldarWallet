package com.inc.eldartest.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.inc.eldartest.model.CreditCard
import com.inc.eldartest.data.CreditCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val creditCardRepository = CreditCardRepository(application)
    val creditCards = MutableLiveData<List<CreditCard>>()

    fun loadCreditCards(userId: Int) {
        Log.e("MAINVIEWMODEL", "CARGANDO TARJETAS")
        viewModelScope.launch(Dispatchers.IO) {
            val cards = creditCardRepository.getCreditCard(userId)
            Log.i(TAG, cards.toString())
            creditCards.postValue(cards)
        }
    }

    fun deleteCreditCard(cardId: Int, userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            creditCardRepository.deleteCard(cardId)
            loadCreditCards(userId)
        }
    }

    fun verifyUserFirstName(firstName: String): Boolean {
        val savedFirstName = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getString("first_name", "")
        return firstName == savedFirstName
    }

    fun verifyUserLastName(lastName: String): Boolean {
        val savedLastName = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getString("last_name", "")
        return  lastName == savedLastName
    }

    fun addCard(card: CreditCard) {
        viewModelScope.launch(Dispatchers.IO) {
            creditCardRepository.saveCard(card)
            loadCreditCards(card.ownerId)  // Recargar las tarjetas para actualizar la vista.
        }
    }
}
