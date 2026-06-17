package com.gweather.data.repository

import com.gweather.data.local.dao.UserDao
import com.gweather.data.local.entity.UserEntity
import com.gweather.domain.AppError
import com.gweather.domain.AppException
import com.gweather.domain.repository.AuthRepository
import com.gweather.util.PasswordHasher
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Boolean {
        val user = userDao.getUserByEmail(email) ?: return false
        return PasswordHasher.verify(password, user.salt, user.passwordHash)
    }

    override suspend fun register(name: String, email: String, password: String) {
        if (userDao.getUserByEmail(email) != null) {
            throw AppException(AppError.EMAIL_ALREADY_EXISTS)
        }
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword(password, salt)
        userDao.insertUser(
            UserEntity(name = name, email = email, passwordHash = hash, salt = salt)
        )
    }
}
