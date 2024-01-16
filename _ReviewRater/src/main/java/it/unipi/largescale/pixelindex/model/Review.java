package it.unipi.largescale.pixelindex.model;

import java.util.ArrayList;
public class Review {
    private int id;
    private int starRating;
    private String text;
    private String playerName; // User that wrote the review
    private String gameName;
    // ArrayList with the list of user that have put their like.
    // In the real project it should be a list of User
    private ArrayList<String> listLikeUser;
    // ArrayList with the list of user that have put their dislike.
    // In the real project it should be a list of User
    private ArrayList<String> listDislikeUser;

    public Review() {this.listLikeUser = new ArrayList<String>();
    this.listDislikeUser = new ArrayList<String>();}
    public Review(int id, int starRating, String text, String playerName,
                  String gameName)
    {
        this.id = id;
        this.starRating = starRating;
        this.text = text;
        this.playerName = playerName;
        this.gameName = gameName;
        this.listLikeUser = new ArrayList<String>();
        this.listDislikeUser = new ArrayList<String>();
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getStarRating(){
        return this.starRating;
    }

    public void setStarRating(int rating){
        this.starRating = rating;
    }

    public String getText(){
        return this.text;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getPlayerName()
    {
        return this.playerName;
    }

    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }

    public String getGameName(){
        return this.gameName;
    }

    public void setGameName(String gameName){
        this.gameName = gameName;
    }

    public ArrayList<String> getListLikeUser() {
        return listLikeUser;
    }

    public void setListLikeUser(ArrayList<String> listLikeUser) {
        this.listLikeUser = listLikeUser;
    }

    public ArrayList<String> getListDislikeUser() {
        return listDislikeUser;
    }

    public void setListDislikeUser(ArrayList<String> listDislikeUser) {
        this.listDislikeUser = listDislikeUser;
    }
}
