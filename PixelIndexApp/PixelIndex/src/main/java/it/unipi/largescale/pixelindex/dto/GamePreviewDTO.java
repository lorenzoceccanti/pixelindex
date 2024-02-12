package it.unipi.largescale.pixelindex.dto;

import it.unipi.largescale.pixelindex.utils.AnsiColor;

public class GamePreviewDTO {

    private String id;
    private String name;
    private Integer releaseYear;
    private String pegiRating;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setPegiRating(String pegiRating) {
        this.pegiRating = pegiRating;
    }

    public String getPegiRating() {
        return pegiRating;
    }

    public String toString() {
        String result = "";
        result += (AnsiColor.ANSI_BLUE + "Name: " + name + AnsiColor.ANSI_RESET);
        if (releaseYear != null)
            result += (" " + AnsiColor.ANSI_RED + "Release Year: " + releaseYear + AnsiColor.ANSI_RESET);
        return result;
    }
}
