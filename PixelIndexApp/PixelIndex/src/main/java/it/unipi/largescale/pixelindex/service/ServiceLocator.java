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
    private static AnalyticsService analyticsService = new AnalyticsServiceImpl();
    public static RegisteredUserService getRegisteredUserService(){
        return registeredUserService;
    }
    public static StatisticsService getStatisticsService() {return statisticsService;}
    public static GameService getGameService() {return gameService;}
    public static ModeratorService getModeratorService() {return moderatorService;}
    public static AnalyticsService getAnalyticsService() {return analyticsService;}
}
