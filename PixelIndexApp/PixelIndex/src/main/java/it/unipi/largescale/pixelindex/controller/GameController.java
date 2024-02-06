package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.service.GameService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameController{
    private List<GamePreviewDTO> searchResult;
    private ArrayList<String> rows;
    private GameService gameService;
    private int rowSelection;
    private int pageSelection;
    private int totalPages;
    private String queryName;

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
        rows.add(AnsiColor.ansiYellow()+"Previous page"+AnsiColor.ansiReset());
        rows.add(AnsiColor.ansiYellow()+"Next page"+AnsiColor.ansiReset());
        rows.add(AnsiColor.ansiYellow()+"Go back"+AnsiColor.ansiReset());
        searchResult.stream().forEach(gamePreviewDTO -> {
            rows.add(gamePreviewDTO.toString());
        });
    }

    /** Returns 1 if there have been connection errors,
     * 0 if not error occoured
     *
     */
    public int askGameQueryByName(){
        ListSelector ls = new ListSelector("Query result");
        int result; int pageSelection = 0;
        int exit = 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("Query?");
        queryName = sc.nextLine();
        do{
            Utils.clearConsole();
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
                    pageSelection = pageSelection > 0 ? --pageSelection : pageSelection;
                    break;
                case 1: // Next page
                    pageSelection = pageSelection < totalPages-1 ? ++pageSelection : pageSelection;
                    break;
                case 2:
                    // qualcosa per andare al menù precedente
                    exit = 1;
                    break;
                default:
                    // Avrò scelto un gioco
                    // chiamo la query per la scelta del gioco
                    break;
            }
        }while(exit != 1);
        return result;
    }
    public GameController()
    {
        this.gameService = ServiceLocator.getGameService();
        this.rows = new ArrayList<>();
        this.rowSelection = 0;
        this.pageSelection = 0;
        this.totalPages = 3;
    }
}
