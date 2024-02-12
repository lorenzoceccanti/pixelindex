package it.unipi.largescale.pixelindex.dto;

import it.unipi.largescale.pixelindex.utils.AnsiColor;

public class MostActiveUserDTO {
    int rank;
    String username;
    int numOfReviews;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumOfReviews() {
        return numOfReviews;
    }

    public void setNumOfReviews(int numOfReviews) {
        this.numOfReviews = numOfReviews;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        int maxWidthUsername = 32; // The max len of username in our dataset
        int maxWidthNumOfReviews = ("Num of Reviews").length();

        String header = String.format("| %-" + maxWidthUsername + "s | %-" + maxWidthNumOfReviews + "s |\n",
                "Username", "Num of Reviews");

        StringBuilder separator = new StringBuilder();
        separator.append("+");
        separator.append("-".repeat(maxWidthUsername + 2));
        separator.append("+");
        separator.append("-".repeat(maxWidthNumOfReviews + 2));
        separator.append("+\n");

        String dataRow;
        if(username.getBytes().length != username.length()){
            /// Japanese chars
            int japaneseBytesLength = username.getBytes().length;
            int japaneseCharsCount = username.length();
            int adjustedWidth = maxWidthUsername - (japaneseBytesLength - japaneseCharsCount) / 2;
            dataRow = String.format("| %-" + (adjustedWidth+2) + "s | %-" + maxWidthNumOfReviews + "d |\n",
                    username, numOfReviews);
        } else {
            dataRow = String.format("| %-" + (maxWidthUsername) + "s | %-" + maxWidthNumOfReviews + "d |\n",
                    username, numOfReviews);
        }

        StringBuilder result = new StringBuilder();
        if(rank == 1){
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
