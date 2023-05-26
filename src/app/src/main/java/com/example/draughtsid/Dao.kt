package com.example.draughtsid

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Insert
    fun insertUser(user: User)
    @Insert
    fun insertGame(game: Game)
    @Query("SELECT id FROM users WHERE name = :userName AND surname = :userSurname")
    fun getUser(userName: String, userSurname: String): List<Int>
}