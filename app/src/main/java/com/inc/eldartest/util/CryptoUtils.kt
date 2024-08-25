package com.inc.eldartest.util

import org.cryptonode.jncryptor.AES256JNCryptor
import org.cryptonode.jncryptor.CryptorException
import java.util.Base64

object CryptoUtils {

    private val password = "una_contraseña_segura"

    fun encrypt(data: String): String {
        val cryptor = AES256JNCryptor()
        val encryptedBytes = try {
            cryptor.encryptData(data.toByteArray(), password.toCharArray())
        } catch (e: CryptorException) {
            throw RuntimeException("Error encrypting data", e)
        }
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(encryptedData: String): String {
        val cryptor = AES256JNCryptor()
        val bytes = Base64.getDecoder().decode(encryptedData)
        val decryptedBytes = try {
            cryptor.decryptData(bytes, password.toCharArray())
        } catch (e: CryptorException) {
            throw RuntimeException("Error decrypting data", e)
        }
        return String(decryptedBytes)
    }
}
