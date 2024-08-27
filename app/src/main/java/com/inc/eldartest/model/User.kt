package com.inc.eldartest.model

data class User(
    val userId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    var balance: Double
)
