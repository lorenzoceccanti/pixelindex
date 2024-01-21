package it.unipi.largescale.pixelindex.model;

import java.util.ArrayList;

public class Library {
    private String userId;
    private ArrayList<Game> gameList;
    private Library(){
        gameList = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Game> getGameList() {
        return gameList;
    }

    public void setGameList(ArrayList<Game> gameList) {
        this.gameList = gameList;
    }
}
