package it.unipi.largescale.pixelindex.dto;

import java.time.LocalDate;

public class GameDTO {

    private String id;
    private String name;
    private LocalDate releaseDate;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setId(String id) { this.id = id; }

    public void setName(String name) {
        this.name = name;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}
