package it.unipi.largescale.pixelindex.model;

import it.unipi.largescale.pixelindex.utils.AnsiColor;

import java.time.LocalDateTime;

public class Review {
    private String id;
    private String author;
    private String gameId;
    private RatingKind rating;
    private String text;
    private LocalDateTime timestamp;
    private Integer likes;
    private Integer dislikes;

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

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    @Override
    public String toString(){
        return AnsiColor.ANSI_RED+"By: "+author+AnsiColor.ANSI_RESET+AnsiColor.ANSI_BLUE+" Rating: "+rating+AnsiColor.ANSI_RESET+"\n"+
                AnsiColor.ANSI_CYAN+"Posted date: "+timestamp.toString()+AnsiColor.ANSI_RESET+"\n"+
                AnsiColor.ANSI_GREEN+"Likes: " + likes+AnsiColor.ANSI_RESET+AnsiColor.ANSI_PURPLE+" Dislikes: "+dislikes+AnsiColor.ANSI_RESET+"\n"+
                "«"+text+"»";
    }
}
