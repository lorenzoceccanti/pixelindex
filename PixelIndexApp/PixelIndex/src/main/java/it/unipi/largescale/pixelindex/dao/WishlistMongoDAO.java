package it.unipi.largescale.pixelindex.dao;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import it.unipi.largescale.pixelindex.dto.GameDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.utils.Utils;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class WishlistMongoDAO extends BaseMongoDAO {

    public void insertGame(String userId, GameDTO game) throws DAOException {
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

    public void removeGame(String userId, String gameId) throws DAOException {
        try (MongoClient mongoClient = beginConnection()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("wishlists");

            Document query = new Document()
                    .append("gameId", new ObjectId(gameId))
                    .append("userId", new ObjectId(userId));

            collection.deleteOne(query);
        } catch (Exception e) {
            throw new DAOException("Error connecting to MongoDB: " + e);
        }
    }

    public List<GameDTO> getGames(String userId) throws DAOException {
        List<GameDTO> wishlistGames = new ArrayList<>();
        try (MongoClient mongoClient = beginConnection()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("wishlists");

            Document query = new Document("userId", new ObjectId(userId));
            for (Document doc : collection.find(query)) {
                GameDTO wishListGame = new GameDTO();
                wishListGame.setId(doc.getObjectId("gameId").toString());
                wishListGame.setName(doc.getString("name"));
                wishListGame.setReleaseDate(Utils.convertDateToLocalDate(doc.getDate("releaseDate")));
                wishlistGames.add(wishListGame);
            }
        } catch (Exception e) {
            throw new DAOException("Error retrieving games from wishlist: " + e);
        }
        return wishlistGames;
    }
}