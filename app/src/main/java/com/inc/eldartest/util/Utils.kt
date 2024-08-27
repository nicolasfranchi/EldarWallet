package com.inc.eldartest.util

object Utils {
    fun formatCardNumber(cardNumber: String): String {
        return if (cardNumber.length == 16) {
            cardNumber.chunked(4).joinToString(" ")
        } else if (cardNumber.length == 17) {
            cardNumber.substring(0, 12).chunked(4).joinToString(" ") + " " + cardNumber.substring(12)
        } else {
            cardNumber
        }
    }
}