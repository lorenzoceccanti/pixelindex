package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.UserReportsDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Company;
import it.unipi.largescale.pixelindex.model.Game;
import it.unipi.largescale.pixelindex.model.Genre;
import it.unipi.largescale.pixelindex.service.GameService;
import it.unipi.largescale.pixelindex.service.ModeratorService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.service.StatisticsService;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.dropdown.ModeratorMenu;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;
import jline.internal.Ansi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModeratorController {
    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    ConsistencyThread consistencyThread = new ConsistencyThread(queue);
    private ArrayList<String> rows = new ArrayList<>();
    private ModeratorMenu moderatorMenu;
    private StatisticsService statisticsService;
    private ArrayList<UserReportsDTO> userReportsDTOS;
    private ModeratorService moderatorService;
    private Runnable[] functionsModerator;
    private GameService gameService;
    private int exitReportList;

    private int showModeratorDropdown(String str){
        int opt = -1;
        do{
            Utils.clearConsole();
            System.out.println(str);
            moderatorMenu.getDisplayed().set(true);
            opt = moderatorMenu.displayMenu();
            functionsModerator[opt].run();
        }while(moderatorMenu.getDisplayed().get());
        return opt;
    }
    private int queryReports(){
        try{
            userReportsDTOS = statisticsService.topNReportedUser(10);
            return 0;
        }catch(ConnectionException ex){
            return 1;
        }
    }
    private void buildReport(){
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW+"Go back"+AnsiColor.ANSI_RESET);
        userReportsDTOS.stream().forEach(userReport -> {
            rows.add(userReport.toString());
        });
        if(rows.size() <= 1){
            System.out.println("*** Empty ***");
        }
    }
    private int banUser(String username){
        try{
            moderatorService.banUser(username, consistencyThread);
            return 0;
        }catch(ConnectionException ex){
            return 1;
        }
    }
    private int displayReport(){
        int result = 1; int choice = -1;
        String messageDisp = "";
        moderatorMenu.getDisplayed().set(false);
        do{
            Utils.clearConsole();
            System.out.println(messageDisp);
            ListSelector ls = new ListSelector("Most reported users:");
            result = queryReports();
            if(result != 0)
                return result;
            buildReport();
            ls.addOptions(rows, "reportsDropdown", "Press enter to ban the user");
            choice = ls.askUserInteraction("reportsDropdown");
            switch(choice)
            {
                case 0:
                    exitReportList = 1;
                    break;
                default:
                    exitReportList = 0;
                    // Ban the user
                    UserReportsDTO user = userReportsDTOS.get(choice-1);
                    int sts = banUser(user.getUsername());
                    messageDisp = (sts == 0) ? "User " + AnsiColor.ANSI_YELLOW+user.getUsername()+AnsiColor.ANSI_RESET+" banned successfully":messageDisp;
            }
        }while(exitReportList != 1);
        moderatorMenu.getDisplayed().set(true);
        showModeratorDropdown("");
        return result;
    }

    private void promptNewGameForm(){
        Game g = new Game();
        moderatorMenu.getDisplayed().set(false);
        System.out.println("*** INSERTING NEW GAME ***");
        Scanner sc = new Scanner(System.in);
        System.out.println("Specify name:");
        g.setName(sc.nextLine());
        System.out.println("Specify the release date [format YYYY-MM-DD]:");
        String dateAsString = sc.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate ld = LocalDate.parse(dateAsString,formatter);
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
        Genre[] genres = new Genre[tokens.length];
        for(int i = 0; i<genres.length; i++){
            genres[i] = new Genre();
            genres[i].setName(tokens[i]);
        }
        g.setGenres(genres);

        System.out.println("Specify the platforms [comma separated]:");
        String platfromString = sc.nextLine();
        String[] platforms = platfromString.split(",");
        g.setPlatforms(platforms);

        System.out.println("Specify the companies [comma separated]:");
        String companiesString = sc.nextLine();
        tokens = companiesString.split(",");
        Company[] companies = new Company[tokens.length];
        for(int i=0; i<companies.length; i++){
            companies[i] = new Company();
            companies[i].setName(tokens[i]);
        }
        g.setCompanies(companies);

        System.out.println("Specify the languages [comma separated]:");
        String languageString = sc.nextLine();
        String[] languages = languageString.split(",");
        g.setLanguages(languages);

        System.out.println("Specify summary:");
        g.setSummary(sc.nextLine());

        String str = "[Message]: Game successfully added";
        try{
            gameService.insertGame(g, consistencyThread);
        }catch (ConnectionException ex){
            ex.printStackTrace();
            System.out.println("Connection lost");
            System.exit(1);
        }
        moderatorMenu.getDisplayed().set(true);
        showModeratorDropdown(str);
    }
    public ModeratorController(){
        this.moderatorMenu = new ModeratorMenu();
        this.moderatorService = ServiceLocator.getModeratorService();
        this.gameService = ServiceLocator.getGameService();
        this.statisticsService = ServiceLocator.getStatisticsService();
        functionsModerator = new Runnable[]{
                () -> {
                    displayReport();
                },
                () -> {
                    // Add game
                    promptNewGameForm();
                },
                () -> {
                    System.exit(0);
                }
        };
    }

    public void execute(){
        // After the login the consistency thread starts
        consistencyThread.start();
        Utils.clearConsole();
        int index = showModeratorDropdown("");
    }
}
