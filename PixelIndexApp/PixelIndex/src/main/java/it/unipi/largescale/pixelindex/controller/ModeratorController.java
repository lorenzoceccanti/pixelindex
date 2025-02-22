package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.UserReportsDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.service.GameService;
import it.unipi.largescale.pixelindex.service.ModeratorService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.service.StatisticsService;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.dropdown.ModeratorMenu;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class ModeratorController {
    ConsistencyThread consistencyThread = new ConsistencyThread();
    private final ArrayList<String> rows = new ArrayList<>();
    private final ModeratorMenu moderatorMenu;
    private final StatisticsService statisticsService;
    private ArrayList<UserReportsDTO> userReportsDTOS;
    private final ModeratorService moderatorService;
    private final Runnable[] functionsModerator;
    private final Runnable[] normalFunctions;
    private final GameService gameService;
    private final RegisteredUserController registeredUserController;
    private final GameController gameController;
    private final StatisticsController statisticsController;

    private void showSpecialDropdown(String str) {
        int opt;
        do {
            Utils.clearConsole();
            System.out.println(str);
            moderatorMenu.getSpecialDisplayed().set(true);
            opt = moderatorMenu.displaySpecialMenu();
            functionsModerator[opt].run();
        } while (moderatorMenu.getSpecialDisplayed().get());
    }

    private void showModeratorDropdown() {
        int opt;
        do {
            Utils.clearConsole();
            moderatorMenu.getDisplayed().set(true);
            opt = moderatorMenu.displayMenu();
            normalFunctions[opt].run();
        } while (moderatorMenu.getDisplayed().get());
    }

    private int queryReports() {
        try {
            userReportsDTOS = statisticsService.topNReportedUser(10);
            return 0;
        } catch (ConnectionException ex) {
            return 1;
        }
    }

    private void buildReport() {
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW + "Go back" + AnsiColor.ANSI_RESET);
        userReportsDTOS.forEach(userReport -> rows.add(userReport.toString()));
        if (rows.size() <= 1) {
            System.out.println("*** Empty ***");
        }
    }

    private int banUser(String username) {
        try {
            moderatorService.banUser(username, consistencyThread);
            return 0;
        } catch (ConnectionException ex) {
            return 1;
        }
    }

    private void displayReport() {
        int result;
        int choice;
        String messageDisp = "";
        moderatorMenu.getSpecialDisplayed().set(false);
        int exitReportList;
        do {
            Utils.clearConsole();
            System.out.println(messageDisp);
            ListSelector ls = new ListSelector("Most reported users:");
            result = queryReports();
            if (result != 0)
                return;
            buildReport();
            ls.addOptions(rows, "reportsDropdown", "Press enter to ban the user");
            choice = ls.askUserInteraction("reportsDropdown");
            if (choice == 0) {
                exitReportList = 1;
            } else {
                exitReportList = 0;
                // Ban the user
                UserReportsDTO user = userReportsDTOS.get(choice - 1);
                int sts = banUser(user.getUsername());
                messageDisp = (sts == 0) ? "User " + AnsiColor.ANSI_YELLOW + user.getUsername() + AnsiColor.ANSI_RESET + " banned successfully" : messageDisp;
            }
        } while (exitReportList != 1);
        moderatorMenu.getSpecialDisplayed().set(true);
        showSpecialDropdown("");
    }

    private void promptNewGameForm() {
        Game g = new Game();
        moderatorMenu.getSpecialDisplayed().set(false);
        System.out.println("*** INSERTING NEW GAME ***");
        Scanner sc = new Scanner(System.in);
        System.out.println("Specify name:");
        g.setName(sc.nextLine());
        System.out.println("Specify the release date [format YYYY-MM-DD]:");
        String dateAsString = sc.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse(dateAsString, formatter);
        g.setReleaseDate(ld);

        System.out.println("Specify the category:");
        g.setCategory(sc.nextLine());

        System.out.println("Specify the game modes [comma separated]:");
        String gameModesString = sc.nextLine();
        String[] gameModes = gameModesString.split(",");
        g.setGameModes(gameModes);


        System.out.println("Specify the genres [comma separated]:");
        String genresString = sc.nextLine();
        String[] tokens = genresString.split(",");
        String[] genres = tokens;
        g.setGenres(genres);

        System.out.println("Specify the platforms [comma separated]:");
        String platfromString = sc.nextLine();
        String[] platforms = platfromString.split(",");
        g.setPlatforms(platforms);

        System.out.println("Specify the companies [comma separated]:");
        String companiesString = sc.nextLine();
        tokens = companiesString.split(",");
        String[] companies = tokens;
        g.setCompanies(companies);

        System.out.println("Specify the languages [comma separated]:");
        String languageString = sc.nextLine();
        String[] languages = languageString.split(",");
        g.setLanguages(languages);

        System.out.println("Specify summary:");
        g.setSummary(sc.nextLine());

        String str = "[Message]: Game successfully added";
        try {
            gameService.insertGame(g, consistencyThread);
        } catch (ConnectionException ex) {
            System.out.println("Connection lost");
            System.exit(1);
        }
        moderatorMenu.getSpecialDisplayed().set(true);
        showSpecialDropdown(str);
    }

    private void synchronizeGames() {
        try {
            moderatorService.synchronizeGames(consistencyThread);
            showSpecialDropdown("Synchronized performed");
        } catch (ConnectionException ex) {
            showSpecialDropdown("Could not perform synchronization, try later");
        }
    }

    public ModeratorController(String username, LocalDate dateOfBirth) {
        this.moderatorMenu = new ModeratorMenu();
        this.moderatorService = ServiceLocator.getModeratorService();
        this.gameService = ServiceLocator.getGameService();
        this.gameController = new GameController(moderatorMenu.getDisplayed(), username, dateOfBirth, true, consistencyThread);
        this.statisticsService = ServiceLocator.getStatisticsService();
        this.registeredUserController = new RegisteredUserController(username, dateOfBirth, true, consistencyThread);
        this.statisticsController = new StatisticsController();
        normalFunctions = new Runnable[]{
                () -> {
                    // Mostrare un altro menù
                    moderatorMenu.getDisplayed().set(false);
                    showSpecialDropdown("");
                },
                gameController::askGameQueryByName,
                () -> registeredUserController.askSearchByUsernameQuery(moderatorMenu.getDisplayed()),
                () -> registeredUserController.displayLibrary(moderatorMenu.getDisplayed()),
                () -> registeredUserController.displayWishlist(moderatorMenu.getDisplayed()),
                () -> registeredUserController.usersYouMightFollow(moderatorMenu.getDisplayed()),
                () -> {
                    moderatorMenu.getDisplayed().set(false);
                    statisticsController.findTopReviewersByReviewsCountLastMonth(moderatorMenu.getDisplayed());
                },
                () -> {
                    moderatorMenu.getDisplayed().set(false);
                    statisticsController.top10GamesByPositiveRatingRatio(moderatorMenu.getDisplayed());
                },
                () -> {
                    moderatorMenu.getDisplayed().set(false);
                    gameController.trendingGamesChart();
                },
                () -> registeredUserController.getSuggestedGames(moderatorMenu.getDisplayed()),
                () -> {
                    consistencyThread.stopThread();
                    System.exit(0);
                }
        };
        // Add game
        // Sync games
        functionsModerator = new Runnable[]{
                this::displayReport,
                this::promptNewGameForm,
                this::synchronizeGames,
                () -> {
                    moderatorMenu.getSpecialDisplayed().set(false);
                    statisticsController.numberOfRegistrationsByMonth(moderatorMenu.getSpecialDisplayed());
                },
                () -> {
                    moderatorMenu.getSpecialDisplayed().set(false);
                    moderatorMenu.getDisplayed().set(true);
                    showModeratorDropdown();
                }
        };
    }

    public void execute() {
        // After the login the consistency thread starts
        consistencyThread.start();
        Utils.clearConsole();
        showModeratorDropdown();
    }
}
