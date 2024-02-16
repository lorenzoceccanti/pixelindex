package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.ReviewPageDTO;
import it.unipi.largescale.pixelindex.dto.ReviewPreviewDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.model.Reaction;
import it.unipi.largescale.pixelindex.model.Review;
import it.unipi.largescale.pixelindex.service.ReviewService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;
import it.unipi.largescale.pixelindex.utils.AnsiColor;
import it.unipi.largescale.pixelindex.utils.Utils;
import it.unipi.largescale.pixelindex.view.impl.ListSelector;

import java.util.ArrayList;
import java.util.List;

public class ReviewController {

    private ConsistencyThread consistencyThread;
    private final ArrayList<String> rows;
    private List<ReviewPreviewDTO> reviewPreviewDTOs;
    private final ReviewService reviewService;
    private int exitReviewList;
    private int countAllReviews;
    private int exitReviewDetails;
    private final String sessionUsername;
    private final boolean isModerator;

    public void viewReviewDetails(int indexView) {
        String reactionResult = "";
        ReviewPreviewDTO revPrev = reviewPreviewDTOs.get(indexView - 3);
        ListSelector ls = new ListSelector("");
        ArrayList<String> opt = new ArrayList<>();
        opt.add("Go back");
        opt.add(AnsiColor.ANSI_BLUE + "Add like" + AnsiColor.ANSI_RESET);
        opt.add(AnsiColor.ANSI_RED + "Add dislike" + AnsiColor.ANSI_RESET);
        opt.add(AnsiColor.ANSI_YELLOW + "Remove review" + AnsiColor.ANSI_RESET);
        // Making the query for get all the details of that specific review
        try {
            String reviewId = revPrev.getId();
            Review detailedReview = reviewService.getReviewDetails(reviewId);
            int sel;
            do {
                Utils.clearConsole();
                System.out.println(reactionResult);
                System.out.println(detailedReview);
                ls.addOptions(opt, "reviewDetailsDropdown", "Add a reaction if you want");
                sel = ls.askUserInteraction("reviewDetailsDropdown");
                switch (sel) {
                    case 0:
                        // Going back
                        exitReviewDetails = 1;
                        exitReviewList = 0;
                        displayExcerpt(detailedReview.getGameId());
                        break;
                    case 1:
                        // Inverti like
                        if (sessionUsername.isEmpty())
                            reactionResult = "Unauthorized";
                        else {
                            reactionResult = "[Operation] " + reviewService.addReaction(reviewId, sessionUsername, Reaction.LIKE, detailedReview.getGameId(), detailedReview.getAuthor(), consistencyThread);
                            exitReviewDetails = 0;
                        }
                        break;
                    case 2:
                        if (sessionUsername.isEmpty())
                            reactionResult = "Unauthorized";
                        else {
                            // Inverti dislike
                            reactionResult = "[Operation] " + reviewService.addReaction(reviewId, sessionUsername, Reaction.DISLIKE, detailedReview.getGameId(), detailedReview.getAuthor(), consistencyThread);
                            exitReviewDetails = 0;
                        }
                        break;
                    case 3:
                        if (isModerator || sessionUsername.equals(detailedReview.getAuthor())) {
                            reviewService.deleteReview(detailedReview.getId(), consistencyThread);
                            reactionResult = "[Operation]: Review removed successfully";
                            exitReviewDetails = 1;
                        } else
                            reactionResult = "Unauthorized";
                        break;
                    default:
                        exitReviewDetails = 0;
                        break;
                }
            } while (exitReviewDetails == 0);
        } catch (ConnectionException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void displayExcerpt(String gameId) {
        int result;
        int pageSelection = 0;
        int totalPages;
        do {
            Utils.clearConsole();
            ListSelector ls = new ListSelector("Query result");
            result = constructExcerpt(gameId, pageSelection);
            int REVIEWS_PER_PAGE = 10;
            totalPages = (int) Math.ceil((double) countAllReviews / REVIEWS_PER_PAGE);
            if (totalPages == 0)
                System.out.println("***List empty ***");
            else
                System.out.println("Page displayed: " + (pageSelection + 1) + " of " + totalPages);
            if (result != 0)
                break;
            constructView();
            ls.addOptions(rows, "displayReviewEx", "Press enter to view review details");
            int choice = ls.askUserInteraction("displayReviewEx");
            switch (choice) {
                case 0: // Previous page
                    exitReviewList = 0;
                    pageSelection = pageSelection > 0 ? --pageSelection : pageSelection;
                    break;
                case 1: // Next page
                    exitReviewList = 0;
                    pageSelection = pageSelection < totalPages - 1 ? ++pageSelection : pageSelection;
                    break;
                case 2: // Go back
                    exitReviewList = 1;
                    return;
                default:
                    exitReviewList = 1;
                    // Details review
                    viewReviewDetails(choice);
                    break;
            }
        } while (exitReviewList != 1);
    }

    private void constructView() {
        rows.clear();
        rows.add(AnsiColor.ANSI_YELLOW + "Previous page" + AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW + "Next page" + AnsiColor.ANSI_RESET);
        rows.add(AnsiColor.ANSI_YELLOW + "Go back" + AnsiColor.ANSI_RESET);
        reviewPreviewDTOs.forEach(reviewPreviewDTO -> rows.add(reviewPreviewDTO.toString()));
    }

    private int constructExcerpt(String gameId, int page) {
        try {
            ReviewPageDTO reviewPageDTO = reviewService.getReviews(gameId, sessionUsername, page);
            countAllReviews = reviewPageDTO.getTotalReviewsCount();
            reviewPreviewDTOs = reviewPageDTO.getReviews();
            return 0;
        } catch (ConnectionException ex) {
            System.out.println(ex.getMessage());
            return 1;
        }
    }

    public ReviewController(String sessionUsername, boolean isModerator, ConsistencyThread consistencyThread) {
        this.rows = new ArrayList<>();
        this.consistencyThread = consistencyThread;
        this.sessionUsername = sessionUsername;
        this.isModerator = isModerator;
        this.reviewPreviewDTOs = new ArrayList<>();
        this.reviewService = ServiceLocator.getReviewService();
    }

    public ReviewController() {
        this.rows = new ArrayList<>();
        this.sessionUsername = "";
        this.isModerator = false;
        this.reviewPreviewDTOs = new ArrayList<>();
        this.reviewService = ServiceLocator.getReviewService();
    }
}
