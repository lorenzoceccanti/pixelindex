package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.UserReportsDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.service.ModeratorService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.service.StatisticsService;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.dropdown.ModeratorMenu;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;
import jline.internal.Ansi;

import java.util.ArrayList;
import java.util.List;
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
    private int exitReportList;

    private int showModeratorDropdown(){
        int opt = -1;
        do{
            Utils.clearConsole();
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
            moderatorService.banUser(username);
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
        showModeratorDropdown();
        return result;
    }
    public ModeratorController(){
        this.moderatorMenu = new ModeratorMenu();
        this.moderatorService = ServiceLocator.getModeratorService();
        this.statisticsService = ServiceLocator.getStatisticsService();
        functionsModerator = new Runnable[]{
                () -> {
                    displayReport();
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
        int index = showModeratorDropdown();
    }
}
