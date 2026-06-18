package com.gweather.util

import org.junit.Assert.*
import org.junit.Test
import java.util.Base64

class PasswordHasherTest {

    @Test
    fun generateSalt_returnsBase64Of16Bytes() {
        val salt = PasswordHasher.generateSalt()
        val decoded = Base64.getDecoder().decode(salt)
        assertEquals(16, decoded.size)
    }

    @Test
    fun generateSalt_returnsDifferentValuesEachCall() {
        val salt1 = PasswordHasher.generateSalt()
        val salt2 = PasswordHasher.generateSalt()
        assertNotEquals(salt1, salt2)
    }

    @Test
    fun hashPassword_returns64CharHexString() {
        val hash = PasswordHasher.hashPassword("password", "somesalt")
        assertEquals(64, hash.length)
        assertTrue(hash.all { it.isDigit() || it in 'a'..'f' })
    }

    @Test
    fun hashPassword_isSameForSameInput() {
        val hash1 = PasswordHasher.hashPassword("mypassword", "mysalt")
        val hash2 = PasswordHasher.hashPassword("mypassword", "mysalt")
        assertEquals(hash1, hash2)
    }

    @Test
    fun hashPassword_isDifferentForDifferentSalt() {
        val hash1 = PasswordHasher.hashPassword("mypassword", "salt1")
        val hash2 = PasswordHasher.hashPassword("mypassword", "salt2")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun hashPassword_isDifferentForDifferentPassword() {
        val hash1 = PasswordHasher.hashPassword("password1", "sameSalt")
        val hash2 = PasswordHasher.hashPassword("password2", "sameSalt")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun verify_returnsTrueForCorrectPassword() {
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword("secret", salt)
        assertTrue(PasswordHasher.verify("secret", salt, hash))
    }

    @Test
    fun verify_returnsFalseForWrongPassword() {
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword("secret", salt)
        assertFalse(PasswordHasher.verify("wrong", salt, hash))
    }

    @Test
    fun verify_returnsFalseForTamperedHash() {
        val salt = PasswordHasher.generateSalt()
        assertFalse(PasswordHasher.verify("password", salt, "0000000000000000000000000000000000000000000000000000000000000000"))
    }
}
