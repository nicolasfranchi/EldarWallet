package com.inc.eldartest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.inc.eldartest.model.User

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): User?
}
