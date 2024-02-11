package it.unipi.largescale.pixelindex.dto;

public class TrendingGamesDTO {
    private String gameName;
    private Integer count;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String toString() {
        return "Game: " + gameName + "\t-\tCount: " + count;
    }
}
