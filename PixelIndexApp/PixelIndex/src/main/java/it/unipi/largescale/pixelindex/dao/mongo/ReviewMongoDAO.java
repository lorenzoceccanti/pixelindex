package it.unipi.largescale.pixelindex.dao.mongo;

import com.mongodb.client.*;
import it.unipi.largescale.pixelindex.dto.ReviewPageDTO;
import it.unipi.largescale.pixelindex.dto.ReviewPreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.RatingKind;
import it.unipi.largescale.pixelindex.model.Review;
import it.unipi.largescale.pixelindex.utils.Utils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReviewMongoDAO extends BaseMongoDAO {
    private Review reviewFromQueryResult(Document result) {
        Review review = new Review();
        ObjectId resultObjectId = result.getObjectId("_id");
        review.setId(resultObjectId.toString());

        review.setText(result.getString("review"));
        review.setAuthor(result.getString("author"));
        review.setGameId(result.getString("gameId"));
        review.setTimestamp(Utils.convertDateToLocalDateTime(result.getDate("postedDate")));
        review.setLikes(result.getInteger("likes", 0));
        review.setDislikes(result.getInteger("dislikes", 0));
        if (result.containsKey("recommended")) {
            review.setRating(result.getBoolean("recommended") ? RatingKind.RECOMMENDED : RatingKind.NOT_RECOMMENDED);
        } else {
            review.setRating(RatingKind.NOT_AVAILABLE);
        }

        return review;
    }

    private ReviewPreviewDTO reviewPreviewFromQueryResult(Document result) {
        ReviewPreviewDTO review = new ReviewPreviewDTO();
        ObjectId resultObjectId = result.getObjectId("_id");
        review.setId(resultObjectId.toString());

        review.setExcerpt(result.getString("review"));
        review.setAuthor(result.getString("author"));
        review.setLikes(result.getInteger("likes", 0));
        review.setDislikes(result.getInteger("dislikes", 0));
        if (result.containsKey("recommended")) {
            review.setRating(result.getBoolean("recommended") ? RatingKind.RECOMMENDED : RatingKind.NOT_RECOMMENDED);
        } else {
            review.setRating(RatingKind.NOT_AVAILABLE);
        }

        return review;
    }

    public Review getReviewById(String id) throws DAOException {
        Review review = null;
        try (MongoClient mongoClient = beginConnectionWithoutReplica()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("reviews");
            Document query = new Document("_id", new ObjectId(id));
            Document result = collection.find(query).first();
            if (result != null) {
                review = reviewFromQueryResult(result);
            }
        } catch (Exception e) {
            throw new DAOException("Error while retrieving review by id: " + e);
        }

        return review;
    }

    public String insertReview(Review review) throws DAOException {
        try (MongoClient mongoClient = beginConnectionWithoutReplica()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("reviews");
            Document document = new Document();
            document.append("review", review.getText());
            document.append("author", review.getAuthor());
            document.append("gameId", new ObjectId(review.getGameId()));
            document.append("recommended", review.getRating() == RatingKind.RECOMMENDED);
            document.append("postedDate", review.getTimestamp());
            collection.insertOne(document);
            return document.get("_id").toString();

        } catch (Exception e) {
            throw new DAOException("Error while inserting review" + e);
        }
    }

    public void deleteReview(String reviewId) throws DAOException {
        try (MongoClient mongoClient = beginConnectionWithoutReplica()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("reviews");
            Document query = new Document("_id", new ObjectId(reviewId));
            collection.deleteOne(query);
        } catch (Exception e) {
            throw new DAOException("Error while deleting review" + e);
        }
    }

    public ReviewPageDTO getReviewsByGameId(String gameId, String username, int page) throws DAOException {
        try (MongoClient mongoClient = beginConnectionWithoutReplica()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("reviews");

            ArrayList<Document> result = collection.aggregate(
                    Arrays.asList(new Document("$match",
                                    new Document("gameId",
                                            new ObjectId(gameId))),
                            new Document("$facet",
                                    new Document("totalCount", Arrays.asList(new Document("$count", "count")))
                                            .append("data", Arrays.asList(
                                                    new Document("$addFields",
                                                            new Document("byUser",
                                                                    new Document("$cond",
                                                                            new Document("if",
                                                                                    new Document("$eq", Arrays.asList("$author", username)))
                                                                                    .append("then", 1L)
                                                                                    .append("else", 0L)))),
                                                    new Document("$sort",
                                                            new Document("byUser", -1L)
                                                                    .append("likes", -1L)),
                                                    new Document("$project",
                                                            new Document("review",
                                                                    new Document("$cond",
                                                                            new Document("if",
                                                                                    new Document("$gt", Arrays.asList(new Document("$strLenCP", "$review"), 50L)))
                                                                                    .append("then",
                                                                                            new Document("$concat", Arrays.asList(new Document("$substrCP", Arrays.asList("$review", 0L, 50L)), "...")))
                                                                                    .append("else", "$review")))
                                                                    .append("author", 1L)
                                                                    .append("recommended", 1L)
                                                                    .append("postedDate", 1L)),
                                                    new Document("$skip", 10L * page),
                                                    new Document("$limit", 10L)))),
                            new Document("$unwind", "$totalCount"),
                            new Document("$addFields",
                                    new Document("totalCount", "$totalCount.count")),
                            new Document("$replaceRoot",
                                    new Document("newRoot",
                                            new Document("$mergeObjects", Arrays.asList("$$ROOT",
                                                    new Document("data", "$data")
                                                            .append("totalCount", "$totalCount"))))),
                            new Document("$project",
                                    new Document("data", 1L)
                                            .append("totalCount", 1L)))).into(new ArrayList<>());

            ReviewPageDTO reviewPage = new ReviewPageDTO();
            reviewPage.setTotalReviewsCount(result.get(0).getInteger("totalCount"));

            List<ReviewPreviewDTO> reviews = new ArrayList<>();
            for (Document res : result.get(0).getList("data", Document.class)) {
                ReviewPreviewDTO review = reviewPreviewFromQueryResult(res);
                reviews.add(review);
            }
            reviewPage.setReviews(reviews);

            return reviewPage;

        } catch (Exception e) {
            throw new DAOException("Error while retrieving reviews by game id" + e);
        }

    }

    public ReviewPageDTO getReviewsByGameId(String gameId) throws DAOException {
        return getReviewsByGameId(gameId, "", 0);
    }

    public ReviewPageDTO getReviewsByGameId(String gameId, String username) throws DAOException {
        return getReviewsByGameId(gameId, username, 0);
    }

    public ReviewPageDTO getReviewsByGameId(String gameId, int page) throws DAOException {
        return getReviewsByGameId(gameId, "", page);
    }
}
