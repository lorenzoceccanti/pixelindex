package it.unipi.largescale.pixelindex.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private final List<LibraryEntry> entries = new ArrayList<>();

    public List<LibraryEntry> getEntries() {
        return entries;
    }

    public void addEntry(LibraryEntry libraryEntry) {
        entries.add(libraryEntry);
    }

    public record LibraryEntry(Game game, LocalDate addedDate, Integer releaseYear) {}
}
