package com.inc.eldartest.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "credit_cards",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CreditCard(
    @PrimaryKey(autoGenerate = true) val cardId: Int = 0,
    val ownerId: Int,
    val cardIssuer: String,
    var cardNumber: String,
    val expiryDate: String,
    var cvv: String
)
