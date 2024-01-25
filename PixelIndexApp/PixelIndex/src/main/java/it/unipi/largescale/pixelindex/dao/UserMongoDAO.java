package it.unipi.largescale.pixelindex.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import it.unipi.largescale.pixelindex.model.RegisteredUser;
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
import java.util.*;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.elemMatch;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.internal.HexUtils.toHex;

public class UserMongoDAO extends BaseMongoDAO implements UserDAO{

    public static String hashPassword(String password) {
        byte[] salt = "ABCD".getBytes();

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
    public RegisteredUser makeLogin(String username, String password)
    {
        // Questa è una semplice prova
        MongoDatabase db;
        try (MongoClient mongoClient = beginConnection()) {
            /*
            db = mongoClient.getDatabase("pixelindex");
            MongoCollection<Document> usersCollection = db.getCollection("users");

            Bson myMatch = (eq("username", username));
            Bson projectionFields = Projections.fields(
                    Projections.include("hashedPassword"),
                    Projections.excludeId()
            );
            List<Document> results = usersCollection.find(myMatch).projection(projectionFields).into(new ArrayList<>());
            String hashedPassword = results.get(0).getString("hashedPassword");
            System.out.println(hashedPassword);
             */
            String hashedPassword = hashPassword(password);
            System.out.println(hashedPassword);
            // Qua poi bisogna riconoscere che tipo di utente è
            System.out.println(validatePassword(password, hashedPassword));

        } catch(Exception ex)
        {
            System.out.println("Error in connecting to MongoDB: login failed");
        }

        RegisteredUser ru = new RegisteredUser();
        return ru;
    }

    @Override
    public RegisteredUser register(RegisteredUser ru){
        return ru;
    }
}
