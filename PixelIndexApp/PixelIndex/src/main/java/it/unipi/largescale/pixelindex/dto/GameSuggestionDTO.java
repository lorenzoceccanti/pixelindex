package it.unipi.largescale.pixelindex.dto;

import it.unipi.largescale.pixelindex.utils.AnsiColor;

public class GameSuggestionDTO {
    private String gameName;
    private int connectionsNumber;

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setConnectionsNumber(int connectionsNumber) {
        this.connectionsNumber = connectionsNumber;
    }

    public String toString() {
        String result = "";
        result += (AnsiColor.ANSI_BLUE + "Game name: " + gameName + AnsiColor.ANSI_RESET);
        result += (" " + AnsiColor.ANSI_RED + "Connections count: " + connectionsNumber + AnsiColor.ANSI_RESET);
        return result;
    }
}
