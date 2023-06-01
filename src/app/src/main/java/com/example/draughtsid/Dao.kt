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
    @Query("SELECT id, user1.name, user1.surname, user2.name, user2.surname, date, result \n" +
            "FROM games \n" +
            "INNER JOIN users AS user1 ON user1.id = games.user1 \n" +
            "INNER JOIN users AS user2 ON user2.id = games.user2 \n")
    fun getGames(): List<ProfileGameReturn>
    @Query("SELECT game FROM games WHERE id = :id")
    fun getGame(id: Int)
}