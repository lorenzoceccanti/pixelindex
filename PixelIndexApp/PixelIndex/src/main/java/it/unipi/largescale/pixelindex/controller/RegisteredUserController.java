package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.User;
import it.unipi.largescale.pixelindex.service.AnalyticsService;
import it.unipi.largescale.pixelindex.service.LibraryService;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisteredUserController {
    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    ConsistencyThread consistencyThread = new ConsistencyThread(queue);
    private RegisteredMenu registeredMenu;
    private Runnable[] functionsRegistered;
    private String sessionUsername;
    private GameController gameController;
    private LibraryService libraryService;
    private RegisteredUserService registeredUserService;
    private AnalyticsService analyticsService;
    private String queryName;
    ArrayList<UserSearchDTO> userSearchDTOs;
    List<String> potentialFriends;
    ArrayList<String> rows = new ArrayList<>();
    private int totalPages;

    private void displayUsers(){
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW+"Previous page"+AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW+"Next page"+AnsiColor.ANSI_RESET);
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
    private int usersByName(String username, int page){
        try{
            userSearchDTOs = registeredUserService.searchUser(username, page);
            return 0;
        }catch(ConnectionException ex)
        {
            return 1;
        }
    }

    private int pressFollow(String username)
    {
        // Checking if the user is following by itself
        if(sessionUsername.equals(username)){
            Utils.clearConsole();
            System.out.println("[Message]: Cannot follow by yourself!");
            return 1;
        }
        try{
            String operation = registeredUserService.followUser(sessionUsername, username, consistencyThread);
            Utils.clearConsole();
            System.out.println("[Operation]: " + operation);
            return 0;
        }catch (ConnectionException ex)
        {
            return 1;
        }
    }


    private int askSearchByUsernameQuery(AtomicBoolean regMenuDisplayed){
        int pageSelection = 0;
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
            ListSelector ls = new ListSelector("Query result");
            System.out.println("Page displayed: " + (pageSelection + 1));
            result = usersByName(queryName, pageSelection);
            if(result != 0)
                return result;
            displayUsers();
            ls.addOptions(rows, "searchUserByUsername", "Press enter to edit your follow");
            choice = ls.askUserInteraction("searchUserByUsername");
            switch(choice)
            {
                case 0: // Previous page
                    exit = 0;
                    pageSelection = pageSelection > 0 ? --pageSelection : pageSelection;
                    break;
                case 1: // Next page
                    exit = 0;
                    pageSelection = pageSelection < totalPages-1 ? ++pageSelection : pageSelection;
                    break;
                case 2: // Go back
                    regMenuDisplayed.set(true);
                    exit = 1;
                    queryName = "";
                    break;
                default:
                    UserSearchDTO u = userSearchDTOs.get(choice-3);
                    // Follow or unfollow
                    // To access the userSearchDTOs access with an index decreased by 3
                    pressFollow(u.getUsername());
                    exit = 0;
            }
        }while(exit != 1);
        return result;
    }
    private int friendsYouMightKnow(){
        try{
            potentialFriends = analyticsService.suggestUsers(sessionUsername);
            System.out.println("Friends you might know:");
            if(potentialFriends.isEmpty())
                System.out.println("*** List empty ***");
            for(int i=0; i<potentialFriends.size(); i++)
                System.out.println((i+1)+") "+potentialFriends.get(i));
            showRegisteredDropdown();
            return 0;
        }catch(ConnectionException ex)
        {
            return 1;
        }
    }
    /*private int displayLibrary(){

    }*/
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
        this.totalPages = 3;
        this.queryName = "";
        this.potentialFriends = new ArrayList<>();
        this.analyticsService = ServiceLocator.getAnalyticsService();
        this.registeredUserService = ServiceLocator.getRegisteredUserService();
        this.libraryService = ServiceLocator.getLibraryService();
        registeredMenu = new RegisteredMenu();
        this.sessionUsername = username;
        this.gameController = new GameController(registeredMenu.getDisplayed(), sessionUsername, consistencyThread);
        functionsRegistered = new Runnable[]{
                () -> {
                    gameController.askGameQueryByName();
                },
                () -> {
                    askSearchByUsernameQuery(registeredMenu.getDisplayed());
                },/*,
                () -> {
                    // View your library
                    // libraryService.getGames(sessionUsername);
                }*/
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
        // After the login the thread starts
        consistencyThread.start();
        Utils.clearConsole();
        int index = showRegisteredDropdown();
    }

}
