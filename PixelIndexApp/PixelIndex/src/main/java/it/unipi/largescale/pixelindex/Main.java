// Questo è un main di prova

package it.unipi.largescale.pixelindex;

import it.unipi.largescale.pixelindex.dao.UserMongoDAO;
import it.unipi.largescale.pixelindex.model.*;
import it.unipi.largescale.pixelindex.security.Crypto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse("1968-10-12", formatter);
        System.out.println("Hello world!");
        System.out.println("myDOB: " + date);

        RegisteredUser registered = new RegisteredUser("ITA");
        registered.setName("Alessandro");
        registered.setSurname("Ceccanti");
        registered.setUsername("ale1968");
        registered.setHashedPassword(Crypto.hashPassword("pippo"));
        registered.setDateOfBirth(date);
        registered.setEmail("cecca2@live.it");

        User u = registered;
        UserMongoDAO userMongoDAO = new UserMongoDAO();
        userMongoDAO.register(u);

        // Prova enumerato
        Review r = new Review();
        r.setId("R001");
        r.setAuthor("A001");
        r.setRating(RatingKind.RECOMMENDED);
        System.out.println(r.toString());

        // Prova connessione MONGO remoto
        UserMongoDAO uMongoDao = new UserMongoDAO();


        User u = new RegisteredUser();


        uMongoDao.makeLogin("maskedgingerjock", "ciao");

    }
}