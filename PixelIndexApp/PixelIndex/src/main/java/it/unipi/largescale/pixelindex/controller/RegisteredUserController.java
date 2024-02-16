package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.GameLibraryElementDTO;
import it.unipi.largescale.pixelindex.dto.GamePreviewDTO;
import it.unipi.largescale.pixelindex.dto.GameSuggestionDTO;
import it.unipi.largescale.pixelindex.dto.UserSearchDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.service.*;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.dropdown.RegisteredMenu;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisteredUserController {
    ConsistencyThread consistencyThread;
    private RegisteredMenu registeredMenu;
    private Runnable[] functionsRegistered;
    private String sessionUsername;
    private LocalDate dateOfBirth;
    private GameController gameController;
    private StatisticsController statisticsController;
    private LibraryService libraryService;
    private WishlistService wishlistService;
    private RegisteredUserService registeredUserService;
    private SuggestionsService suggestionsService;
    private String queryName;
    ArrayList<UserSearchDTO> userSearchDTOs;
    List<String> potentialUsersToFollow;
    List<GameSuggestionDTO> potentialGames;
    List<GameLibraryElementDTO> gameLibraryElementDTOS;
    List<GamePreviewDTO> gameWishlistDTOs;
    ArrayList<String> rows = new ArrayList<>();
    private boolean isModerator;

    private void displayUsers() {
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW + "Previous page" + AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW + "Next page" + AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW + "Go back" + AnsiColor.ANSI_RESET);
        userSearchDTOs.stream().forEach(userSearchDTO -> {
            rows.add(userSearchDTO.toString());
        });
        if (rows.size() <= 3) {
            System.out.println("*** Empty ***");
        }
    }

    /**
     * Returns 1 if there have been connection errors,
     * 0 if not error occoured
     *
     * @param username The string inserted
     */
    private int usersByName(String username, int page) {
        try {
            userSearchDTOs = registeredUserService.searchUser(username, page);
            return 0;
        } catch (ConnectionException ex) {
            return 1;
        }
    }

    private int pressFollow(String username, StringBuilder message) {
        // Checking if the user is following by itself
        if (sessionUsername.equals(username)) {
            message.replace(0, message.toString().length(), "[Message]: Cannot follow by yourself!");
            return 1;
        }
        try {
            String operation = registeredUserService.followUser(sessionUsername, username, consistencyThread);
            message.replace(0, message.toString().length(), "[Operation]: " + operation);
            return 0;
        } catch (ConnectionException ex) {
            return 1;
        }
    }

    private int reportUser(String username, StringBuilder message) {
        // Check if the user is reporting by itself
        if (sessionUsername.equals(username)) {
            message.replace(0, message.toString().length(), "[Message]: Cannot report by yourself!");
            return 1;
        }
        try {
            int sts = registeredUserService.reportUser(sessionUsername, username);
            if (sts == 0)
                message.replace(0, message.toString().length(), "[Operation]: " + AnsiColor.ANSI_YELLOW + username + AnsiColor.ANSI_RESET + " reported successfully");
            else if (sts == -1)
                message.replace(0, message.toString().length(), "[Message]: Cannot report a moderator!");
            return 0;
        } catch (ConnectionException ex) {
            return 1;
        }
    }

    public int askSearchByUsernameQuery(AtomicBoolean regMenuDisplayed) {
        int pageSelection = 0;
        int result = 1;
        int choice = -1;
        int exit = 0;
        regMenuDisplayed.set(false);
        StringBuilder followMessage = new StringBuilder();

        if (queryName.isEmpty()) {
            System.out.println("Query?");
            Scanner sc = new Scanner(System.in);
            queryName = sc.nextLine();
        }
        do {
            Utils.clearConsole();
            System.out.println(followMessage);
            ListSelector ls = new ListSelector("Query result");
            System.out.println("Page displayed: " + (pageSelection + 1));
            result = usersByName(queryName, pageSelection);
            if (result != 0)
                return result;
            displayUsers();
            ls.addOptions(rows, "searchUserByUsername", "Select an user:");
            choice = ls.askUserInteraction("searchUserByUsername");
            switch (choice) {
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
                    // To access the userSearchDTOs access with an index decreased by 3
                    UserSearchDTO u = userSearchDTOs.get(choice - 3);
                    ListSelector ls1 = new ListSelector("User selected: " + AnsiColor.ANSI_YELLOW + u.getUsername() + AnsiColor.ANSI_RESET);
                    ArrayList<String> userOpt = new ArrayList<>();
                    userOpt.add(AnsiColor.ANSI_BLUE + "Edit your follow" + AnsiColor.ANSI_RESET);
                    userOpt.add(AnsiColor.ANSI_RED + "Report" + AnsiColor.ANSI_RESET);
                    userOpt.add(AnsiColor.ANSI_YELLOW + "Go back" + AnsiColor.ANSI_RESET);
                    ls1.addOptions(userOpt, "selectedUserDrop", "Make your choice");
                    int sel = ls1.askUserInteraction("selectedUserDrop");
                    if (sel == 0)
                        pressFollow(u.getUsername(), followMessage);
                    else if(sel == 1)
                        reportUser(u.getUsername(), followMessage);
                    exit = 0;
            }
        } while (exit != 1);
        return result;
    }

    public int usersYouMightFollow(AtomicBoolean inMenu) {
        List<String> rows = new ArrayList<>();
        ListSelector ls = new ListSelector("");
        ArrayList<String> opt = new ArrayList<>();
        opt.add("Go back");
        inMenu.set(false);
        try {
            potentialUsersToFollow = suggestionsService.suggestUsers(sessionUsername);
            rows.add("Users you might follow:");
            if (potentialUsersToFollow.isEmpty())
                rows.add("*** List empty ***");
            rows.addAll(potentialUsersToFollow);
            for (String row : rows) System.out.println(row);
            ls.addOptions(opt, "usersYouMightFollow", "Make your choice");
            int choice = ls.askUserInteraction("usersYouMightFollow");
            if (choice == 0) {
                inMenu.set(true);
            }
            return 0;
        } catch (ConnectionException ex) {
            return 1;
        }
    }

    public int getSuggestedGames(AtomicBoolean inMenu) {
        ListSelector ls = new ListSelector("");
        ArrayList<String> opt = new ArrayList<>();
        opt.add("Go back");
        inMenu.set(false);
        try {
            potentialGames = suggestionsService.suggestGames(sessionUsername);
            System.out.println("Games you might like:");
            if (potentialGames.isEmpty())
                System.out.println("*** List empty ***");
            for (int i = 0; i < potentialGames.size(); i++)
                System.out.println(potentialGames.get(i));
            System.out.println("");
            ls.addOptions(opt, "suggestionGames", "Make your choice");
            int choice = ls.askUserInteraction("suggestionGames");
            if (choice == 0) {
                inMenu.set(true);
            }
            return 0;
        } catch (ConnectionException ex) {
            return 1;
        }
    }

    private int queryUserLibrary(String username, int page) {
        try {
            gameLibraryElementDTOS = libraryService.getGames(username, page);
            return 0;
        } catch (ConnectionException ex) {
            return 1;
        }
    }

    private void buildUserLibrary() {
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW + "Previous page" + AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW + "Next page" + AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW + "Go back" + AnsiColor.ANSI_RESET);
        gameLibraryElementDTOS.stream().forEach(GameLibraryElementDTO -> {
            rows.add(GameLibraryElementDTO.toString());
        });
        if (rows.size() <= 3) {
            System.out.println("*** Empty ***");
        }
    }

    public int displayLibrary(AtomicBoolean regMenuDisplayed) {
        int pageSelection = 0;
        int result = 1;
        int choice = -1;
        int exit = 0;
        regMenuDisplayed.set(false);
        do {
            Utils.clearConsole();
            ListSelector ls = new ListSelector("Your library:");
            System.out.println("Page displayed: " + (pageSelection + 1));
            result = queryUserLibrary(sessionUsername, pageSelection);
            if (result != 0)
                return result;
            buildUserLibrary();
            ls.addOptions(rows, "libraryDropdown", "Press enter to view game details");
            choice = ls.askUserInteraction("libraryDropdown");
            switch (choice) {
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
                    // Viewing game details
                    GamePreviewDTO gamePreviewDTO = gameLibraryElementDTOS.get(choice - 3);
                    gameController.viewGameDetail(0, gamePreviewDTO, true); // qui la funzione deve capire di essere chiamata dalla library
                    break;
            }
        } while (exit != 1);
        return result;
    }

    private int queryUserWishlist(String username, int page) {
        try {
            gameWishlistDTOs = wishlistService.getGames(username, page);
            return 0;
        } catch (ConnectionException ex) {
            return 1;
        }
    }

    private void buildUserWishlist() {
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW + "Previous page" + AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW + "Next page" + AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW + "Go back" + AnsiColor.ANSI_RESET);
        gameWishlistDTOs.stream().forEach(gameWishlist -> {
            rows.add(gameWishlist.toString());
        });
        if (rows.size() <= 3) {
            System.out.println("*** Empty ***");
        }
    }

    public int displayWishlist(AtomicBoolean regMenuDisplayed) {
        int pageSelection = 0;
        int result = 1;
        int choice = -1;
        int exit = 0;
        regMenuDisplayed.set(false);
        do {
            Utils.clearConsole();
            ListSelector ls = new ListSelector("Your wishlist:");
            System.out.println("Page displayed: " + (pageSelection + 1));
            result = queryUserWishlist(sessionUsername, pageSelection);
            if (result != 0)
                return result;
            buildUserWishlist();
            ls.addOptions(rows, "wishlistDropdown", "Press enter to view game details");
            choice = ls.askUserInteraction("wishlistDropdown");
            switch (choice) {
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
                    // Viewing game details
                    GamePreviewDTO gamePreviewDTO = gameWishlistDTOs.get(choice - 3);
                    gameController.viewGameDetail(0, gamePreviewDTO, true);
                    break;
            }
        } while (exit != 1);
        return result;
    }

    private int showRegisteredDropdown(List<String> view) {
        int opt = -1;
        do {
            Utils.clearConsole();
            view.stream().forEach(elem -> {
                System.out.println(elem);
            });
            registeredMenu.getDisplayed().set(true);
            opt = registeredMenu.displayMenu(sessionUsername);
            functionsRegistered[opt].run();
            view.clear();
        } while (registeredMenu.getDisplayed().get());
        return opt;
    }

    public RegisteredUserController(String username, LocalDate dateOfBirth, boolean isModerator, ConsistencyThread consistencyThread) {
        this.queryName = "";
        this.consistencyThread = consistencyThread;
        this.potentialUsersToFollow = new ArrayList<>();
        this.potentialGames = new ArrayList<>();
        this.suggestionsService = ServiceLocator.getSuggestionsService();
        this.registeredUserService = ServiceLocator.getRegisteredUserService();
        this.libraryService = ServiceLocator.getLibraryService();
        this.wishlistService = ServiceLocator.getWishlistService();
        this.isModerator = isModerator;
        registeredMenu = new RegisteredMenu();
        this.sessionUsername = username;
        this.dateOfBirth = dateOfBirth;
        this.gameController = new GameController(registeredMenu.getDisplayed(), sessionUsername, this.dateOfBirth, this.isModerator, consistencyThread);
        this.statisticsController = new StatisticsController();
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
                    // View your wishlist
                    displayWishlist(registeredMenu.getDisplayed());
                },
                () -> {
                    registeredMenu.getDisplayed().set(false);
                    usersYouMightFollow(registeredMenu.getDisplayed());
                },
                () -> {
                    registeredMenu.getDisplayed().set(false);
                    statisticsController.findTopReviewersByReviewsCountLastMonth(registeredMenu.getDisplayed());
                },
                () -> {
                    registeredMenu.getDisplayed().set(false);
                    statisticsController.top10GamesByPositiveRatingRatio(registeredMenu.getDisplayed());
                },
                () -> {
                    registeredMenu.getDisplayed().set(false);
                    gameController.trendingGamesChart();
                },
                () -> {
                    registeredMenu.getDisplayed().set(false);
                    getSuggestedGames(registeredMenu.getDisplayed());
                },
                () -> {
                    consistencyThread.stopThread();
                    System.exit(0);
                }
        };
    }

    public RegisteredUserController(String username, LocalDate dateOfBirth, boolean isModerator){
        this(username, dateOfBirth, isModerator, new ConsistencyThread());
    }
    public void execute() {
        // After the login the thread starts
        consistencyThread.start();
        Utils.clearConsole();
        int index = showRegisteredDropdown(new ArrayList<>());
    }

}
