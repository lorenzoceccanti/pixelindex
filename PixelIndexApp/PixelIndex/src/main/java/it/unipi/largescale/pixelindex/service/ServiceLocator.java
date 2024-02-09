package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.service.impl.*;

public class ServiceLocator {
    private static RegisteredUserService registeredUserService = new RegUserServiceImpl();
    // Here you have to insert, at least
    // GameService
    // ReviewService
    // WhishlistService
    // LibraryService
    // AnalyticsService
    // StatisticService
    private static StatisticsService statisticsService = new StatisticsServiceImpl();
    private static GameService gameService = new GameServiceImpl();
    private static ModeratorService moderatorService = new ModeratorServiceImpl();
    private static SuggestionsService suggestionsServic = new SuggestionsServiceImpl();
    private static ReviewService reviewService = new ReviewServiceImpl();
    private static LibraryService libraryService = new LibraryServiceImpl();
    public static RegisteredUserService getRegisteredUserService(){
        return registeredUserService;
    }
    public static StatisticsService getStatisticsService() {return statisticsService;}
    public static GameService getGameService() {return gameService;}
    public static ReviewService getReviewService(){return reviewService;}
    public static ModeratorService getModeratorService() {return moderatorService;}
    public static SuggestionsService getAnalyticsService() {return suggestionsServic;}
    public static LibraryService getLibraryService() {return libraryService;}
}
