package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.AuthUserDTO;
import it.unipi.largescale.pixelindex.dto.UserLoginDTO;
import it.unipi.largescale.pixelindex.dto.UserRegistrationDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.service.GameService;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.dropdown.UnregisteredMenu;

import java.io.Console;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class UnregisteredUserController {
    private UserLoginDTO userLoginDTO;
    private UserRegistrationDTO userRegistrationDTO;
    private UnregisteredMenu unregisteredMenu;
    private RegisteredUserService registeredUserService;
    private GameService gameService;
    private Runnable[] functionsUnregistered;
    private GameController gameController;
    private StatisticsController statisticsController;
    private String sessionUsername = "";
    private int isModerator;
    int errorCode = -1;

    /** Invokes the userService for making the login
     *
     * @return 1 if the login failed due to connection errors
     * 2 if the login failed due to wrong username
     * 3 if the login failed due to wrong password
     * 6 if the login succeded as mod
     * 0 if the login succeded
     */
    private int login(){
        try{
            AuthUserDTO authUserDTO = registeredUserService.makeLogin(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            if(authUserDTO.getRole().equals("moderator"))
                return 6;
            else
                return 0;
        }catch(ConnectionException ex)
        {
            System.out.println(ex.getMessage());
            return 1;
        }catch(UserNotFoundException ex)
        {
            System.out.println("User name does not exists. Retry");
            return 2;
        }catch(WrongPasswordException ex)
        {
            System.out.println("Wrong password. Retry");
            return 3;
        }
    }

    /** Invokes the register service for letting the user registering to the platform
     * @param preferredLanguage The language spoken by the user
     * @return 4 if the registration failed for any reason
     * 5 if the registration succeded
     */
    private int register(String preferredLanguage){
        try{
            registeredUserService.register(userRegistrationDTO, preferredLanguage);
            return 5;
        }catch(ConnectionException ex)
        {
            return 4;
        }
    }

    /** Asks credentials to the user
     *
     * @return 1 if the login failed due to connection errors
     * 2 if the login failed due to wrong username
     * 3 if the login failed due to wrong password
     * 0 if the login succeded
     */
    private int askCredentials(AtomicBoolean displayed){
        Scanner sc = new Scanner(System.in);
        System.out.println("Username?");
        String username = sc.nextLine();
        System.out.println("Password?");
        Console console = System.console();
        String password = String.valueOf(console.readPassword());

        userLoginDTO.setUsername(username);
        userLoginDTO.setPassword(password);

        int ret = login();
        if(ret == 0)
        {
            displayed.set(false);
            sessionUsername = username;
            isModerator = 0;
        } else if(ret == 6){
            displayed.set(false);
            sessionUsername = username;
            isModerator = 1;
        } else{
            displayed.set(true);
        }
        return ret;
    }

    /** Asks the user to fill the registration form
     *
     * @return 4 if the registration failed for any reason
     * 5 if the registration succeded
     */
    private int askRegistrationData(AtomicBoolean displayed)
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("username?");
        userRegistrationDTO.setUsername(sc.nextLine());
        System.out.println("Name?");
        userRegistrationDTO.setName(sc.nextLine());
        System.out.println("Surname?");
        userRegistrationDTO.setSurname(sc.nextLine());
        System.out.println("Email address?");
        userRegistrationDTO.setEmail(sc.nextLine());
        System.out.println("Date of birth? YYYY-MM-DD");
        try{
            userRegistrationDTO.setDateOfBirth(Utils.convertStringToLocalDate(sc.nextLine()));
        }catch(DateTimeParseException e){
            displayed.set(true);
            return 4; // Going back to menù
        }
        System.out.println("Preferred language?");
        String preferredLanguage = sc.nextLine();
        System.out.println("Choose your password:");
        Console console = System.console();
        String password = String.valueOf(console.readPassword());
        userRegistrationDTO.setPassword(password);

        int ret = register(preferredLanguage);
        if(ret == 4)
            displayed.set(true);
        if(ret == 5)
        {
            displayed.set(false);
            sessionUsername = userRegistrationDTO.getUsername();
        }
        return ret;
    }

    public UnregisteredUserController()
    {
        unregisteredMenu = new UnregisteredMenu();
        userLoginDTO = new UserLoginDTO();
        userRegistrationDTO = new UserRegistrationDTO();
        this.registeredUserService = ServiceLocator.getRegisteredUserService();
        this.isModerator = -1;
        this.gameService = ServiceLocator.getGameService();
        this.gameController = new GameController(unregisteredMenu.getDisplayed());
        this.statisticsController = new StatisticsController();
        functionsUnregistered = new Runnable[]{
                () -> {//0
                    errorCode = askCredentials(unregisteredMenu.getDisplayed());
                },
                () -> {//1
                    errorCode = askRegistrationData(unregisteredMenu.getDisplayed());
                },
                () -> {//2
                    errorCode = gameController.askGameQueryByName();
                },
                () -> {//3 Most active reviewers
                    unregisteredMenu.getDisplayed().set(false);
                    statisticsController.findTopReviewersByPostCountLastMonth(unregisteredMenu.getDisplayed());
                },
                () -> {//4 Top rated games
                    System.exit(0);
                },
                () -> {//5
                    System.exit(0);
                }
        };
    }


    public String getUsername()
    {
        return sessionUsername;
    }


    /**
     * Shows the dropdown and after the user selection proceeds
     * to execute the proper functionality
     */
    public int showUnregisteredDropdown()
    {
        /* This condition checks if the login has been successful
        by passing a reference to a boolean wrapper
        When displayed = false, it means login successful and stop looping
         */
        String welcomeMessage = "";
        int index = -1;
        String messageText = "";
        while(unregisteredMenu.getDisplayed().get())
        {
            switch(errorCode)
            {

                case 0: // No errors
                    break;
                case 1:
                    messageText = "Connection error";
                    break;
                case 2:
                    messageText = "Login failed: wrong username";
                    break;
                case 3:
                    messageText = "Login failed: wrong password";
                    break;
                case 4:
                    messageText = "Registration process: failed";
                    break;
                case 5:
                    messageText = "Registration process: success";
                    break;
                default:
                    messageText = welcomeMessage;
            }
            index = unregisteredMenu.displayMenu(messageText);
            functionsUnregistered[index].run();
        }
        return errorCode;
    }

}
