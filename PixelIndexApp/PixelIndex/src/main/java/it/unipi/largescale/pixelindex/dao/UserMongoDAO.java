package it.unipi.largescale.pixelindex.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.model.Moderator;
import it.unipi.largescale.pixelindex.model.RegisteredUser;
import it.unipi.largescale.pixelindex.model.User;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.ECField;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.elemMatch;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.internal.HexUtils.toHex;

public class UserMongoDAO extends BaseMongoDAO implements UserDAO{

    public static LocalDate convertDateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }
    public static String hashPassword(String password) {
        byte[] salt = generateSalt();

        // Hash the password with the salt
        byte[] hashedPassword = hashWithPBKDF2(password, salt, 1000);

        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hashedPasswordB64 = Base64.getEncoder().encodeToString(hashedPassword);

        return saltB64 + ":" + hashedPasswordB64;
    }

    private static byte[] hashWithPBKDF2(String password, byte[] salt, int iterations) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
            SecretKey secretKey = factory.generateSecret(spec);
            return secretKey.getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean validatePassword(String plainPassword, String hashedPassword) {
        // Separare il sale e l'hash dalla stringa risultante di hashPassword()
        String[] parts = hashedPassword.split(":");
        String saltBase64 = parts[0];
        String storedHashedPasswordBase64 = parts[1];

        // Convertire il sale e l'hash dal formato Base64 al formato di byte
        byte[] salt = Base64.getDecoder().decode(saltBase64);
        byte[] storedHashedPassword = Base64.getDecoder().decode(storedHashedPasswordBase64);

        // Calcolare l'hash della password fornita con il sale estratto
        byte[] hashedPasswordToCheck = hashWithPBKDF2(plainPassword, salt, 1000);

        // Confrontare l'hash calcolato con l'hash originale
        return Arrays.equals(hashedPasswordToCheck, storedHashedPassword);
    }


    @Override
    public User makeLogin(String username, String password)
    {
        User user = null;
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

            if(validatePassword(password, hashedPassword))
            {
                // Qua poi bisogna riconoscere che tipo di utente è
                String role = document.getString("role");
                if(role.equals("user"))
                {
                    RegisteredUser ru = new RegisteredUser();
                    ru.setId(document.getString("_id"));
                    ru.setUsername(document.getString("username"));
                    ru.setName(document.getString("name"));
                    ru.setSurname(document.getString("surname"));
                    ru.setRole(document.getString("role"));
                    ru.setLanguage(document.getString("language"));
                    ru.setDateOfBirth(convertDateToLocalDate(document.getDate("dateOfBirth")));
                    user = ru;
                } else if(role.equals("moderator")) {
                    Moderator mo = new Moderator();
                    mo.setId(document.getString("_id"));
                    mo.setUsername(document.getString("username"));
                    mo.setName(document.getString("name"));
                    mo.setSurname(document.getString("surname"));
                    mo.setRole(document.getString("role"));
                    mo.setLanguage(document.getString("language"));
                    mo.setDateOfBirth(convertDateToLocalDate(document.getDate("dateOfBirth")));
                    user = mo;
                }
                return user;
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
    public RegisteredUser register(User u){
        MongoDatabase db;
        try (MongoClient mongoClient = beginConnection()) {

            db = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> usersCollection = db.getCollection("users");

            Document doc = new Document("username", u.getUsername())
                    .append("hashedPassword"
        return ru;
    }
}
