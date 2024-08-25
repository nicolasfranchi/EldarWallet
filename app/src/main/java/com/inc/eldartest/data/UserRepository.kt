package com.inc.eldartest.data

import android.content.Context
import com.inc.eldartest.model.CreditCard
import com.inc.eldartest.model.User
import com.inc.eldartest.util.CryptoUtils

class UserRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val creditCardDao = db.creditCardDao()

    suspend fun saveUser(user: User) {
        val userId = userDao.insertUser(user).toInt()
        if (userId > 0) {
            val defaultCards = listOf(
                CreditCard(ownerId = userId, cardIssuer = "Mastercard", cardNumber = "5031 7557 3453 0604", expiryDate = "11/25", cvv = "123"),
                CreditCard(ownerId = userId, cardIssuer = "Visa", cardNumber = "4509 9535 6623 3704", expiryDate = "11/25", cvv = "123"),
                CreditCard(ownerId = userId, cardIssuer = "American Express", cardNumber = "3711 803032 57522", expiryDate = "11/25", cvv = "1234")
            )
            defaultCards.forEach { card ->
                card.cardNumber = CryptoUtils.encrypt(card.cardNumber)
                card.cvv = CryptoUtils.encrypt(card.cvv)
                creditCardDao.insertCard(card)
            }
        }
    }

    suspend fun getUser(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getUser(id: Int): User? {
        return userDao.getUserById(id)
    }
}
