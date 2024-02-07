package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.ReviewPageDTO;
import it.unipi.largescale.pixelindex.dto.ReviewPreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.service.ReviewService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReviewController {

    private ReviewPageDTO reviewPageDTO;
    private ArrayList<String> rows;
    private List<ReviewPreviewDTO> reviewPreviewDTOs;
    private ReviewService reviewService;
    private int totalPages;
    private int exitReviewList;
    private int countAllReviews;
    private AtomicBoolean menuDisplayed;
    private String sessionUsername;
    public int displayExcerpt(String gameId){
        int result; int pageSelection = 0;
        do{
            Utils.clearConsole();
            ListSelector ls = new ListSelector("Query result");
            System.out.println("Page displayed: " + (pageSelection+1) + "of "+countAllReviews);
            result = constructExcerpt(gameId,pageSelection);
            if(result != 0)
                break;
            constructView();
            ls.addOptions(rows,"displayReviewEx", "Press enter to view review details");
            int choice = ls.askUserInteraction("displayReviewEx");
            switch(choice)
            {
                case 0: // Previous page
                    exitReviewList = 0;
                    pageSelection = pageSelection > 0 ? --pageSelection : pageSelection;
                    break;
                case 1: // Next page
                    exitReviewList = 0;
                    pageSelection = pageSelection < totalPages-1 ? ++pageSelection : pageSelection;
                    break;
                case 2: // Go back
                    menuDisplayed.set(true);
                    exitReviewList = 1;
                    break;
                default:
                    exitReviewList = 1;
                    // Details review
                    break;
            }
        }while(exitReviewList != 1);
        return result;
    }
    private void constructView(){
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW+"Previous page"+AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW+"Next page"+AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW+"Go back"+AnsiColor.ANSI_RESET);
        rows.stream().forEach(reviewPreviewDTO -> {
            rows.add(reviewPreviewDTO.toString());
        });
    }
    private int constructExcerpt(String gameId, int page){
        try{
            reviewPageDTO = reviewService.getReviews(gameId, sessionUsername, page);
            countAllReviews = reviewPageDTO.getTotalReviewsCount();
            reviewPreviewDTOs = reviewPageDTO.getReviews();
            return 0;
        }catch(ConnectionException ex)
        {
            ex.printStackTrace();
            return 1;
        }
    }

    public ReviewController(String sessionUsername){
        this.rows = new ArrayList<>();
        this.sessionUsername = sessionUsername;
        this.reviewPreviewDTOs = new ArrayList<>();
        this.reviewService = ServiceLocator.getReviewService();
        this.menuDisplayed = new AtomicBoolean(false);
        this.countAllReviews = 0;
    }
    public ReviewController(){
        this.rows = new ArrayList<>();
        this.sessionUsername = "";
        this.reviewPreviewDTOs = new ArrayList<>();
        this.reviewService = ServiceLocator.getReviewService();
        this.menuDisplayed = new AtomicBoolean(false);
        this.countAllReviews = 0;
    }
}
