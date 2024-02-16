package it.unipi.largescale.pixelindex.model;

import java.util.ArrayList;

public class Library {
    private ArrayList<Game> gameList;
    private Library(){
        gameList = new ArrayList<>();
    }

    public ArrayList<Game> getGameList() {
        return gameList;
    }

    public void setGameList(ArrayList<Game> gameList) {
        this.gameList = gameList;
    }
}
