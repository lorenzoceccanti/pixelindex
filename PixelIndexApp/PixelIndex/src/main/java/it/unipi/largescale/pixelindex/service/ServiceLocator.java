package it.unipi.largescale.pixelindex.service;

import it.unipi.largescale.pixelindex.service.impl.*;

public class ServiceLocator {
    private static final RegisteredUserService registeredUserService = new RegUserServiceImpl();
    private static final StatisticsService statisticsService = new StatisticsServiceImpl();
    private static final GameService gameService = new GameServiceImpl();
    private static final ModeratorService moderatorService = new ModeratorServiceImpl();
    private static final SuggestionsService suggestionsService = new SuggestionsServiceImpl();
    private static final ReviewService reviewService = new ReviewServiceImpl();
    private static final LibraryService libraryService = new LibraryServiceImpl();
    private static final WishlistService wishlistService = new WishlistServiceImpl();

    public static RegisteredUserService getRegisteredUserService(){
        return registeredUserService;
    }
    public static StatisticsService getStatisticsService() {return statisticsService;}
    public static GameService getGameService() {return gameService;}
    public static ReviewService getReviewService(){return reviewService;}
    public static ModeratorService getModeratorService() {return moderatorService;}
    public static SuggestionsService getSuggestionsService() {return suggestionsService;}
    public static LibraryService getLibraryService() {return libraryService;}

    public static WishlistService getWishlistService() {return wishlistService;}

}
