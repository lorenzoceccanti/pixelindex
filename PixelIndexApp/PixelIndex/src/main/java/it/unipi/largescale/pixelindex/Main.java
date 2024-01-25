// Questo è un main di prova

package it.unipi.largescale.pixelindex;

import it.unipi.largescale.pixelindex.dao.RegisteredUserMongoDAO;
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

        RegisteredUserMongoDAO userMongoDAO = new RegisteredUserMongoDAO();
        // userMongoDAO.register(registered);

        if(userMongoDAO.makeLogin("ale1968", "pippo") != null)
            System.out.println("successo");

    }
}