package com.example.draughtsid

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity (tableName = "users")
data class User (
    @PrimaryKey (autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "surname")
    var surname: String
        )

@Entity (tableName = "games")
data class Game (
    @PrimaryKey (autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "user1")
    var user1: Int,
    @ColumnInfo(name = "user2")
    var user2: Int,
    @ColumnInfo(name = "event")
    var event: String,
    @ColumnInfo(name = "round")
    var round: String,
    @ColumnInfo(name = "result")
    var result: String,
    @ColumnInfo(name = "game")
    var game: String
        )

data class ProfileGame(
    val id: Int,
    val users: String,
    var date: String,
    var result: String
)
/*
@Query("SELECT id, user1.name, user1.surname, user2.name, user2.surname, date, result \n" +
            "FROM games \n" +
            "INNER JOIN users AS user1 ON user1.id = games.user1 \n" +
            "INNER JOIN users AS user2 ON user2.id = games.user2 \n")
 */
data class ProfileGameReturn(
    val id: Int,
    val user1Name: String,
    val user1Surname: String,
    val user2Name: String,
    val user2Surname: String,
    val date: String,
    val result: String
)