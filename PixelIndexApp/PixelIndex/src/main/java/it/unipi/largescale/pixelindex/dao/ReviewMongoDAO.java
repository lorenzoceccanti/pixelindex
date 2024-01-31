package it.unipi.largescale.pixelindex.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.model.RatingKind;
import it.unipi.largescale.pixelindex.model.Review;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

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
            collection.insertOne(document);
        } catch (Exception e) {
            throw new DAOException("Error while inserting review" + e);
        }
    }
}
