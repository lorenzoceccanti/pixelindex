package it.unipi.largescale.pixelindex.dto;

import java.time.LocalDate;

public class SearchGameDTO {
    private String name;
    private LocalDate releaseDate;


    public String getName() {
        return name;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}
