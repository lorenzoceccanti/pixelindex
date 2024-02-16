package it.unipi.largescale.pixelindex.dto;

import it.unipi.largescale.pixelindex.utils.AnsiColor;

import java.time.LocalDate;

public class GameLibraryElementDTO extends GamePreviewDTO {
    private LocalDate addedDate;

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    @Override
    public String toString() {
        String result = super.toString();
        result += (" "+ AnsiColor.ANSI_CYAN+"Added Date: " + addedDate+AnsiColor.ANSI_RESET);
        return result;
    }
}
