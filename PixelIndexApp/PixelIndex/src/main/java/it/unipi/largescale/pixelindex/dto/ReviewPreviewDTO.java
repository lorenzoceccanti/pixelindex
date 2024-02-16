package it.unipi.largescale.pixelindex.dto;

import it.unipi.largescale.pixelindex.model.RatingKind;
import it.unipi.largescale.pixelindex.utils.AnsiColor;

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

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    @Override
    public String toString() {
        return AnsiColor.ANSI_RED+"Author: "+author+AnsiColor.ANSI_RESET
                +AnsiColor.ANSI_YELLOW+" Rating: "+rating+AnsiColor.ANSI_RESET
                +" Excerpt: " + excerpt
                +AnsiColor.ANSI_BLUE+" Likes: "+likes+AnsiColor.ANSI_RESET
                +AnsiColor.ANSI_PURPLE+" Dislikes: "+dislikes+AnsiColor.ANSI_RESET;
    }
}
