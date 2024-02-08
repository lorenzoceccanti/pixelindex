package it.unipi.largescale.pixelindex.dao.mongo;

import com.mongodb.MongoSocketException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.model.Moderator;
import it.unipi.largescale.pixelindex.model.RegisteredUser;
import it.unipi.largescale.pixelindex.model.User;
import it.unipi.largescale.pixelindex.utils.Utils;

import org.bson.Document;
import org.bson.conversions.Bson;

import it.unipi.largescale.pixelindex.security.Crypto;

import java.time.format.DateTimeParseException;
import java.util.*;

import static com.mongodb.client.model.Filters.*;

public class RegisteredUserMongoDAO extends BaseMongoDAO {

    public RegisteredUser makeLogin(String username, String password) throws WrongPasswordException, UserNotFoundException, DAOException {
        MongoDatabase db;
        List<Document> results = null;
        try (MongoClient mongoClient = beginConnection()) {

            db = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> usersCollection = db.getCollection("users");

            Bson myMatch = (eq("username", username));
            Bson projectionFields = Projections.fields(
                    Projections.include("hashedPassword", "username", "name", "surname",
                            "role", "language", "dateOfBirth", "email")
            );
            results = usersCollection.find(myMatch).projection(projectionFields).into(new ArrayList<>());
        } catch (MongoSocketException e) {
            throw new DAOException("Error in connecting to MongoDB");
        }
        // Checking the emptiness of the array
        if (results.isEmpty())
            throw new UserNotFoundException();
        Document document = results.get(0);
        String hashedPassword = document.getString("hashedPassword");
        RegisteredUser u = null;
        if (Crypto.validatePassword(password, hashedPassword)) {
            // Qua poi bisogna riconoscere che tipo di utente è
            String role = document.getString("role");
            if (role.equals("user")) {
                RegisteredUser ru = new RegisteredUser();
                ru.setId(document.getObjectId("_id").toHexString());
                ru.setUsername(document.getString("username"));
                ru.setName(document.getString("name"));
                ru.setSurname(document.getString("surname"));
                ru.setRole(document.getString("role"));
                ru.setLanguage(document.getString("language"));
                ru.setEmail(document.getString("email"));
                ru.setDateOfBirth(Utils.convertDateToLocalDate(document.getDate("dateOfBirth")));
                u = ru;
            } else if (role.equals("moderator")) {
                Moderator mo = new Moderator();
                mo.setId(document.getObjectId("_id").toHexString());
                mo.setUsername(document.getString("username"));
                mo.setName(document.getString("name"));
                mo.setSurname(document.getString("surname"));
                mo.setRole(document.getString("role"));
                mo.setLanguage(document.getString("language"));
                mo.setEmail(document.getString("email"));
                mo.setDateOfBirth(Utils.convertDateToLocalDate(document.getDate("dateOfBirth")));
                u = mo;
            }
            return u;
        } else {
            throw new WrongPasswordException();
        }
    }

    public RegisteredUser register(MongoClient mc, RegisteredUser u, ClientSession clientSession) throws DAOException {
        MongoDatabase db;
        db = mc.getDatabase("pixelindex");
        MongoCollection<Document> usersCollection = db.getCollection("users");

        Document doc = new Document("username", u.getUsername())
                .append("hashedPassword", u.getHashedPassword())
                .append("dateOfBirth", u.getDateOfBirth())
                .append("email", u.getEmail())
                .append("name", u.getName())
                .append("surname", u.getSurname())
                .append("role", u.getRole())
                .append("language", u.getLanguage());

        try {
            usersCollection.insertOne(clientSession, doc);
        } catch (MongoWriteException ex) {
            throw new DAOException("Already registered user [" + u.getUsername() + "]");
        }

        return u;
    }

    public void reportUser(String usernameReporting, String usernameReported) throws DAOException {
        if (usernameReported.equals(usernameReporting))
            return;
        MongoDatabase db;
        try (MongoClient mongoClient = beginConnection()) {
            db = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> usersCollection = db.getCollection("users");
            Bson myMatch1 = (eq("username", usernameReported));
            Document or1 = new Document("$expr", new Document("$lt",
                    Arrays.asList(new Document("$size", "$reported_by"), 50)));
            Document or2 = new Document("reported_by", new Document("$exists", false));
            Bson or = or(or1, or2);
            Bson filter = and(myMatch1, or);
            Document update = new Document("$addToSet", new Document("reported_by", usernameReporting));
            usersCollection.updateOne(filter, update);
        } catch (MongoSocketException ex) {
            throw new DAOException("Error in connecting to MongoDB");
        }
    }

    public void banUser(String username) throws DAOException {
        // Starting a MongoDAO transaction
        MongoDatabase db;
        try (MongoClient mongoClient = BaseMongoDAO.beginConnection()) {
            try (ClientSession clientSession = mongoClient.startSession()) {
                clientSession.startTransaction();
                try {
                    db = mongoClient.getDatabase("pixelindex");
                    MongoCollection<Document> usersCollection = db.getCollection("users");
                    Bson myMatch = eq("username", username);
                    Document update = new Document("$set", new Document("isBanned", true));
                    usersCollection.updateOne(clientSession, myMatch, update);

                    MongoCollection<Document> reviewCollection = db.getCollection("reviews");
                    Bson myMatch2 = eq("author", username);
                    // Removing all the reviews placed by the user
                    reviewCollection.deleteMany(clientSession, myMatch2);
                    clientSession.commitTransaction();
                } catch (MongoSocketException ex) {
                    clientSession.abortTransaction();
                    throw new DAOException(ex);
                }
            }
        }
    }

    public ArrayList<UserSearchDTO> searchUser(String username, int page) throws DAOException {
        ArrayList<UserSearchDTO> users = new ArrayList<>();
        try (MongoClient mongoClient = beginConnectionWithoutReplica()) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("users");

            List<Bson> aggregationPipeline = new ArrayList<>();

            // Add field for similarity score based on string lengths
            aggregationPipeline.add(Aggregates.addFields(new Field<>("similarityScore",
                    new Document("$divide", Arrays.asList(
                            new Document("$strLenCP", "$username"),
                            username.length())))));

            // Ensure the document matches the regex
            aggregationPipeline.add(Aggregates.match(Filters.regex("username", username, "i")));

            // Sort by similarityScore in descending order, then by followers count
            // If two or more users have the same similarity score
            aggregationPipeline.add(Aggregates.sort(Sorts.orderBy(Sorts.ascending("similarityScore"),
                    Sorts.descending("followers"))));

            // Implement pagination
            int pageSize = 10;
            aggregationPipeline.add(Aggregates.skip(pageSize * page));
            aggregationPipeline.add(Aggregates.limit(pageSize));

            ArrayList<Document> results = collection.aggregate(aggregationPipeline).into(new ArrayList<>());
            for (Document result : results) {
                UserSearchDTO userSearchDTO = new UserSearchDTO();
                userSearchDTO.setUsername(result.getString("username"));
                userSearchDTO.setFollowingsCount(result.get("following") != null ? result.getInteger("following") : 0);
                userSearchDTO.setFollowersCount(result.getInteger("followers", 0));
                users.add(userSearchDTO);
            }
        } catch (Exception e) {
            throw new DAOException("Error retrieving users: " + e.getMessage());
        }
        return users;
    }


}
