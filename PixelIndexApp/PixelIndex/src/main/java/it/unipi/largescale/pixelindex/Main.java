// Questo è un main di prova

package it.unipi.largescale.pixelindex;

import it.unipi.largescale.pixelindex.dao.RegisteredUserMongoDAO;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
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
        RegisteredUser element = null;
        // userMongoDAO.register(registered);

        try{
            element = userMongoDAO.makeLogin("ale1968", "pippo");
        }catch(WrongPasswordException ex)
        {
            System.out.println("ale1968: wrong password");
        }
        // Controllo quali funzionalità sbloccare, se quelle da moderatore o quelle da utente comune
        if(element instanceof Moderator)
        {
            System.out.println("Full Functionalities");
        } else if(element instanceof RegisteredUser){
            System.out.println("Limited functionalities");
        } else{
            System.out.println("None");
        }


        Moderator moderator = new Moderator("italian");
        moderator.setUsername("mod001");
        moderator.setName("Mod");
        moderator.setSurname("SurMod");
        moderator.setHashedPassword(Crypto.hashPassword("mod!#"));
        moderator.setDateOfBirth(date);
        moderator.setEmail("mod@mods.net");

        RegisteredUserMongoDAO moderatorDAO = new RegisteredUserMongoDAO();
        // moderatorDAO.register(moderator);
        element = null;
        try{
            element = moderatorDAO.makeLogin("mod001","mod!#");
        }catch(WrongPasswordException ex)
        {
            System.out.println("mod001: Wrong password!");
        }

        // Controllo quali funzionalità sbloccare, se quelle da moderatore o quelle da utente comune
        if(element instanceof Moderator)
        {
            System.out.println("Full Functionalities");
        } else if(element instanceof RegisteredUser){
            System.out.println("Limited functionalities");
        } else{
            System.out.println("None");
        }
    }
}