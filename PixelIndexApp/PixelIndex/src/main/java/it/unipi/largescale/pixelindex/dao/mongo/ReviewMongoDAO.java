package it.unipi.largescale.pixelindex.dao.mongo;

import com.mongodb.client.*;
import it.unipi.largescale.pixelindex.dto.ReviewPreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.RatingKind;
import it.unipi.largescale.pixelindex.model.Review;
import it.unipi.largescale.pixelindex.utils.Utils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReviewMongoDAO extends BaseMongoDAO {
    private Review reviewFromQueryResult(Document result) {
        Review review = new Review();
        ObjectId resultObjectId = result.getObjectId("_id");
        review.setId(resultObjectId.toString());
        // TODO: togliere tutti gli if tanto la get ritorna null se non c'è la chiave nell'oggetto
        if (result.containsKey("review")) {
            review.setText(result.getString("review"));
        }
        if (result.containsKey("author")) {
            review.setAuthor(result.getString("author"));
        }
        if (result.containsKey("gameId")) {
            review.setGameId(result.getString("gameId"));
        }
        if (result.containsKey("recommended")) {
            review.setRating(result.getBoolean("recommended") ? RatingKind.RECOMMENDED : RatingKind.NOT_RECOMMENDED);
        } else {
            review.setRating(RatingKind.NOT_AVAILABLE);
        }
        if (result.containsKey("postedDate")) {
            review.setTimestamp(Utils.convertDateToLocalDateTime(result.getDate("postedDate")));
        }

        review.setLikes(result.getInteger("likes", 0));
        review.setDislikes(result.getInteger("dislikes", 0));

        return review;
    }

    private ReviewPreviewDTO reviewPreviewFromQueryResult(Document result) {
        ReviewPreviewDTO review = new ReviewPreviewDTO();
        ObjectId resultObjectId = result.getObjectId("_id");
        review.setId(resultObjectId.toString());
        // TODO: togliere tutti gli if tanto la get ritorna null se non c'è la chiave nell'oggetto
        if (result.containsKey("review")) {
            review.setExcerpt(result.getString("review"));
        }
        if (result.containsKey("author")) {
            review.setAuthor(result.getString("author"));
        }
        if (result.containsKey("recommended")) {
            review.setRating(result.getBoolean("recommended") ? RatingKind.RECOMMENDED : RatingKind.NOT_RECOMMENDED);
        } else {
            review.setRating(RatingKind.NOT_AVAILABLE);
        }
        if (result.containsKey("postedDate")) {
            review.setTimestamp(Utils.convertDateToLocalDateTime(result.getDate("postedDate")));
        }

        review.setLikes(result.getInteger("likes", 0));
        review.setDislikes(result.getInteger("dislikes", 0));

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

    public void insertReview(Review review) throws DAOException {
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
        } catch (Exception e) {
            throw new DAOException("Error while inserting review" + e);
        }
    }

    public List<ReviewPreviewDTO> getReviewsByGameId(String gameId, int page) throws DAOException {

        try (MongoClient mongoClient = beginConnectionWithoutReplica()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("reviews");

            List<ReviewPreviewDTO> reviews = new ArrayList<>();

            ArrayList<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                            new Document("gameId",
                                    new ObjectId("65afd5ed7ae28aa3f604e020"))),
                    new Document("$sort",
                            new Document("likes", -1L)),
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
                                    .append("likes", 1L)
                                    .append("dislikes", 1L)
                                    .append("postedDate", 1L)),
                    new Document("$skip", 0L),
                    new Document("$limit", 10L))).into(new ArrayList<>());

            for (Document res : result) {
                ReviewPreviewDTO review = reviewPreviewFromQueryResult(res);
                reviews.add(review);
            }
            return reviews;

        } catch (Exception e) {
            throw new DAOException("Error while retrieving reviews by game id" + e);
        }

    }

    public List<ReviewPreviewDTO> getReviewsByGameId(String gameId) throws DAOException {
        return getReviewsByGameId(gameId, 0);
    }
}
