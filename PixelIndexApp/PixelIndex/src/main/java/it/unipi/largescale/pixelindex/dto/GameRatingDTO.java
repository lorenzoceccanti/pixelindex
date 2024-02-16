package it.unipi.largescale.pixelindex.dto;

public class GameRatingDTO {
    private int rank;
    private String name;
    private int releaseYear;
    private double positiveRatingRatio;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPositiveRatingRatio(double positiveRatingRatio) {
        this.positiveRatingRatio = positiveRatingRatio;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString(){
        int maxWidthName = 80;
        int maxWidthYear = "Release Year".length();
        int maxWidthRatio = "Rating Ratio".length();

        String header = String.format("| %-" + maxWidthName + "s | %-" + maxWidthYear + "s | %-" + maxWidthRatio + "s |\n",
                "Name", "Release Year", "Rating Ratio");

        StringBuilder separator = new StringBuilder();
        separator.append("+");
        separator.append("-".repeat(maxWidthName + 2));
        separator.append("+");
        separator.append("-".repeat(maxWidthYear + 2));
        separator.append("+");
        separator.append("-".repeat(maxWidthRatio + 2));
        separator.append("+\n");

        String dataRow = String.format("| %-" + maxWidthName + "s | %-" + maxWidthYear + "d | %-" + maxWidthRatio + ".2f |\n",
                name, releaseYear, positiveRatingRatio);

        StringBuilder result = new StringBuilder();
        if(rank == 1){
            result.append(separator);
            result.append(header);
        }
        result.append(separator);
        result.append(dataRow);
        if(rank == 10)
            result.append(separator);
        return result.toString();
    }
}
