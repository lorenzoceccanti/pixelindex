package it.unipi.largescale.pixelindex.dto;

import it.unipi.largescale.pixelindex.model.RatingKind;

import java.time.LocalDateTime;

public class ReviewPreviewDTO {
    
    private String id;
    private String author;
    private RatingKind rating;
    private String excerpt;
    private Integer likes;
    private Integer dislikes;

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

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    @Override
    public String toString() {
        return "ReviewPreviewDTO{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", rating=" + rating +
                ", excerpt='" + excerpt + '\'' +
                ", likes=" + likes + '\'' +
                ", dislikes=" + dislikes + '\'' +
                '}';
    }
}
