package it.unipi.largescale.pixelindex.dto;

import it.unipi.largescale.pixelindex.model.Game;

public class GamePreviewDTO {

    private String id;
    private String name;
    private int releaseYear;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String toString()
    {
        String result = "";
        result += ("Name: " + name);
        if(releaseYear != 0)
            result += (" Release Year: " + releaseYear);
        return result;
    }
}
