package com.inc.eldartest.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val balance: Double = 0.0
)
