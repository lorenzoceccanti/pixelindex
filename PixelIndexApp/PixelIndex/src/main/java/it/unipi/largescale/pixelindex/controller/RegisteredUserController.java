package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.GameLibraryElementDTO;
import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.User;
import it.unipi.largescale.pixelindex.service.SuggestionsService;
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
    private SuggestionsService suggestionsService;
    private String queryName;
    ArrayList<UserSearchDTO> userSearchDTOs;
    List<String> potentialFriends;
    List<GameLibraryElementDTO> GameLibraryElementDTOs;
    ArrayList<String> rows = new ArrayList<>();

    private void displayUsers(){
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW+"Previous page"+AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW+"Next page"+AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW+"Go back"+AnsiColor.ANSI_RESET);
        userSearchDTOs.stream().forEach(userSearchDTO -> {
            rows.add(userSearchDTO.toString());
        });
        if(rows.size() <= 3){
            System.out.println("*** Empty ***");
        }
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

    private int pressFollow(String username, StringBuilder message)
    {
        // Checking if the user is following by itself
        if(sessionUsername.equals(username)){
            message.replace(0, message.toString().length(),"[Message]: Cannot follow by yourself!");
            return 1;
        }
        try{
            String operation = registeredUserService.followUser(sessionUsername, username, consistencyThread);
            message.replace(0, message.toString().length(),"[Operation]: " + operation);
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
        StringBuilder followMessage = new StringBuilder();

        if(queryName.isEmpty())
        {
            System.out.println("Query?");
            Scanner sc = new Scanner(System.in);
            queryName = sc.nextLine();
        }
        do
        {
            Utils.clearConsole();
            System.out.println(followMessage);
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
                    pageSelection = (rows.size() > 3) ? ++pageSelection : pageSelection;
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
                    pressFollow(u.getUsername(), followMessage);
                    exit = 0;
            }
        }while(exit != 1);
        return result;
    }
    private int friendsYouMightKnow(){
        List<String> rows = new ArrayList<>();
        registeredMenu.getDisplayed().set(false);
        try{
            potentialFriends = suggestionsService.suggestUsers(sessionUsername);
            rows.add("Friends you might know");
            if(potentialFriends.isEmpty())
                rows.add("*** List empty ***");
            /*for(int i=0; i<potentialFriends.size(); i++)
                System.out.println((i+1)+") "+potentialFriends.get(i));*/
            rows.addAll(potentialFriends);
            showRegisteredDropdown(rows);
            return 0;
        }catch(ConnectionException ex)
        {
            return 1;
        }
    }
    private int queryUserLibrary(String username, int page){
        try{
            GameLibraryElementDTOs = libraryService.getGames(username, page);
            return 0;
        }catch(ConnectionException ex){
            return 1;
        }
    }

    private void buildUserLibrary(){
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW+"Previous page"+AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW+"Next page"+AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW+"Go back"+AnsiColor.ANSI_RESET);
        GameLibraryElementDTOs.stream().forEach(GameLibraryElementDTO -> {
            rows.add(GameLibraryElementDTO.toString());
        });
        if(rows.size() <= 3){
            System.out.println("*** Empty ***");
        }
    }
    private int displayLibrary(AtomicBoolean regMenuDisplayed){
        int pageSelection = 0;
        int result = 1; int choice = -1; int exit = 0;
        regMenuDisplayed.set(false);
        do{
            Utils.clearConsole();
            ListSelector ls = new ListSelector("Your library:");
            System.out.println("Page displayed: " + (pageSelection+1));
            result = queryUserLibrary(sessionUsername, pageSelection);
            if(result != 0)
                return result;
            buildUserLibrary();
            ls.addOptions(rows, "libraryDropdown", "Press enter to remove a game from your library");
            choice = ls.askUserInteraction("libraryDropdown");
            switch(choice){
                case 0: // Previous page
                    exit = 0;
                    pageSelection = pageSelection > 0 ? --pageSelection : pageSelection;
                    break;
                case 1: // Next page
                    exit = 0;
                    pageSelection = (rows.size() > 3) ? ++pageSelection : pageSelection;
                    break;
                case 2: // Go back
                    regMenuDisplayed.set(true);
                    exit = 1;
                    break;
                default:
                    break;
                    // Removing game from library
            }
        }while(exit != 1);
        return result;
    }
    private int showRegisteredDropdown(List<String> view)
    {
        int opt = -1;
        do{
            Utils.clearConsole();
            view.stream().forEach(elem -> {
                System.out.println(elem);
            });
            registeredMenu.getDisplayed().set(true);
            opt = registeredMenu.displayMenu(sessionUsername);
            functionsRegistered[opt].run();
            view.clear();
        }while(registeredMenu.getDisplayed().get());
        return opt;
    }
    public RegisteredUserController(String username)
    {
        this.queryName = "";
        this.potentialFriends = new ArrayList<>();
        this.suggestionsService = ServiceLocator.getSuggestionsService();
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
                },
                () -> {
                    // View your library
                    displayLibrary(registeredMenu.getDisplayed());
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
        // After the login the thread starts
        consistencyThread.start();
        Utils.clearConsole();
        int index = showRegisteredDropdown(new ArrayList<>());
    }

}
