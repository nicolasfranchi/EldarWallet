package com.inc.eldartest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.inc.eldartest.model.Card

@Dao
interface CardDao {
    @Insert
    fun insertCard(card: Card)

    @Query("SELECT * FROM credit_cards WHERE ownerId = :userId")
    fun getCardsByUserId(userId: String): List<Card>

    @Query("DELETE FROM credit_cards WHERE cardId = :cardId")
    suspend fun deleteCardById(cardId: Int)
}