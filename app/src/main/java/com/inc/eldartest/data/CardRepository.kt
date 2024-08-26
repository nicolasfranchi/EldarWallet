package com.inc.eldartest.data

import android.content.Context
import com.inc.eldartest.model.Card
import com.inc.eldartest.util.CryptoUtils

class CardRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val creditCardDao = db.creditCardDao()

    suspend fun saveCard(card: Card) {
        card.cardNumber = CryptoUtils.encrypt(card.cardNumber)
        card.cvv = CryptoUtils.encrypt(card.cvv)
        creditCardDao.insertCard(card)
    }

    suspend fun getCreditCard(id: String): List<Card> {
        val encryptedCards = creditCardDao.getCardsByUserId(id)
        return encryptedCards.map {
            it.cardNumber = CryptoUtils.decrypt(it.cardNumber)
            it.cvv = CryptoUtils.decrypt(it.cvv)
            it
        }
    }

    suspend fun deleteCard(cardId: Int) {
        creditCardDao.deleteCardById(cardId)
    }
}
