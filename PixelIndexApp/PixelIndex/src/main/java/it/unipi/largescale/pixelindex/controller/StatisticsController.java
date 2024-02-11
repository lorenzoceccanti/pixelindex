package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.MostActiveUserDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.service.StatisticsService;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class StatisticsController {
    StatisticsService statisticsService;
    ArrayList<MostActiveUserDTO> top10Reviewers;
    ArrayList<String> buttons = new ArrayList<>();


    public void findTopReviewersByPostCountLastMonth(AtomicBoolean inMainMenu){
        try{
            top10Reviewers = statisticsService.findTop10ReviewersByPostCountLastMonth();
        }catch (ConnectionException ex){
            System.out.println("MongoDB: connection lost");
        }
        ListSelector ls = new ListSelector("*** TOP 10 REVIEWERS OF LAST MONTH *** ");
        ls.addOptions(buttons, "backTop10Rev", "");
        int choice = ls.askUserInteraction("backTop10Rev");
        if(choice == 0) // The button has been pressed
        {
            inMainMenu.set(true);
            return;
        }

    }
    public StatisticsController (){
        this.statisticsService = ServiceLocator.getStatisticsService();
        buttons.add("Go back");
    }
}
