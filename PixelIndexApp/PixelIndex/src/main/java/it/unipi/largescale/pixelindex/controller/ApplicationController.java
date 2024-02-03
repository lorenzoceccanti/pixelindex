package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.service.GameService;
import it.unipi.largescale.pixelindex.service.ModeratorService;
import it.unipi.largescale.pixelindex.service.RegisteredUserService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;
import it.unipi.largescale.pixelindex.view.options.UnregisteredMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ApplicationController {

    private void showLogin() {
        System.out.println("Insert your login data");
        // Scanner sc = new Scanner(System)
    }

    public ApplicationController() {

        ListSelector notRegisteredSel = new ListSelector("Welcome to PixelIndex!");
        UnregisteredMenu unregisteredMenu = new UnregisteredMenu();

        notRegisteredSel.addOptions(unregisteredMenu.getOptions(), "unregistered_menu", "Make your choce");
        int index = notRegisteredSel.askUserInteraction("unregistered_menu");
        Map<Integer, Runnable> hanlderMap = new HashMap<>();
        RegisteredUserService userService = ServiceLocator.getRegisteredUserService();
        Runnable[] functionsArray = new Runnable[]{
                //  showLogin,

        };
        functionsArray[index].run();
        // int[] n = new int[2]
        /*for (int i = 0; i < unregisteredMenu.optionNumber(); i++) {
            handlerMap.put(i, )
        }*/

        /*switch (index) {
            case 0:

        }*/
        // int notRegisteredSel = notRegisteredSel.askUserInteraction("unregistered_menu");

        // notRegisteredSel.askUserInteraction();

        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        ConsistencyThread consistencyThread = new ConsistencyThread(taskQueue);
        consistencyThread.start();
        GameService gameService = ServiceLocator.getGameService();

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
        */

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

    }
}
