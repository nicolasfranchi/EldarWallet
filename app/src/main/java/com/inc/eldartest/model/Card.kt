package com.inc.eldartest.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "credit_cards"
)
data class Card(
    @PrimaryKey(autoGenerate = true) val cardId: Int = 0,
    val ownerId: String,
    val cardIssuer: String,
    var cardNumber: String,
    val expiryDate: String,
    var cvv: String
): Serializable
