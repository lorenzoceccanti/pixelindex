package it.unipi.largescale.pixelindex.model;

import it.unipi.largescale.pixelindex.utils.AnsiColor;

import java.time.LocalDate;
import java.util.Arrays;

public class Game {
    private String id;
    private String name;
    private String category;
    private String pegiRating;
    private LocalDate releaseDate;
    private String[] gameModes;
    private String[] genres;
    private String[] companies;
    private String[] languages;
    private String[] platforms;
    private String summary;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    public String getStatus() { return status; }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String[] getGameModes() {
        return gameModes;
    }

    public void setGameModes(String[] gameModes) {
        this.gameModes = gameModes;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getCompanies() {
        return companies;
    }

    public void setCompanies(String[] companies) {
        this.companies = companies;
    }

    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public String[] getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String[] platforms) {
        this.platforms = platforms;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPegiRating() {
        return pegiRating;
    }

    public void setPegiRating(String pegiRating) {
        this.pegiRating = pegiRating;
    }

    @Override
    public String toString(){
        String result = "";
        result += (AnsiColor.ANSI_BLUE+ "Name: " + name+ AnsiColor.ANSI_RESET);
        result += (AnsiColor.ANSI_GREEN+" Category: " + category+AnsiColor.ANSI_RESET);

        result += (releaseDate == null ? "" : (AnsiColor.ANSI_CYAN+" Release Date: " + releaseDate.toString()+AnsiColor.ANSI_RESET));
        result += (genres == null ? "" : "\nGenres: " + Arrays.toString(genres));
        result += (gameModes == null ? "" : "\nGame modes: " + Arrays.toString(gameModes));
        result += (companies == null ? "" :"\nCompanies: " + Arrays.toString(companies));
        result += (languages == null ? "" :"\nLanguages: " + Arrays.toString(languages));
        result += (platforms == null ? "":"\nPlatforms: " + Arrays.toString(platforms));
        result += (summary == null ? "" : ("\nSummary: " + summary));

        return result;
    }

}
