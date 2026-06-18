package com.gweather.data.repository

import com.gweather.data.local.dao.UserDao
import com.gweather.data.local.entity.UserEntity
import com.gweather.domain.AppError
import com.gweather.domain.AppException
import com.gweather.util.PasswordHasher
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    private lateinit var userDao: UserDao
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        userDao = mockk()
        repository = AuthRepositoryImpl(userDao)
    }

    @Test
    fun login_withCorrectPassword_returnsTrue() = runTest {
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword("password123", salt)
        val user = UserEntity(name = "Alice", email = "alice@test.com", passwordHash = hash, salt = salt)
        coEvery { userDao.getUserByEmail("alice@test.com") } returns user

        assertTrue(repository.login("alice@test.com", "password123"))
    }

    @Test
    fun login_withWrongPassword_returnsFalse() = runTest {
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword("password123", salt)
        val user = UserEntity(name = "Alice", email = "alice@test.com", passwordHash = hash, salt = salt)
        coEvery { userDao.getUserByEmail("alice@test.com") } returns user

        assertFalse(repository.login("alice@test.com", "wrongpassword"))
    }

    @Test
    fun login_withUnknownEmail_returnsFalse() = runTest {
        coEvery { userDao.getUserByEmail("nobody@test.com") } returns null

        assertFalse(repository.login("nobody@test.com", "password123"))
    }

    @Test
    fun register_withNewEmail_insertsUserIntoDao() = runTest {
        coEvery { userDao.getUserByEmail("new@test.com") } returns null
        coEvery { userDao.insertUser(any()) } just Runs

        repository.register("Bob", "new@test.com", "password123")

        coVerify { userDao.insertUser(any()) }
    }

    @Test
    fun register_withNewEmail_hashesPasswordBeforeStoring() = runTest {
        var capturedUser: UserEntity? = null
        coEvery { userDao.getUserByEmail("new@test.com") } returns null
        coEvery { userDao.insertUser(any()) } answers { capturedUser = firstArg() }

        repository.register("Bob", "new@test.com", "password123")

        assertNotNull(capturedUser)
        assertNotEquals("password123", capturedUser!!.passwordHash)
        assertTrue(PasswordHasher.verify("password123", capturedUser!!.salt, capturedUser!!.passwordHash))
    }

    @Test
    fun register_withExistingEmail_throwsEmailAlreadyExists() = runTest {
        val existing = UserEntity(name = "Alice", email = "alice@test.com", passwordHash = "hash", salt = "salt")
        coEvery { userDao.getUserByEmail("alice@test.com") } returns existing

        val exception = runCatching {
            repository.register("Alice2", "alice@test.com", "password123")
        }.exceptionOrNull()

        assertNotNull(exception)
        assertTrue(exception is AppException)
        assertEquals(AppError.EMAIL_ALREADY_EXISTS, (exception as AppException).error)
    }
}
