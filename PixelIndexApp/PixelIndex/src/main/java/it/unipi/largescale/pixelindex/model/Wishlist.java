package it.unipi.largescale.pixelindex.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Wishlist {
    private final List<WishListEntry> entries = new ArrayList<>();

    public List<WishListEntry> getEntries() {
        return entries;
    }

    public void addEntry(WishListEntry libraryEntry) {
        entries.add(libraryEntry);
    }

    public record WishListEntry(Game game, LocalDate addedDate, Integer releaseYear) {}
}
