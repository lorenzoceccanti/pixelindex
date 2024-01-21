package it.unipi.largescale.pixelindex.model;

import java.util.ArrayList;

public class Whishlist {
    private String userId;
    private ArrayList<Game> gameList;

    public Whishlist()
    {
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
