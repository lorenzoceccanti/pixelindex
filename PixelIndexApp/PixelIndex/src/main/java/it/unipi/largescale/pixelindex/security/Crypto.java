package it.unipi.largescale.pixelindex.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Crypto {
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
}
