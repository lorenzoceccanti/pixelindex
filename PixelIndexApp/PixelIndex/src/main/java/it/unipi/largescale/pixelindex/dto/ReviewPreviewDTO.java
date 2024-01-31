package it.unipi.largescale.pixelindex.dto;

public class ReviewPreviewDTO {

    private String id;
    private String author;
    private RatingKind rating;
    private String excerpt;
    private String timestamp;

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

    public String getTimestamp() {
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

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
