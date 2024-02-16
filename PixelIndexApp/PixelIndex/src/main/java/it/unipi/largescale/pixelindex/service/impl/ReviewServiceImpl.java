package it.unipi.largescale.pixelindex.service.impl;

import it.unipi.largescale.pixelindex.controller.ConsistencyThread;
import it.unipi.largescale.pixelindex.dao.mongo.ReviewMongoDAO;
import it.unipi.largescale.pixelindex.dao.neo4j.ReviewNeo4jDAO;
import it.unipi.largescale.pixelindex.dto.ReviewPageDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.Reaction;
import it.unipi.largescale.pixelindex.service.ReviewService;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Review;

import java.util.Map;


public class ReviewServiceImpl implements ReviewService {

    private final ReviewMongoDAO reviewMongoDAO;
    private final ReviewNeo4jDAO reviewNeo4jDAO;

    public ReviewServiceImpl() {
        this.reviewMongoDAO = new ReviewMongoDAO();
        this.reviewNeo4jDAO = new ReviewNeo4jDAO();
    }

    public void insertReview(Review review, String gameName, Integer gameReleaseYear) throws ConnectionException {
        try {
            reviewMongoDAO.insertReview(review, gameName, gameReleaseYear);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public void deleteReview(String reviewId, ConsistencyThread consistencyThread) throws ConnectionException {
        try {
        reviewMongoDAO.deleteReview(reviewId);
        consistencyThread.addTask(() -> {
            try {
                reviewNeo4jDAO.deleteReview(reviewId);
            } catch (DAOException e) {
                System.out.println("Consistency update failed while deleting a review.");
            }
        });
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public ReviewPageDTO getReviews(String gameId, String username, Integer page) throws ConnectionException {
        try {
            return reviewMongoDAO.getReviewsByGameId(gameId, username, page);
        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }

    public Review getReviewDetails(String id) throws ConnectionException{
        try{
            return reviewMongoDAO.getReviewById(id);
        }catch(DAOException e){
            throw new ConnectionException(e);
        }
    }

    public String addReaction(String reviewId, String username, Reaction reaction, String gameId, String reviewAuthor, ConsistencyThread consistencyThread) throws ConnectionException {
        try {
            String outcome = reviewNeo4jDAO.addReaction(reviewId, username, reaction, gameId, reviewAuthor);
            consistencyThread.addTask(() -> {
                try {
                    Map<String, Integer> reactions = reviewNeo4jDAO.getReactionsCount(reviewId);
                    reviewMongoDAO.setReactionsCount(reviewId, reactions.get("likes"), reactions.get("dislikes"));
                } catch (DAOException e) {
                    System.out.println("Consistency update failed while updating review reactions.");
                }
            });

            return outcome;

        } catch (DAOException e) {
            throw new ConnectionException(e);
        }
    }
}
