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
    var result: String
        )