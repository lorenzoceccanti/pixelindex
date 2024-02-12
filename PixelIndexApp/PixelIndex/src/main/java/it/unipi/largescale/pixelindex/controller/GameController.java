package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.dto.TrendingGamesDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.model.RatingKind;
import it.unipi.largescale.pixelindex.model.Review;
import it.unipi.largescale.pixelindex.service.*;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;
import jline.internal.Ansi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController{
    private List<GamePreviewDTO> searchResult;
    private ArrayList<String> rows;
    private GameService gameService;
    private LibraryService libraryService;
    private ReviewService reviewService;
    private WishlistService wishlistService;
    private AtomicBoolean menuDisplayed;
    private ReviewController reviewController;
    private ConsistencyThread consistencyThread;
    private int rowSelection;
    private String queryName;
    private String sessionUsername;
    private LocalDate dateOfBirth;
    private boolean isModerator;
    private int exitGameList;
    private int exitGameDetails;
    public void trendingGamesChart(){
        ArrayList<String> o = new ArrayList<>();
        o.add("Go back");
        System.out.println("Which year?");
        Scanner sc = new Scanner(System.in);
        int year = sc.nextInt();
        try{
            List<TrendingGamesDTO> list = gameService.getTrendingGames(year,10);
            System.out.println("*** TRENDING GAMES CHART ***");
            if(list.isEmpty())
                System.out.println("Trending chart empty.");
            for(int i=0; i<list.size(); i++)
                System.out.print(list.get(i));
            System.out.println("");
        }catch(ConnectionException ex)
        {
            System.out.println("Connection to Neo4J lost");
        }
        ListSelector ls = new ListSelector("");
        ls.addOptions(o,"backTrendingGames","Make your choice");
        int choice = ls.askUserInteraction("backTrendingGames");
        if(choice == 0)
            menuDisplayed.set(true);
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
        if(rows.size() <= 3){
            System.out.println("*** Empty ***");
        }
    }

    private void formNewReview(String gameId, String gameName, Integer gameReleaseYear){
        ListSelector ls = new ListSelector("*** Inserting new review ***");
        ArrayList<String> opt = new ArrayList<>();
        opt.add("Yes");
        opt.add("No");
        opt.add("I don't know");
        ls.addOptions(opt, "newReviewDropDown", "Do you recommend this game?");
        int choice = ls.askUserInteraction("newReviewDropDown");
        System.out.println("Write something: (end with enter)");
        Scanner sc = new Scanner(System.in);
        String text = sc.nextLine();

        Review r = new Review();
        if(choice == 0)
            r.setRating(RatingKind.RECOMMENDED);
        else if(choice == 1)
            r.setRating(RatingKind.NOT_RECOMMENDED);
        else
            r.setRating(RatingKind.NOT_AVAILABLE);
        r.setText(text);
        r.setAuthor(sessionUsername);
        r.setGameId(gameId);
        r.setTimestamp(LocalDateTime.now());

        try{
            reviewService.insertReview(r,gameName, gameReleaseYear);
        }catch (ConnectionException ex){
            ex.printStackTrace();
        }
    }

    public int viewGameDetail(Integer exitGameDetails, GamePreviewDTO gamePreviewDTO, boolean fromLibrary){
        /* Sfasamento di 3 posizioni in avanti
        dovuto ai primi 3 pulsanti dell'anteprima dei giochi
         */
        int addToLibrarySts = -1; String message = "";
        Game g; ListSelector ls = new ListSelector("Game details:");
        ArrayList<String> opt = new ArrayList<>();
        opt.add("Show top 10 most relevant reviews");
        opt.add("Add this game to library");
        opt.add("Remove this game from library");
        opt.add("Add this game to wishlist");
        opt.add("Remove this game from wishlist");
        opt.add("Add review");
        opt.add("Go back");
        // Making the query for get all the details of that specific game

        do{
            String gameId = gamePreviewDTO.getId();
            String gameName = gamePreviewDTO.getName();
            Integer gameReleaseYear = gamePreviewDTO.getReleaseYear();
            try{
                Utils.clearConsole();
                System.out.println((addToLibrarySts == -1)?"":message);
                g = gameService.getGameById(gameId);
                System.out.println(g);
                ls.addOptions(opt, "gameDetailsDropdown", "Please select");
                int sel = ls.askUserInteraction("gameDetailsDropdown");
                switch(sel)
                {
                    case 0:
                        addToLibrarySts = -1;
                        reviewController.displayExcerpt(gameId, exitGameDetails);
                        break;
                    case 1:
                        if(sessionUsername.isEmpty()){
                            addToLibrarySts = 1;
                            message = "Unauthorized";
                        } else {
                            // Add to library
                            addToLibrarySts = libraryService.addGame(sessionUsername, gamePreviewDTO);
                            message = "Game successfully added to library";
                        }
                        break;
                    case 2:
                        if(sessionUsername.isEmpty()){
                            addToLibrarySts = 1;
                            message = "Unauthorized";
                        } else {
                            // Remove from library
                            addToLibrarySts = libraryService.removeGame(sessionUsername, gamePreviewDTO);
                            message = "Game successfully removed from library";
                        }
                        break;
                    case 3:
                        if(sessionUsername.isEmpty()){
                            addToLibrarySts = 1;
                            message = "Unauthorized";
                        } else {
                            // Add to wishlist
                            addToLibrarySts = wishlistService.addGame(sessionUsername, gamePreviewDTO.getId());
                            message = "Game successfully added to wishlist";
                        }
                        break;
                    case 4:
                        if(sessionUsername.isEmpty()){
                            addToLibrarySts = 1;
                            message = "Unauthorized";
                        } else {
                            // Remove from whishlist
                            addToLibrarySts = wishlistService.removeGame(sessionUsername, gamePreviewDTO.getId());
                            message = "Game successfully removed from wishlist";
                        }
                        break;
                    case 5:
                        if(sessionUsername.isEmpty()){
                            addToLibrarySts = 1;
                            message = "Unauthorized";
                        } else {
                            // Add new review
                            formNewReview(gameId, gameName, gameReleaseYear);
                            addToLibrarySts = 1;
                            message = "Review successfully added";
                        }
                        break;
                    case 6:
                        // Go back
                        addToLibrarySts = -1;
                        exitGameDetails = 1;
                        exitGameList = 0;
                        if(!fromLibrary)
                            askGameQueryByName();
                        break;
                    default:
                        break;
                }
            }catch(ConnectionException ex){
                return 1;
            }
        }while(exitGameDetails != 1);
        return 0;
    }

    /** Returns 1 if there have been connection errors,
     * 0 if not error occoured,
     * 6 if there's the need to go back to the menù
     *
     */
    public int askGameQueryByName(){
        int result; int pageSelection = 0;
        String message = "";

        if(queryName.isEmpty())
        {
            Scanner sc = new Scanner(System.in);
            System.out.println("Query? Possibile syntax:");
            System.out.println(AnsiColor.ANSI_CYAN+"<name>"+AnsiColor.ANSI_RESET);
            System.out.println(AnsiColor.ANSI_CYAN+"<name> [-c <company> | -p <platform> | -y <releaseYear>]"+AnsiColor.ANSI_RESET);
            queryName = sc.nextLine();
        }
        do{
            Utils.clearConsole();
            System.out.println(message);
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
                    pageSelection = (rows.size() > 3) ? ++pageSelection : pageSelection;
                    break;
                case 2: // Go back
                    menuDisplayed.set(true);
                    exitGameList = 1;
                    queryName = "";
                    break;
                default: // Game selection
                    menuDisplayed.set(false);
                    GamePreviewDTO gamePreviewDTO = searchResult.get(choice-3);
                    if(!isModerator || gamePreviewDTO.getPegiRating() != null)
                    {
                        // Has PEGI rating
                        if(sessionUsername.isEmpty()){
                            message = AnsiColor.ANSI_RED+"[Warning]:"+AnsiColor.ANSI_RESET+" This game is age-restricted. Please login";
                            exitGameList = 0;
                        }else{
                            boolean restricted = Utils.isAgeRestricted(this.dateOfBirth, gamePreviewDTO.getPegiRating());
                            if(!restricted){
                                exitGameList = 1;
                                viewGameDetail(0, gamePreviewDTO, false);
                            } else {
                                message = AnsiColor.ANSI_RED+"[Warning]:"+AnsiColor.ANSI_RESET+" This game has been restricted for the users of your age";
                            }
                        }
                    } else {
                        // Does not have PEGI rating
                        exitGameList = 1;
                        viewGameDetail(0, gamePreviewDTO, false);
                    }
                    break;
            }
        }while(exitGameList != 1);
        return result;
    }
    public GameController(AtomicBoolean inMenu)
    {
        this.gameService = ServiceLocator.getGameService();
        this.reviewController = new ReviewController();
        this.queryName = "";
        this.sessionUsername = "";
        this.isModerator = false;
        this.rows = new ArrayList<>();
        this.menuDisplayed = inMenu;
        this.rowSelection = 0;
    }
    public GameController(AtomicBoolean inMenu, String sessionUsername, LocalDate dateOfBirth, boolean isModerator, ConsistencyThread consistencyThread)
    {
        this.gameService = ServiceLocator.getGameService();
        this.libraryService = ServiceLocator.getLibraryService();
        this.wishlistService = ServiceLocator.getWishlistService();
        this.reviewService = ServiceLocator.getReviewService();
        this.sessionUsername = sessionUsername;
        this.isModerator = isModerator;
        this.dateOfBirth = dateOfBirth;
        this.reviewController = new ReviewController(sessionUsername, isModerator, consistencyThread);
        this.queryName = "";
        this.rows = new ArrayList<>();
        this.menuDisplayed = inMenu;
        this.rowSelection = 0;
    }
}
