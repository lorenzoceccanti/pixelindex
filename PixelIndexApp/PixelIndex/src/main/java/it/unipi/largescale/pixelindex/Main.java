// Questo è un main di prova

package it.unipi.largescale.pixelindex;

import it.unipi.largescale.pixelindex.dao.impl.BaseNeo4jDAO;
import it.unipi.largescale.pixelindex.dto.AuthUserDTO;
import it.unipi.largescale.pixelindex.dto.UserRegistrationDTO;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import org.neo4j.driver.Driver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Main {
    private static List<String> tryNeo(){
        try(
                Driver neoDriver = BaseNeo4jDAO.beginConnection();
                var session = neoDriver.session()
        ){
            return session.executeRead( tx -> {
                List<String> usernames = new ArrayList<>();
                var result = tx.run("MATCH (u:User) RETURN u.username LIMIT 3;");
                while(result.hasNext()){
                    usernames.add(result.next().get(0).asString());
                }
                return usernames;
            });
        }
    }
    public static void main(String[] args) {

        RegisteredUserService registeredUserService = ServiceLocator.getRegisteredUserService();

        // Registration use case

        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setName("Alessandro");
        userRegistrationDTO.setSurname("Ceccanti");
        userRegistrationDTO.setUsername("ale1968");
        userRegistrationDTO.setPassword("pippo");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse("1968-10-12", formatter);
        userRegistrationDTO.setDateOfBirth(date);
        userRegistrationDTO.setEmail("cecca2@live.it");

        AuthUserDTO authUserDTO = registeredUserService.register(userRegistrationDTO, "italian");
        System.out.println("Registration done");


        // Login use case w/ personal info
        authUserDTO = null;
        try{
            authUserDTO = registeredUserService.makeLogin("ale1968", "pippo");
            System.out.println("Welcome " + authUserDTO.getName());
            System.out.println("Your personal information:");
            System.out.println(authUserDTO);
        }catch(UserNotFoundException ex)
        {
            System.out.println("Login failed: The provided username doesn't match any registration");

        }catch(WrongPasswordException ex2)
        {
            System.out.println("Login failed: The password provided is wrong");
        }

        // NEO4J remoto
        List<String> usernames = tryNeo();
        System.out.println("Username list");
        for(int i=0; i<usernames.size(); i++)
        {
            System.out.print(usernames.get(i));
        }
        System.out.print("\n");


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
    }
}