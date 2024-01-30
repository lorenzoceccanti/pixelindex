package it.unipi.largescale.pixelindex.dao;

import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import it.unipi.largescale.pixelindex.dao.impl.BaseMongoDAO;
import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import org.bson.Document;
import org.bson.types.ObjectId;

public class WishlistMongoDAO extends BaseMongoDAO {

    public void insertWishlist(String userId, GameDTO game) throws DAOException {
        try (MongoClient mongoClient = beginConnection()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("wishlists");

            Document wishlistItem = new Document()
                    .append("gameId", new ObjectId(game.getId()))
                    .append("userId", new ObjectId(userId))
                    .append("gameName", game.getName())
                    .append("releaseDate", java.util.Date.from(game.getReleaseDate().atStartOfDay()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toInstant()));

            try {
                collection.insertOne(wishlistItem);
            } catch (MongoWriteException e) {
                if (e.getError().getCode() == 11000) {
                    // Duplicate key error
                    throw new DAOException("A wishlist item with the same gameId and userId already exists.");
                } else {
                    throw new DAOException("Error inserting wishlist item: " + e);
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error connecting to MongoDB: " + e);
        }
    }
}