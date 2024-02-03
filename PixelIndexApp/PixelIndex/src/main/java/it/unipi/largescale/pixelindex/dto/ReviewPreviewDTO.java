package it.unipi.largescale.pixelindex.dto;

import it.unipi.largescale.pixelindex.model.RatingKind;

import java.time.LocalDateTime;

public class ReviewPreviewDTO {

    private String id;
    private String author;
    private RatingKind rating;
    private String excerpt;
    private LocalDateTime timestamp;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public RatingKind getRating() {
        return rating;
    }

    public String getText() {
        return excerpt;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setRating(RatingKind rating) {
        this.rating = rating;
    }

    public void setExcerpt(String text) {
        this.excerpt = text;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return "ReviewPreviewDTO{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", rating=" + rating +
                ", excerpt='" + excerpt + '\'' +
                ", timestamp='" + timestamp.toString() + '\'' +
                '}';
    }
}
