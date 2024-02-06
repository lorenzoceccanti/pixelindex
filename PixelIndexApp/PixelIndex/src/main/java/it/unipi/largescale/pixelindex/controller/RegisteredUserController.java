package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.User;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.dropdown.RegisteredMenu;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RegisteredUserController {
    private RegisteredMenu registeredMenu;
    private Runnable[] functionsRegistered;
    private String sessionUsername;
    private GameController gameController;
    private RegisteredUserService registeredUserService;
    private String queryName;
    ArrayList<UserSearchDTO> userSearchDTOs;
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
            userSearchDTOs = registeredUserService.searchUser(username);
            return 0;
        }catch(ConnectionException ex)
        {
            return 1;
        }
    }

    private int askSearchByUsernameQuery(){
        int result;
        ListSelector ls = new ListSelector("Query result");
        if(queryName.isEmpty())
        {
            System.out.println("Query?");
            Scanner sc = new Scanner(System.in);
            queryName = sc.nextLine();
        }
        Utils.clearConsole();
        result = usersByName(queryName);
        if(result != 0)
            return result;
        displayUsers();
        ls.addOptions(rows, "searchUserByUsername", "Press en");
        return result;
    }
    private int showRegisteredDropdown()
    {
        while(registeredMenu.getDisplayed().get())
        {
            int opt = registeredMenu.displayMenu(sessionUsername);
            functionsRegistered[opt].run();
        }
        return registeredMenu.displayMenu(sessionUsername);
    }
    public RegisteredUserController(String username)
    {
        this.registeredUserService = ServiceLocator.getRegisteredUserService();
        registeredMenu = new RegisteredMenu();
        this.sessionUsername = username;
        this.gameController = new GameController(registeredMenu.getDisplayed());
        functionsRegistered = new Runnable[]{
                () -> {
                    gameController.askGameQueryByName();
                },
                () -> {
                    askSearchByUsernameQuery();
                },
                () ->{
                    System.exit(0);
                }
        };
    }

    public void execute(){
        int index = showRegisteredDropdown();
    }

}
