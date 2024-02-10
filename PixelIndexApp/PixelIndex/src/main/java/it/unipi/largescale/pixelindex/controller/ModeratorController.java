package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.UserReportsDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.service.ModeratorService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.service.StatisticsService;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.view.dropdown.ModeratorMenu;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ModeratorController {
    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    ConsistencyThread consistencyThread = new ConsistencyThread(queue);
    private ArrayList<String> rows = new ArrayList<>();
    private ModeratorMenu moderatorMenu;
    private StatisticsService statisticsService;
    private ArrayList<UserReportsDTO> userReportsDTOS;
    private ModeratorService moderatorService;
    private Runnable[] functionsModerator;

    private int queryReports(){
        try{
            userReportsDTOS = statisticsService.topNReportedUser(10);
            return 0;
        }catch(ConnectionException ex){
            return 1;
        }
    }
    private void viewReport(){
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW+"Go back"+AnsiColor.ANSI_RESET);
        userReportsDTOS.stream().forEach(userReport -> {
            rows.add(userReportsDTOS.toString());
        });
        if(rows.size() <= 1){
            System.out.println("*** Empty ***");
        }
    }
    private void displayReport(){
        /** TO DO **/
    }
    public ModeratorController(){
        this.moderatorMenu = new ModeratorMenu();
        this.moderatorService = ServiceLocator.getModeratorService();
        this.statisticsService = ServiceLocator.getStatisticsService();
        functionsModerator = new Runnable[]{
                () -> {

                },
                () -> {
                    System.exit(0);
                }
        };
    }
}
