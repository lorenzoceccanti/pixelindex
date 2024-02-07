package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.User;
import it.unipi.largescale.pixelindex.service.AnalyticsService;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.dropdown.RegisteredMenu;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisteredUserController {
    private RegisteredMenu registeredMenu;
    private Runnable[] functionsRegistered;
    private String sessionUsername;
    private GameController gameController;
    private RegisteredUserService registeredUserService;
    private AnalyticsService analyticsService;
    private String queryName;
    ArrayList<UserSearchDTO> userSearchDTOs;
    List<String> potentialFriends;
    ArrayList<String> rows = new ArrayList<>();

    private void displayUsers(){
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW+"Go back"+AnsiColor.ANSI_RESET);
        userSearchDTOs.stream().forEach(userSearchDTO -> {
            rows.add(userSearchDTO.toString());
        });
    }
    /** Returns 1 if there have been connection errors,
     * 0 if not error occoured
     *
     * @param username The string inserted
     */
    private int usersByName(String username){
        try{
            userSearchDTOs = registeredUserService.searchUser(username, 0);
            return 0;
        }catch(ConnectionException ex)
        {
            return 1;
        }
    }

    private int pressFollow(String username)
    {
        try{
            registeredUserService.followUser(sessionUsername, username);
            return 0;
        }catch (ConnectionException ex)
        {
            return 1;
        }
    }

    private int pressUnfollow(String username)
    {
        try{
            registeredUserService.unfollowUser(sessionUsername, username);
            return 0;
        }catch(ConnectionException ex){
            return 1;
        }
    }

    private int askSearchByUsernameQuery(AtomicBoolean regMenuDisplayed){
        int result = 1; int choice = -1; int exit = 0;
        regMenuDisplayed.set(false);

        if(queryName.isEmpty())
        {
            System.out.println("Query?");
            Scanner sc = new Scanner(System.in);
            queryName = sc.nextLine();
        }
        do
        {
            Utils.clearConsole();
            ListSelector ls = new ListSelector("Query result");
            result = usersByName(queryName);
            if(result != 0)
                return result;
            displayUsers();
            ls.addOptions(rows, "searchUserByUsername", "Press enter to edit your follow");
            // Checking choice: the index is to decrease by 1
            choice = ls.askUserInteraction("searchUserByUsername");
            if(choice != 0) // otherwise go back
            {
                /*
                UserSearchDTO userSearchDTO = userSearchDTOs.get(choice-1);
                if(userSearchDTO.getIsFollowed().isEmpty())
                {
                    // Add follow
                    int sts = pressFollow(userSearchDTO.getUsername());
                    if(sts != 0)
                        return sts;
                } else if(userSearchDTO.getIsFollowed().equals("*")){
                    // Remove follow
                    int sts = pressUnfollow(userSearchDTO.getUsername());
                }*/
            } else {
                exit = 1;
            }
        }while(exit != 1);
        // Going back
        regMenuDisplayed.set(true);
        queryName = "";
        return result;
    }
    private int friendsYouMightKnow(){
        try{
            potentialFriends = analyticsService.suggestUsers("ilDuca");
            //Utils.clearConsole();
            System.out.println("Friends you might know:");
            for(int i=0; i<potentialFriends.size(); i++)
                System.out.println((i+1)+") "+potentialFriends.get(i));
            showRegisteredDropdown();
            return 0;
        }catch(ConnectionException ex)
        {
            return 1;
        }
    }
    private int showRegisteredDropdown()
    {
        int opt = -1;
        do{
            registeredMenu.getDisplayed().set(true);
            opt = registeredMenu.displayMenu(sessionUsername);
            functionsRegistered[opt].run();
        }while(registeredMenu.getDisplayed().get());
        return opt;
    }
    public RegisteredUserController(String username)
    {
        this.queryName = "";
        this.potentialFriends = new ArrayList<>();
        this.analyticsService = ServiceLocator.getAnalyticsService();
        this.registeredUserService = ServiceLocator.getRegisteredUserService();
        registeredMenu = new RegisteredMenu();
        this.sessionUsername = username;
        this.gameController = new GameController(registeredMenu.getDisplayed());
        functionsRegistered = new Runnable[]{
                () -> {
                    gameController.askGameQueryByName();
                },
                () -> {
                    askSearchByUsernameQuery(registeredMenu.getDisplayed());
                },
                () -> {
                    registeredMenu.getDisplayed().set(false);
                    friendsYouMightKnow();
                },
                () ->{
                    System.exit(0);
                }
        };
    }
    public void execute(){
        Utils.clearConsole();
        int index = showRegisteredDropdown();
    }

}
