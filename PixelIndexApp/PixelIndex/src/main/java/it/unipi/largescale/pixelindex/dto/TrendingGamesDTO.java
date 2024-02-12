package it.unipi.largescale.pixelindex.dto;

public class TrendingGamesDTO {
    public int rank;
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

    public void setRank(int rank) {
        this.rank = rank;
    }

    /*@Deprecated
    public String toString() {
        return "Game: " + gameName + "\t-\tCount: " + count;
    }*/

    public String toString() {
        int maxWidthGameName = 75;
        int maxWidthCount = ("Players count").length();
        String header = String.format("| %-" + maxWidthGameName + "s | %-" + maxWidthCount + "s |\n",
                "Game Name", "Players count");
        StringBuilder separator = new StringBuilder();

        separator.append("+");
        separator.append("-".repeat(maxWidthGameName + 2));
        separator.append("+");
        separator.append("-".repeat(maxWidthCount + 2));
        separator.append("+\n");

        String dataRow = String.format("| %-" + maxWidthGameName + "s | %-" + maxWidthCount + "d |\n", gameName, count);

        StringBuilder result = new StringBuilder();
        if(rank == 1)
        {
            result.append(separator);
            result.append(header);
        }
        result.append(separator);
        result.append(dataRow);
        if(rank == 10){
            result.append(separator);
        }
        return result.toString();
    }

}
