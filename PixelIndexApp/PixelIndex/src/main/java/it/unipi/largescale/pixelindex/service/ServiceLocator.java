package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.service.impl.ModeratorServiceImpl;
import it.unipi.largescale.pixelindex.service.impl.RegUserServiceImpl;
import it.unipi.largescale.pixelindex.service.impl.StatisticsServiceImpl;
import it.unipi.largescale.pixelindex.service.impl.GameServiceImpl;

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
    public static RegisteredUserService getRegisteredUserService(){
        return registeredUserService;
    }
    public static StatisticsService getStatisticsService() {return statisticsService;}
    public static GameService getGameService() {return gameService;}
    public static ModeratorService getModeratorService() {return moderatorService;}
}
