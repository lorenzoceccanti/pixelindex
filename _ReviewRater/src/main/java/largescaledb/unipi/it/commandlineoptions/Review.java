package largescaledb.unipi.it.commandlineoptions;

public class Review {
    private String nome;
    private boolean like;
    private boolean dislike;

    public Review(String nome) {
        this.nome = nome;
    }

    public void setLike(boolean like) {
        this.like = like;
        if (like) {
            this.dislike = false; // Ensure mutual exclusion
        }
    }

    public void setDislike(boolean dislike) {
        this.dislike = dislike;
        if (dislike) {
            this.like = false; // Ensure mutual exclusion
        }
    }

    @Override
    public String toString() {
        return nome + ": \n( " + (like ? "*" : " ") + " ) Like\n( " + (dislike ? "*" : " ") + " ) Dislike";
    }

    public String visualizeReview()
    {
        return nome + " like: " + like + " dislike: " + dislike;
    }
}
