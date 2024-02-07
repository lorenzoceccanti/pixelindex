package it.unipi.largescale.pixelindex.controller;

import it.unipi.largescale.pixelindex.dto.ReviewPageDTO;
import it.unipi.largescale.pixelindex.exceptions.ConnectionException;
import it.unipi.largescale.pixelindex.service.ReviewService;
import it.unipi.largescale.pixelindex.service.ServiceLocator;

import java.security.Provider;
import java.util.ArrayList;

public class ReviewController {

    private ReviewPageDTO reviewPageDTO;
    private ArrayList<String> rows;
    private ReviewService reviewService;
    private int pageSelection;
    private int totalPages;
    private int exitReviewList;
    private int constructExcerpt(String gameId){
        try{
            reviewPageDTO = reviewService.getReviews(gameId);
            return 0;
        }catch(ConnectionException ex)
        {
            return 1;
        }
    }

    public void displayReviews(){
        rows.clear();
        rows.add("Go back");
        // reviewPageDTO
    }
    public ReviewController(){
        this.reviewService = ServiceLocator.getReviewService();
        this.pageSelection = 0;
    }
}
