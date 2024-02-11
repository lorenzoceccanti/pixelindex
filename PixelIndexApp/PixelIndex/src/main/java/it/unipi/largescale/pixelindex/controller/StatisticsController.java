package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.GameRatingDTO;
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
    ArrayList<GameRatingDTO> top10Games;
    ArrayList<String> buttons = new ArrayList<>();
    ListSelector ls;

    private void addDropdown(AtomicBoolean inMainMenu){
        ls.addOptions(buttons, "backStatistics", "Select an option");
        int choice = ls.askUserInteraction("backStatistics");
        if(choice == 0) // The button has been pressed
            inMainMenu.set(true);
    }

    public void findTopReviewersByPostCountLastMonth(AtomicBoolean inMainMenu){
        try{
            top10Reviewers = statisticsService.findTop10ReviewersByPostCountLastMonth();
        }catch (ConnectionException ex){
            System.out.println("MongoDB: connection lost");
        }
        System.out.println("*** TOP 10 REVIEWERS OF LAST MONTH *** ");
        System.out.println("|Username|Number of reviews");
        for(int i=0; i<top10Reviewers.size(); i++){
            System.out.println(top10Reviewers.get(i));
        }
       addDropdown(inMainMenu);
    }

    public void top10GamesByPositiveRatingRatio(AtomicBoolean inMainMenu){
        try{
            top10Games = statisticsService.topNRatedGames(10);
        }catch (ConnectionException ex){
            System.out.println("MongoDB: connection lost");
        }
        System.out.println("*** TOP 10 RATED GAMES ***");
        System.out.println("|Name|Release Year|Positive Review Ratio|");
        for(int i=0; i<top10Games.size(); i++){
            System.out.println(top10Games.get(i));
        }
        addDropdown(inMainMenu);
    }
    public StatisticsController (){
        this.ls = new ListSelector("");
        this.statisticsService = ServiceLocator.getStatisticsService();
        buttons.add("Go back");
    }
}
