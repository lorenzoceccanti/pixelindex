package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.service.GameService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.utils.Utils;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.Console;
import java.lang.annotation.Native;
import java.util.List;
import java.util.Scanner;

public class GameController implements NativeKeyListener {
    private List<GamePreviewDTO> searchResult;
    private GameService gameService;
    private int rowSelection;
    private int pageSelection;
    private int totalPages;
    private String queryName;
    private void clearConsole(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    @Override
    public void nativeKeyPressed(NativeKeyEvent e)
    {
        if(e.getKeyCode() == NativeKeyEvent.VC_DOWN)
        {
            // Moving the cursor down if the arrow down pressed
            if(rowSelection < (searchResult.size()-1)){
                rowSelection++;
            }
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_UP)
        {
            // Moving the cursor up it the arrow up pressed
            if(rowSelection > 0)
                rowSelection--;
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_LEFT)
        {
            // Moving to the previous page if the left key is pressed
            if(pageSelection > 0)
            {
                pageSelection--;
                rowSelection = 0;
            }

        }
        if(e.getKeyCode() == NativeKeyEvent.VC_RIGHT)
        {
            if(pageSelection < totalPages)
            {
                pageSelection++;
                rowSelection = 0;
            }

        }
        if(e.getKeyCode() == NativeKeyEvent.VC_Q)
        {
            Utils.stopTrackingKeyboard(this);
        }
    }
    @Override
    public void nativeKeyReleased(NativeKeyEvent e)
    {
        if(e.getKeyCode() == NativeKeyEvent.VC_DOWN)
        {
            searchResult.get(rowSelection).setCursorSelection(true);
            searchResult.get(rowSelection-1).setCursorSelection(false);
            clearConsole();
            displayGames();
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_UP)
        {
            searchResult.get(rowSelection).setCursorSelection(true);
            searchResult.get(rowSelection+1).setCursorSelection(false);
            clearConsole();
            displayGames();
        }
        if(e.getKeyCode() == NativeKeyEvent.VC_LEFT
        || e.getKeyCode() == NativeKeyEvent.VC_RIGHT)
        {
            // Change page: issue again the query
            if(pageSelection > 0 && pageSelection < totalPages){
                clearConsole();
                gameByName(queryName, pageSelection);
                displayGames();
            }
        }

    }
    @Override
    public void nativeKeyTyped(NativeKeyEvent arg0)
    {
        Console console = System.console();
        // In order to disable the echo of the key pressed, like we have for passwords
        char[] buf = console.readPassword();
    }

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
            searchResult.get(0).setCursorSelection(true);
            return 0;
        }catch(ConnectionException ex)
        {
            ex.getMessage();
            return 1;
        }
    }

    private void displayGames(){
        searchResult.stream().forEach(gamePreviewDTO -> {
            System.out.println(gamePreviewDTO);
        });
        System.out.println("Press arrow keys to move");
        System.out.println("Press enter to expand the game");
        System.out.println("Press B to go back");
    }

    /** Returns 1 if there have been connection errors,
     * 0 if not error occoured
     *
     */
    public int askGameQueryByName(){
        int result;
        Scanner sc = new Scanner(System.in);
        System.out.println("Query?");
        queryName = sc.nextLine();
        result = gameByName(queryName, 0);
        if(result == 0)
        {
            displayGames();
            Utils.startTrackingKeyboard(this);
        }
        return result;
    }
    public GameController()
    {
        this.gameService = ServiceLocator.getGameService();
        this.rowSelection = 0;
        this.pageSelection = 0;
        this.totalPages = 3;
    }
}
