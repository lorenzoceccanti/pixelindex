package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.GameRatingDTO;
import it.unipi.largescale.pixelindex.dto.MostActiveReviewerDTO;
import it.unipi.largescale.pixelindex.dto.RegistrationStatsDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.service.StatisticsService;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class StatisticsController {
    StatisticsService statisticsService;
    ArrayList<MostActiveReviewerDTO> top10Reviewers;
    ArrayList<GameRatingDTO> top10Games;
    ArrayList<RegistrationStatsDTO> registrationStatsDTOs;
    ArrayList<String> buttons = new ArrayList<>();
    ListSelector ls;


    private void addDropdown(AtomicBoolean inMainMenu) {
        ls.addOptions(buttons, "backStatistics", "Select an option");
        int choice = ls.askUserInteraction("backStatistics");
        if (choice == 0) // The button has been pressed
            inMainMenu.set(true);
    }

    public void findTopReviewersByReviewsCountLastMonth(AtomicBoolean inMainMenu) {
        try {
            top10Reviewers = statisticsService.findTop10ReviewersByReviewsCountLastMonth();
        } catch (ConnectionException ex) {
            System.out.println("MongoDB: connection lost");
        }
        System.out.println("*** TOP 10 REVIEWERS OF LAST MONTH *** ");
        for (int i = 0; i < top10Reviewers.size(); i++) {
            System.out.print(top10Reviewers.get(i));
        }
        System.out.println("");
        addDropdown(inMainMenu);
    }

    public void top10GamesByPositiveRatingRatio(AtomicBoolean inMainMenu) {
        try {
            top10Games = statisticsService.topNRatedGames(10);
        } catch (ConnectionException ex) {
            System.out.println("MongoDB: connection lost");
        }
        System.out.println("*** TOP 10 RATED GAMES ***");
        for (int i = 0; i < top10Games.size(); i++) {
            System.out.print(top10Games.get(i));
        }
        System.out.println("");
        addDropdown(inMainMenu);
    }

    public void numberOfRegistrationsByMonth(AtomicBoolean inMainMenu) {
        System.out.println("Which year?");
        Scanner sc = new Scanner(System.in);
        int year = sc.nextInt();
        try {
            registrationStatsDTOs = statisticsService.numberOfRegistrationsByMonth(year);
        } catch (ConnectionException ex) {
            System.out.println("MongoDB: connection lost");
        }
        System.out.println("*** REGISTRATION STATS: PIXELINDEX ***");
        if (registrationStatsDTOs.isEmpty())
            System.out.println("No registrations in year: " + year);
        for (int i = 0; i < registrationStatsDTOs.size(); i++) {
            System.out.print(registrationStatsDTOs.get(i));
        }
        System.out.println("");
        addDropdown(inMainMenu);
    }

    public StatisticsController() {
        this.ls = new ListSelector("");
        this.statisticsService = ServiceLocator.getStatisticsService();
        buttons.add("Go back");
    }
}
