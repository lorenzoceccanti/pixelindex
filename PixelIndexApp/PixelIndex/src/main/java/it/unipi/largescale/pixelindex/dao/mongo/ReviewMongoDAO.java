package it.unipi.largescale.pixelindex.dao.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import it.unipi.largescale.pixelindex.dto.ReviewPreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.RatingKind;
import it.unipi.largescale.pixelindex.model.Review;
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
        if (result.containsKey("review")) {
            review.setText(result.getString("review"));
        }
        if (result.containsKey("author")) {
            review.setAuthor(result.getString("author"));
        }
        if (result.containsKey("rating")) {
            review.setRating(result.getBoolean("recommended") ? RatingKind.RECOMMENDED : RatingKind.NOT_RECOMMENDED);
        } else {
            review.setRating(RatingKind.NOT_AVAILABLE);
        }
        if (result.containsKey("postedDate")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(result.getString("postedDate"), formatter);
            LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
            review.setTimestamp(localDateTime);
        }
        return review;
    }

    public Review getReviewById(String id) throws DAOException {
        Review review = null;
        try (MongoClient mongoClient = beginConnection()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("reviews");
            Document query = new Document("_id", new ObjectId(id));
            Document result = collection.find(query).first();
            if (result != null) {
                review = reviewFromQueryResult(result);
            }
        } catch (Exception e) {
            throw new DAOException("Error while retrieving review by id" + e);
        }

        return review;
    }

    public void insertReview(Review review) throws DAOException {
        try (MongoClient mongoClient = beginConnection()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("reviews");
            Document document = new Document();
            document.append("review", review.getText());
            document.append("author", review.getAuthor());
            document.append("recommended", review.getRating() == RatingKind.RECOMMENDED);
            document.append("postedDate", review.getTimestamp().toString());
            document.append("likes", 0);
            document.append("dislikes", 0);
            collection.insertOne(document);
        } catch (Exception e) {
            throw new DAOException("Error while inserting review" + e);
        }
    }

    public List<ReviewPreviewDTO> getReviewsByGameId(String gameId, int page) throws DAOException {
        //DA FARE -> decidere quali campi mostrare nella preview, scorrendo le pagine su mongo con limit e skip
        try (MongoClient mongoClient = beginConnection()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("reviews");
            Document query = new Document("gameId", gameId);
            List<ReviewPreviewDTO> reviews = new ArrayList<>();


            AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                            new Document("game_id",
                                    new ObjectId("65afd5ed7ae28aa3f604e020"))),
                    new Document("$project",
                            new Document("excerpt",
                                    new Document("$concat", Arrays.asList(new Document("$substr", Arrays.asList("$review", 0L, 50L)), "...")))
                                    .append("author", 1L)
                                    .append("recommended", 1L)),
                    new Document("$skip", 10L),
                    new Document("$limit", 10L)));


            for (Document res : result) {
                ReviewPreviewDTO review = new ReviewPreviewDTO();
                review.setId(res.getObjectId("_id").toString());
                review.setAuthor(res.getString("author"));
                if (res.containsKey("rating")) {
                    review.setRating(res.getBoolean("recommended") ? RatingKind.RECOMMENDED : RatingKind.NOT_RECOMMENDED);
                } else {
                    review.setRating(RatingKind.NOT_AVAILABLE);
                }
                review.setExcerpt(res.getString("excerpt"));
                review.setTimestamp(res.getString("postedDate"));
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
