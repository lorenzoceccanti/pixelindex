package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.service.impl.RegUserServiceImpl;

public class ServiceLocator {
    private static RegisteredUserService registeredUserService = new RegUserServiceImpl();
    // Here you have to insert, at least
    // GameService
    // ReviewService
    // WhishlistService
    // LibraryService
    // AnalyticsService
    public static RegisteredUserService getRegisteredUserService(){
        return registeredUserService;
    }
}
