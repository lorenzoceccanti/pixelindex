package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.service.GameService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController{
    private List<GamePreviewDTO> searchResult;
    private ArrayList<String> rows;
    private GameService gameService;
    private AtomicBoolean menuDisplayed;
    private int rowSelection;
    private int pageSelection;
    private int totalPages;
    private String queryName;
    private int exitGameList;

    /** Returns 1 if there have been connection errors,
     * 0 if not error occoured
     *
     * @param queryName The query search
     * @param page The page to be displayed, for example
     * if you are interested in the elements n.10, ..., 19
     * you will pass 1 to the page parameter
     */
    private int gameByName(String queryName, int page)
    {
        try{
            searchResult = gameService.search(queryName, page);
            return 0;
        }catch(ConnectionException ex)
        {
            ex.getMessage();
            return 1;
        }
    }

    private void displayGames(){
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW+"Previous page"+AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW+"Next page"+AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW+"Go back"+AnsiColor.ANSI_RESET);
        searchResult.stream().forEach(gamePreviewDTO -> {
            rows.add(gamePreviewDTO.toString());
        });
        /* Introdurre qui un eventuale conto del numero totale di giochi*/
    }


    private int viewGameDetail(int indexView){
        /* Sfasamento di 2 posizioni in avanti
        dovuto ai primi 3 pulsanti
         */
        GamePreviewDTO gamePreviewDTO = searchResult.get(indexView-3);
        Game g; ListSelector ls = new ListSelector("Game details:");
        ArrayList<String> opt = new ArrayList<>();
        opt.add("Show top 10 most relevant reviews");
        opt.add("Go back");
        // Making the query for get all the details of that specific game
        try{
            g = gameService.getGameById(gamePreviewDTO.getId());
            System.out.println(g);
            ls.addOptions(opt, "gameDetailsDropdown", "Please select");
            int sel = ls.askUserInteraction("gameDetailsDropdown");
            if(sel == 1){
                // Go Back
                exitGameList = 0;
                askGameQueryByName();
            } else {
                // Showing reviews

            }
            return 0;
        }catch(ConnectionException ex)
        {
            return 1;
        }
    }

    /** Returns 1 if there have been connection errors,
     * 0 if not error occoured,
     * 6 if there's the need to go back to the menù
     *
     */
    public int askGameQueryByName(){
        int result; int pageSelection = 0;

        if(queryName.isEmpty())
        {
            Scanner sc = new Scanner(System.in);
            System.out.println("Query?");
            queryName = sc.nextLine();
        }
        do{
            Utils.clearConsole();
            ListSelector ls = new ListSelector("Query result");
            System.out.println("Page displayed: " + (pageSelection + 1));
            result = gameByName(queryName, pageSelection);
            if(result != 0)
                break;
            displayGames();
            ls.addOptions(rows, "searchGameByName", "Press enter to view game details");
            int choice = ls.askUserInteraction("searchGameByName");
            switch(choice)
            {
                case 0: // Previous page
                    exitGameList = 0;
                    menuDisplayed.set(false);
                    pageSelection = pageSelection > 0 ? --pageSelection : pageSelection;
                    break;
                case 1: // Next page
                    exitGameList = 0;
                    menuDisplayed.set(false);
                    pageSelection = pageSelection < totalPages-1 ? ++pageSelection : pageSelection;
                    break;
                case 2: // Go back
                    menuDisplayed.set(true);
                    exitGameList = 1;
                    queryName = "";
                    break;
                default: // Game selection
                    menuDisplayed.set(false);
                    exitGameList = 1;
                    viewGameDetail(choice);
                    break;
            }
        }while(exitGameList != 1);
        return result;
    }
    public GameController(AtomicBoolean inMenu)
    {
        this.gameService = ServiceLocator.getGameService();
        this.queryName = "";
        this.rows = new ArrayList<>();
        this.menuDisplayed = inMenu;
        this.rowSelection = 0;
        this.pageSelection = 0;
        this.totalPages = 3;
    }
}
