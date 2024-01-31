// Questo è un main di prova

package it.unipi.largescale.pixelindex;

import it.unipi.largescale.pixelindex.dto.AuthUserDTO;
import it.unipi.largescale.pixelindex.dto.UserRegistrationDTO;
import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {

        RegisteredUserService registeredUserService = ServiceLocator.getRegisteredUserService();

        // Registration use case: MONGODB and Neo4j: check RegUserServiceImpl

        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setName("Alessandro");
        userRegistrationDTO.setSurname("Ceccanti");
        userRegistrationDTO.setUsername("ale1968");
        userRegistrationDTO.setPassword("pippo");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse("1968-10-12", formatter);
        userRegistrationDTO.setDateOfBirth(date);
        userRegistrationDTO.setEmail("cecca2@live.it");

        try {
            AuthUserDTO authUserDTO = registeredUserService.register(userRegistrationDTO, "italian");
        } catch (ConnectionException ex) {
            System.out.println(ex.getMessage());
            // System.exit(1);
        }

        // Login use case w/ personal info
        AuthUserDTO authUserDTO = null;
        try {
            authUserDTO = registeredUserService.makeLogin("ale1968", "pippo");
            System.out.println("Welcome " + authUserDTO.getName());
            System.out.println("Your personal information:");
            System.out.println(authUserDTO);
        } catch (UserNotFoundException ex) {
            System.out.println("Login failed: The provided username doesn't match any registration");

        } catch (WrongPasswordException ex2) {
            System.out.println("Login failed: The password provided is wrong");
        } catch (ConnectionException ex3) {
            System.out.println(ex3.getMessage());
        }


        // Further use case to implement: login as moderator
        // The following only for testing purposes, the correct way to proceed is to have only the DTOs
        /*
        RegisteredUser element = null;
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
        */


        // Print number of following/followers of Chang Liu
        // Chang Liu starts to follow ale1968
        ArrayList<UserSearchDTO> searchResult = new ArrayList<>();
        try {

            searchResult = registeredUserService.searchUser("ale1968");
            System.out.println("username | numberOfFollowers | numberOfFollowed");
            for (int i = 0; i < searchResult.size(); i++)
                System.out.println(searchResult.get(i));

            searchResult = registeredUserService.searchUser("chang liu");
            System.out.println("username | numberOfFollowers | numberOfFollowed");
            for (int i = 0; i < searchResult.size(); i++)
                System.out.println(searchResult.get(i));

            // registeredUserService.unfollowUser("Chang Liu", "ale1968");
            /*
            registeredUserService.followUser("Chang Liu", "ale1968");
            searchResult = registeredUserService.searchUser("Chang Liu");
            System.out.println("username | numberOfFollowers | numberOfFollowed");
            for(int i=0; i<searchResult.size(); i++)
                System.out.println(searchResult.get(i));*/
        } catch (ConnectionException ex) {
            System.out.println(ex.getMessage());
        }

        /* Generare 100 segnalazioni a Chang Liu */
        for (int i = 0; i < 100; i++) {
            try {
                registeredUserService.reportUser("lore", "Chang Liu");
                // registeredUserService.reportUser("Chang Liu", "Chang Liu"); // should fail
            } catch (ConnectionException ex) {
                System.out.println(ex.getMessage());
            }
        }
        // Verifica che Chang Liu abbia 50 segnalazioni da utenti diversi lore_0, lore_1, .., lore_49
    }
}
