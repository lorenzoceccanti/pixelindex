package it.unipi.largescale.pixelindex.dto;

import it.unipi.largescale.pixelindex.utils.AnsiColor;

public class GameRatingDTO {
    private String name;
    private int releaseYear;
    private double positiveRatingRatio;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPositiveRatingRatio() {
        return positiveRatingRatio;
    }

    public void setPositiveRatingRatio(double positiveRatingRatio) {
        this.positiveRatingRatio = positiveRatingRatio;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
    @Override
    public String toString(){
        return AnsiColor.ANSI_PURPLE+name+AnsiColor.ANSI_RESET+" "+AnsiColor.ANSI_GREEN+releaseYear+AnsiColor.ANSI_RESET+" "+AnsiColor.ANSI_YELLOW+" "+positiveRatingRatio+AnsiColor.ANSI_RESET;
    }
}
