package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.service.impl.RegUserServiceImpl;
import it.unipi.largescale.pixelindex.service.impl.StatisticsServiceImpl;

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
    public static RegisteredUserService getRegisteredUserService(){
        return registeredUserService;
    }
    public static StatisticsService getStatisticsService() {return statisticsService;}
}
