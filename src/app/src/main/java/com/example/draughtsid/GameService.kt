package com.example.draughtsid

class GameService(games: MutableList<ProfileGame>){
    private var profileGames = mutableListOf<ProfileGame>()

    init {
        profileGames = games
    }

}