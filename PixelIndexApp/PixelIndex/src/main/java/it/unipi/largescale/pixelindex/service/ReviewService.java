package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.controller.ConsistencyThread;
import it.unipi.largescale.pixelindex.dto.ReviewPageDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Reaction;
import it.unipi.largescale.pixelindex.model.Review;

public interface ReviewService {

    void insertReview(Review review, String gameName, Integer gameReleaseYear) throws ConnectionException;
    void deleteReview(String reviewId, ConsistencyThread consistencyThread) throws ConnectionException;
    ReviewPageDTO getReviews(String gameId, String username, Integer page) throws ConnectionException;
    Review getReviewDetails(String id) throws ConnectionException;
    String addReaction(String reviewId, String username, Reaction reaction, String gameId, String reviewAuthor, ConsistencyThread consistencyThread) throws ConnectionException;
}
