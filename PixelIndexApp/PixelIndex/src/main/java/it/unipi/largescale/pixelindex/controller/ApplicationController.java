package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.AuthUserDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.exceptions.UserNotFoundException;
import it.unipi.largescale.pixelindex.exceptions.WrongPasswordException;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.view.dropdown.RegisteredMenu;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;
import it.unipi.largescale.pixelindex.view.dropdown.UnregisteredMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ApplicationController {
    private UnregisteredMenu unregisteredMenu;
    private LoginController loginController;
    private UserController userController;
    private RegisteredMenu registeredMenu;
    private Runnable[] functionsUnregistered;
    private Runnable[] functionsRegistered;
    private String sessionUsername;
    private int loginOutcome = -1;


    public ApplicationController() {

        unregisteredMenu = new UnregisteredMenu();
        loginController = new LoginController();
        functionsUnregistered = new Runnable[]{
                () -> {
                    loginOutcome = loginController.askCredentials(unregisteredMenu.getDisplayed());
                },
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {System.exit(0);}
        };

        int ret = showUnregisteredDropdown();
        registeredMenu = new RegisteredMenu();
        functionsRegistered = new Runnable[]{
                () -> {
                    sessionUsername = loginController.getUsername();
                    registeredMenu.displayMenu(sessionUsername);
                },
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {System.exit(0);}
        };
        functionsRegistered[ret].run();
    }

    /**
     *
     * @return The index corresponding to the choice made
     * -1 in case of errors
     */
    public int showUnregisteredDropdown()
    {
        /* This condtion checks if the login has been successful
        by passing a reference to a boolean wrapper
        When displayed = false, it means login successful and stop looping
         */

        int index = -1;
        String messageText = "";
        while(unregisteredMenu.getDisplayed().get())
        {
            switch(loginOutcome)
            {
                case 0:
                    break;
                case 1:
                    messageText = "Login failed: connection error";
                    break;
                case 2:
                    messageText = "Login failed: wrong username";
                    break;
                case 3:
                    messageText = "Login failed: wrong password";
                    break;
                default:
                    messageText = "Welcome to PixelIndex";
            }
            index = unregisteredMenu.displayMenu(messageText);
            functionsUnregistered[index].run();
        }
        return index;
    }

        /*
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        ConsistencyThread consistencyThread = new ConsistencyThread(taskQueue);
        consistencyThread.start();
        GameService gameService = ServiceLocator.getGameService();
         */

        // Prova inserimento gioco con consistency

        /*
        Game game = new Game();
        game.setName("GREZZO 3");
        game.setSummary("Grezzo 2 sequel");
        game.setCategory("main_game");
        try {
            game.setId(gameService.insertGameOnDocument(game));
            consistencyThread.addTask(() -> {
                try {
                    gameService.insertGameOnGraph(game);
                    System.out.println("Consistency insertion SUCCEDED");
                } catch (ConnectionException e) {
                    System.out.println("Consistency insertion FAILED");
                    return;
                }

                consistencyThread.addTask(() -> {
                    System.out.println("MongoDB updated");
                });
            });
        } catch(ConnectionException e) {
            System.out.println("Document DB insertion FAILED");
        }


        // Prova ban in eventual consistency

        ModeratorService moderatorService = ServiceLocator.getModeratorService();
        try {
            moderatorService.banUser("ale1968");
            consistencyThread.addTask(() -> {
                try {
                    moderatorService.deleteUserFromGraph("ale1968");
                    System.out.println("Ban: consistency SUCCEDED");
                } catch (ConnectionException e) {
                    System.out.println("Ban: consistency FAILED");
                    return;
                }
            });
        } catch (ConnectionException ex) {
            System.out.println("Document DB removal failed");
        }

        consistencyThread.stopThread();
        // display_choices()

        // utente conferma roba

        // parte il servizio confermato
        */
}
