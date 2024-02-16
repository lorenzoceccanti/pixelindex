package it.unipi.largescale.pixelindex.dto;

import it.unipi.largescale.pixelindex.utils.AnsiColor;

public class UserReportsDTO {
    private String username;
    private int numberReports;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNumberReports(int numberReports) {
        this.numberReports = numberReports;
    }

    public String toString(){
        return AnsiColor.ANSI_BLUE+"User: " + username + AnsiColor.ANSI_RESET+" " + AnsiColor.ANSI_PURPLE+"Number of reports: " + numberReports+AnsiColor.ANSI_RESET;
    }
}
