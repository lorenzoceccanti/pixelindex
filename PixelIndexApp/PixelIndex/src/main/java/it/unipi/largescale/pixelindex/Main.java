// Questo è un main di prova

package it.unipi.largescale.pixelindex;

import it.unipi.largescale.pixelindex.controller.ApplicationController;
import it.unipi.largescale.pixelindex.dto.*;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.service.StatisticsService;
import it.unipi.largescale.pixelindex.service.GameService;

import jdk.jfr.Registered;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void registration(RegisteredUserService registeredUserService)
    {
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
    }

    public static void login(RegisteredUserService registeredUserService)
    {
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
    }

    public static void showFollowers(RegisteredUserService registeredUserService)
    {
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
    }

    public static void reportUser(RegisteredUserService registeredUserService)
    {
        /* Generare 100 segnalazioni a Chang Liu */
        for (int i = 0; i < 46; i++) {
            try {
                registeredUserService.reportUser("lore_"+i, "ale1968");
                // registeredUserService.reportUser("Chang Liu", "Chang Liu"); // should fail
            } catch (ConnectionException ex) {
                System.out.println(ex.getMessage());
            }
        }
        // Verifica che Chang Liu abbia 50 segnalazioni da utenti diversi lore_0, lore_1, .., lore_49
    }

    public static void getReports(StatisticsService statisticsService){
        ArrayList<UserReportsDTO> userReportsDTOs;
        try{
            userReportsDTOs = statisticsService.topNReportedUser(10);
            System.out.println("username | numberReports");
            for (int i = 0; i < userReportsDTOs.size(); i++)
                System.out.println(userReportsDTOs.get(i));
        }catch(ConnectionException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {

       ApplicationController applicationController = new ApplicationController();

        /*
        GameService gs = ServiceLocator.getGameService();
        List<GamePreviewDTO> games = null;
        try {
            games = gs.advancedSearch("grand");
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
        for(GamePreviewDTO g : games)
            System.out.println(g.getName());

*/


        /*
        // RegisteredUserService registeredUserService = ServiceLocator.getRegisteredUserService();
        // Registration use case
        // registration(registeredUserService);
        // Login use case
        // login(registeredUserService);
        // Show followers
        // showFollowers(registeredUserService);
        // Report User
        // reportUser(registeredUserService);
        StatisticsService statisticsService = ServiceLocator.getStatisticsService();
        getReports(statisticsService); // last 10 reports

         */

    }
}
