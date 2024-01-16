package it.unipi.largescale.pixelindex.dto;
// How the review is showed to the user
public class ReviewDTO {
    private int id;
    private String author;
    private String text;
    private int starRating;
    private int numberLikes;
    private int numberDislikes;
    private boolean cursorSelection;

    public ReviewDTO() { this.cursorSelection = false; }

    public ReviewDTO(String author, String text, int starRating, int numberLikes, int numberDislikes)
    {
        this.author = author;
        this.text = text;
        this.starRating = starRating;
        this.numberLikes = numberLikes;
        this.numberDislikes = numberDislikes;
        this.cursorSelection = false;
    }
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStarRating() {
        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }

    public int getNumberLikes() {
        return numberLikes;
    }

    public void setNumberLikes(int numberLikes) {
        this.numberLikes = numberLikes;
    }

    public int getNumberDislikes() {
        return numberDislikes;
    }

    public void setNumberDislikes(int numberDislikes) {
        this.numberDislikes = numberDislikes;
    }

    public boolean isCursorSelection() {
        return cursorSelection;
    }

    public void setCursorSelection(boolean cursorSelection) {
        this.cursorSelection = cursorSelection;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // to string
    @Override
    public String toString(){
        String result = "";
        result += ("by: " + author + "\n");
        result += ("stars: " + starRating + "/5\n");
        result += (text + "\n");

        if(cursorSelection)
            result += ("> likes: " + numberLikes + " dislikes: " + numberDislikes + "\n");
        else
            result += ("likes: " + numberLikes + " dislikes: " + numberDislikes + "\n");

        return result;
    }
}
