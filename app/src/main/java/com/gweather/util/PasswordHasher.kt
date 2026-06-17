package com.gweather.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordHasher {

    fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun hashPassword(password: String, salt: String): String {
        val input = (password + salt).toByteArray(Charsets.UTF_8)
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input).joinToString("") { "%02x".format(it) }
    }

    fun verify(password: String, salt: String, storedHash: String): Boolean {
        return hashPassword(password, salt) == storedHash
    }
}
