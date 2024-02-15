package it.unipi.largescale.pixelindex.dao.mongo;

import com.mongodb.MongoSocketException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.DAOException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.model.Moderator;
import it.unipi.largescale.pixelindex.model.RegisteredUser;
import it.unipi.largescale.pixelindex.utils.Utils;

import org.bson.Document;
import org.bson.conversions.Bson;


import it.unipi.largescale.pixelindex.security.Crypto;

import java.util.*;

import static com.mongodb.client.model.Filters.*;

public class RegisteredUserMongoDAO extends BaseMongoDAO {

    private static void populateUserFieldsFromDocument(RegisteredUser user, Document document) {
        user.setId(document.getObjectId("_id").toHexString());
        user.setUsername(document.getString("username"));
        user.setName(document.getString("name"));
        user.setSurname(document.getString("surname"));
        user.setRole(document.getString("role"));
        user.setEmail(document.getString("email"));
        user.setDateOfBirth(Utils.convertDateToLocalDate(document.getDate("dateOfBirth")));
    }

    public RegisteredUser makeLogin(String username, String password) throws WrongPasswordException, UserNotFoundException, DAOException {
        MongoDatabase db;
        List<Document> results;
        try (MongoClient mongoClient = beginConnection(true)) {

            db = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> usersCollection = db.getCollection("users");

            Bson myMatch = (eq("username", username));
            Bson projectionFields = Projections.fields(
                    Projections.include("hashedPassword", "username", "name", "surname",
                            "role","dateOfBirth", "email")
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
        if (Crypto.validatePassword(password, hashedPassword)) {
            String role = document.getString("role");
            RegisteredUser user;

            if (role.equals("moderator")) {
                user = new Moderator();
            } else {
                user = new RegisteredUser();
            }

            populateUserFieldsFromDocument(user, document);
            return user;
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
                .append("role", u.getRole());
        try {
            usersCollection.insertOne(clientSession, doc);
        } catch (MongoWriteException ex) {
            throw new DAOException("Already registered user [" + u.getUsername() + "]");
        }

        return u;
    }

    /**
     * @return 1 if the user reports itself, -1 if you are trying to report a mod,
     * 0 if everything is fine
     * @throws DAOException throws DAOException if something goes wrong
     */
    public int reportUser(String usernameReporting, String usernameReported) throws DAOException {
        if (usernameReported.equals(usernameReporting))
            return 1;
        MongoDatabase db;
        try (MongoClient mongoClient = beginConnection(false)) {
            db = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> usersCollection = db.getCollection("users");
            Bson myMatch1 = (and(eq("username", usernameReported),eq("role","user")));
            Document or1 = new Document("$expr", new Document("$lt",
                    Arrays.asList(new Document("$size", "$reported_by"), 50)));
            Document or2 = new Document("reported_by", new Document("$exists", false));
            Bson or = or(or1, or2);
            Bson filter = and(myMatch1, or);
            Document update = new Document("$addToSet", new Document("reported_by", usernameReporting));
            UpdateResult updateResult = usersCollection.updateOne(filter, update);
            if(updateResult.getMatchedCount() == 0)
                return -1;
            else
                return 0;
        } catch (MongoSocketException ex) {
            throw new DAOException("Error in connecting to MongoDB");
        }
    }

    public void banUser(String username) throws DAOException {
        // Starting a MongoDAO transaction
        MongoDatabase db;
        try (MongoClient mongoClient = BaseMongoDAO.beginConnection(false)) {
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
        try (MongoClient mongoClient = beginConnection(false)) {
            MongoDatabase database = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> collection = database.getCollection("users");

            List<Bson> aggregationPipeline = new ArrayList<>();

            aggregationPipeline.add(Aggregates.match(Filters.exists("isBanned",false)));

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

    public void updateFollowers(String src, String dst, int followingSrc, int followerDst) throws DAOException
    {
        try(MongoClient mc = beginConnection(false))
        {
            MongoDatabase database = mc.getDatabase("pixelindex");
            MongoCollection<Document> usersCollection = database.getCollection("users");
            List<Bson> updatePipeline = Arrays.asList(
                    Updates.set("following",
                            new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList("$username", src)), followingSrc, "$following"))),
                    Updates.set("followers",
                            new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList("$username", dst)), followerDst, "$followers")))
            );

            usersCollection.updateMany(Filters.or(Filters.eq("username", src), Filters.eq("username", dst)), updatePipeline);
        }catch(MongoSocketException ex){
            throw new DAOException("Error in connecting to MongoDB");
        }
    }

}
