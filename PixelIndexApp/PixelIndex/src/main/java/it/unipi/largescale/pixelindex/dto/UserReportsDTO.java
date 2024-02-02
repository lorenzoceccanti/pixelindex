package it.unipi.largescale.pixelindex.dto;

public class UserReportsDTO {
    private String username;
    private int numberReports;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumberReports() {
        return numberReports;
    }

    public void setNumberReports(int numberReports) {
        this.numberReports = numberReports;
    }

    public String toString(){
        return username + " " + numberReports;
    }
}
