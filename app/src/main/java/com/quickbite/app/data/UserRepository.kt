package com.quickbite.app.data

import com.quickbite.app.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }
}
