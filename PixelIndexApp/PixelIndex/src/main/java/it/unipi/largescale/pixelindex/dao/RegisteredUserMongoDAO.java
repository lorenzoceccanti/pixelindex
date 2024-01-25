package it.unipi.largescale.pixelindex.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.model.Moderator;
import it.unipi.largescale.pixelindex.model.RegisteredUser;
import org.bson.Document;
import org.bson.conversions.Bson;

import it.unipi.largescale.pixelindex.security.Crypto;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class RegisteredUserMongoDAO extends BaseMongoDAO implements UserDAO{

    public static LocalDate convertDateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }


    @Override
    public RegisteredUser makeLogin(String username, String password)
    {
        // Questa è una semplice prova
        MongoDatabase db;
        try (MongoClient mongoClient = beginConnection()) {

            db = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> usersCollection = db.getCollection("users");

            Bson myMatch = (eq("username", username));
            Bson projectionFields = Projections.fields(
                    Projections.include("hashedPassword", "username", "name", "surname",
                            "role", "language", "dateOfBirth")
            );
            List<Document> results = usersCollection.find(myMatch).projection(projectionFields).into(new ArrayList<>());
            Document document = results.get(0);
            String hashedPassword = document.getString("hashedPassword");
            RegisteredUser u = null;
            if(Crypto.validatePassword(password, hashedPassword))
            {
                // Qua poi bisogna riconoscere che tipo di utente è
                String role = document.getString("role");
                if(role.equals("user"))
                {
                    RegisteredUser ru = new RegisteredUser();
                    ru.setId(document.getObjectId("_id").toHexString());
                    ru.setUsername(document.getString("username"));
                    ru.setName(document.getString("name"));
                    ru.setSurname(document.getString("surname"));
                    ru.setRole(document.getString("role"));
                    ru.setLanguage(document.getString("language"));
                    ru.setDateOfBirth(convertDateToLocalDate(document.getDate("dateOfBirth")));
                    u = ru;
                } else if(role.equals("moderator")) {
                    Moderator mo = new Moderator();
                    mo.setId(document.getObjectId("_id").toHexString());
                    mo.setUsername(document.getString("username"));
                    mo.setName(document.getString("name"));
                    mo.setSurname(document.getString("surname"));
                    mo.setRole(document.getString("role"));
                    mo.setLanguage(document.getString("language"));
                    mo.setDateOfBirth(convertDateToLocalDate(document.getDate("dateOfBirth")));
                    u = mo;
                }
                return u;
            } else {
                throw new WrongPasswordException();
            }
        } catch(Exception ex)
        {
            System.out.println("Error in connecting to MongoDB: login failed");
        }
        return null;
    }

    @Override
    public RegisteredUser register(RegisteredUser u) {
        MongoDatabase db;
        try (MongoClient mongoClient = beginConnection()) {

            db = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> usersCollection = db.getCollection("users");

            Document doc = new Document("username", u.getUsername())
                    .append("hashedPassword", u.getHashedPassword())
                    .append("dateOfBirth", u.getDateOfBirth())
                    .append("email", u.getEmail())
                    .append("name", u.getName())
                    .append("surname", u.getSurname())
                    .append("role", u.getRole())
                    .append("language", u.getLanguage());

            usersCollection.insertOne(doc);
        } catch (Exception ex) {
            System.out.println("Error in registering the user");
        }
        return u;
    }
}
