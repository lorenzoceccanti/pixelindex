package it.unipi.largescale.pixelindex.dto;

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
}
