package it.unipi.largescale.pixelindex.dao;

import it.unipi.largescale.pixelindex.dto.ReviewDTO;
import it.unipi.largescale.pixelindex.model.Review;

import java.util.ArrayList;


public class ReviewDAO {
    // Here we suppose to work in memory
    ArrayList<Review> revList = new ArrayList<>();

    public void addReview(Review r)
    {
        revList.add(r);
    }

    /**
     Shows a limited number of reviews to a user
     @param start The index of the first review showed
     @param end The index of the last review showed
     @return An array list of ReviewDTO
     */
    public ArrayList<ReviewDTO> listReviews(int start, int end){
        ArrayList<ReviewDTO> reviewRetList = new ArrayList<>();
        for(int i=start; i<end; i++)
        {
            Review temp = revList.get(i);
            int currLikes, currDislikes;
            // Computing the number of likes for the current review
            try{
                currLikes = temp.getListLikeUser().size();
            }catch(NullPointerException ex)
            {
                currLikes = 0;
            }
            try{
                currDislikes = temp.getListDislikeUser().size();
            }catch(NullPointerException ex)
            {
                currDislikes = 0;
            }
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setId(temp.getId());
            reviewDTO.setAuthor(temp.getPlayerName());
            reviewDTO.setText(temp.getText());
            reviewDTO.setStarRating(temp.getStarRating());
            reviewDTO.setNumberLikes(currLikes);
            reviewDTO.setNumberDislikes(currDislikes);

            reviewRetList.add(reviewDTO);

        }
        return reviewRetList;
    }

    /**
     Gives the possibility to user to add his like to the review
     @param reviewId The id of the review involved
     @param user The username of the user that is putting his like
     @return A int: 0 if the like adding has been successful,
     1 if the user has already put his like, -1 if the review doesn't exists
     */
    public int addLike(int reviewId, String user){
        // Searching the review among the list
        for(int i=0; i<revList.size(); i++)
        {
            Review temp = revList.get(i);
            if(temp.getId() == reviewId){
                // Review found. Add like if not already there
                // Retrieving the list of users that put like previously
                ArrayList<String> l = temp.getListLikeUser();
                for(int j = 0; j<l.size(); j++)
                {
                    // If I immediately find an user, it means failure
                    String str = l.get(j);
                    if(str.equals(user)){
                        return 1; // Like already put
                    }
                }
                // Nothing found, adding like
                l.add(user);
                return 0;
            }
        }
        // Here if does not exists in memory the review to which
        // the user wants to put his like on
        return -1;
    }
    /**
     Gives the possibility to user to remove his like to the review
     @param reviewId The id of the review involved
     @param user The username of the user that is removing his like
     @return A boolean: true if the like removing has been successful,
     false otherwise
     */
    public boolean removeLike(int reviewId, String user){
        // Searching the review among the list
        for(int i=0; i<revList.size(); i++)
        {
            Review temp = revList.get(i);
            if(temp.getId() == reviewId){
                // Getting the list of user liking the review
                ArrayList<String> l = temp.getListLikeUser();
                // This methods removes the user from the list of likes
                // If the element is not found, the list is unchanged
                boolean sts = l.remove(user);
                if(sts)
                    return true;
            }
        }
        return false;
    }

    /**
     Gives the possibility to user to add his dislike to the review
     @param reviewId The id of the review involved
     @param user The username of the user that is putting his dislike
     @return A int: 0 if the dislike adding has been successful,
     1 if the user has already put his dislike, -1 if the review doesn't exists
     */
    public int addDislike(int reviewId, String user){
        // Searching the review among the list
        for(int i=0; i<revList.size(); i++)
        {
            Review temp = revList.get(i);
            if(temp.getId() == reviewId){
                // Review found. Add like if not already there
                // Retrieving the list of users that put like previously
                ArrayList<String> l = temp.getListDislikeUser();
                for(int j = 0; j<l.size(); j++)
                {
                    // If I immediately find an user, it means failure
                    String str = l.get(j);
                    if(str.equals(user)){
                        return 1; // Like already put
                    }
                }
                // Nothing found, adding like
                l.add(user);
                return 0;
            }
        }
        // Here if does not exists in memory the review to which
        // the user wants to put his like on
        return -1;
    }

    /**
     Gives the possibility to user to remove his dislike to the review
     @param reviewId The id of the review involved
     @param user The username of the user that is removing his dislike
     @return A boolean: true if the dislike removing has been successful,
     false otherwise
     */
    public boolean removeDislike(int reviewId, String user){
        // Searching the review among the list
        for(int i=0; i<revList.size(); i++)
        {
            Review temp = revList.get(i);
            if(temp.getId() == reviewId){
                // Getting the list of user liking the review
                ArrayList<String> l = temp.getListDislikeUser();
                // This methods removes the user from the list of likes
                // If the element is not found, the list is unchanged
                boolean sts = l.remove(user);
                if(sts)
                    return true;
            }
        }
        return false;
    }

}
