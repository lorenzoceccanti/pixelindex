package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.service.GameService;
import it.unipi.largescale.pixelindex.service.ModeratorService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;

import java.security.Provider;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ApplicationController {

    public ApplicationController(){
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
        try{
            moderatorService.banUser("ale1968");
            consistencyThread.addTask(() -> {
                try {
                    moderatorService.deleteUserFromGraph("ale1968");
                    System.out.println("Consistency insertion SUCCEDED");
                } catch (ConnectionException e) {
                    System.out.println("Consistency insertion FAILED");
                    return;
                }
            });
        }catch(ConnectionException ex)
        {
            System.out.println("Document DB removal failed");
        }

        consistencyThread.stopThread();
        // display_choices()

        // utente conferma roba

        // parte il servizio confermato

    }
}
