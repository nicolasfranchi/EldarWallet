package com.inc.eldartest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.inc.eldartest.model.CreditCard

@Dao
interface CreditCardDao {
    @Insert
    fun insertCard(card: CreditCard)

    @Query("SELECT * FROM credit_cards WHERE ownerId = :userId")
    fun getCardsByUserId(userId: Int): List<CreditCard>

    @Query("DELETE FROM credit_cards WHERE cardId = :cardId")
    suspend fun deleteCardById(cardId: Int)
}