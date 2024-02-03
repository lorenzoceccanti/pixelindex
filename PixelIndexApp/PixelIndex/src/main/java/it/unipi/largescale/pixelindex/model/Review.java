package it.unipi.largescale.pixelindex.model;

import java.time.LocalDateTime;

public class Review {
    private String id;
    private String author;
    private String gameId;
    private RatingKind rating;
    private String text;
    private LocalDateTime timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public RatingKind getRating() {
        return rating;
    }

    public void setRating(RatingKind rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", gameId='" + gameId + '\'' +
                ", rating=" + rating +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp.toString() +
                '}';
    }
}
