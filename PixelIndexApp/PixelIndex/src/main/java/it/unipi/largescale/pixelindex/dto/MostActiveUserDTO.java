package it.unipi.largescale.pixelindex.dto;

import it.unipi.largescale.pixelindex.utils.AnsiColor;

public class MostActiveUserDTO {
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
    @Override
    public String toString(){
        return AnsiColor.ANSI_YELLOW+username+AnsiColor.ANSI_RESET+" " + AnsiColor.ANSI_BLUE+numOfReviews+AnsiColor.ANSI_RESET;
    }
}
