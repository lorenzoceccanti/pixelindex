package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.ReviewPageDTO;
import it.unipi.largescale.pixelindex.dto.ReviewPreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Review;
import it.unipi.largescale.pixelindex.service.ReviewService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;
import jline.internal.Ansi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReviewController {

    private final int REVIEWS_PER_PAGE = 10;
    private ReviewPageDTO reviewPageDTO;
    private Review detailedReview;
    private ArrayList<String> rows;
    private List<ReviewPreviewDTO> reviewPreviewDTOs;
    private ReviewService reviewService;
    private int exitReviewList;
    private int countAllReviews;
    private int exitReviewDetails;
    private AtomicBoolean menuDisplayed;
    private String sessionUsername;
    public int viewReviewDetails(int indexView){
        AtomicBoolean inGameDetails = new AtomicBoolean(false);
        ReviewPreviewDTO revPrev = reviewPreviewDTOs.get(indexView-3);
        ListSelector ls = new ListSelector("Review details");
        ArrayList<String> opt = new ArrayList<>();
        opt.add("Go back");
        opt.add(AnsiColor.ANSI_BLUE+"Add like"+ AnsiColor.ANSI_RESET);
        opt.add(AnsiColor.ANSI_RED+"Add dislike"+AnsiColor.ANSI_RESET);
        // Making the query for get all the details of that specific review
        try{
            String reviewId = revPrev.getId();
            detailedReview = reviewService.getReviewDetails(reviewId);
            int sel = -1;
            do{
                System.out.println("printing r..");
                System.out.println(detailedReview);
                ls.addOptions(opt,"reviewDetailsDropdown", "Add a reaction if you want");
                sel = ls.askUserInteraction("reviewDetailsDropdown");
                switch(sel)
                {
                    case 0:
                        // Going back
                        exitReviewDetails = 1;
                        exitReviewList = 0;
                        displayExcerpt(detailedReview.getGameId(),0);
                        break;
                    case 1:
                        // Inverti like
                        exitReviewDetails = 0;
                        break;
                    case 2:
                        // Inverti dislike
                        exitReviewDetails = 0;
                        break;
                    default:
                        exitReviewDetails = 0;
                        break;
                }
            }while(sel != 0);
            return 0;
        }catch(ConnectionException ex)
        {
            return 1;
        }
    }
    public int displayExcerpt(String gameId, Integer exitGameDetails){
        int result; int pageSelection = 0;
        int totalPages = 0;
        do{
            Utils.clearConsole();
            ListSelector ls = new ListSelector("Query result");
            result = constructExcerpt(gameId,pageSelection);
            totalPages = (int)Math.ceil((double)(countAllReviews/REVIEWS_PER_PAGE));
            if(totalPages == 0)
                System.out.println("***List empty ***");
            else
                System.out.println("Page displayed: " + (pageSelection+1) + "of "+ totalPages);
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
                    exitGameDetails = 0;
                    exitReviewList = 1;
                    return 1;
                default:
                    exitReviewList = 1;
                    // Details review
                    System.out.println("choice: " + choice);
                    viewReviewDetails(choice);
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
        reviewPreviewDTOs.stream().forEach(reviewPreviewDTO -> {
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
    }
    public ReviewController(){
        this.rows = new ArrayList<>();
        this.sessionUsername = "";
        this.reviewPreviewDTOs = new ArrayList<>();
        this.reviewService = ServiceLocator.getReviewService();
        this.menuDisplayed = new AtomicBoolean(false);
    }
}
