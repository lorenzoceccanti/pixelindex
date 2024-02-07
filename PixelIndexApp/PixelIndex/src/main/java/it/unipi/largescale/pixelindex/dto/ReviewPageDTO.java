package it.unipi.largescale.pixelindex.dto;

import java.util.ArrayList;
import java.util.List;

public class ReviewPageDTO {
    Integer totalReviewsCount;
    List<ReviewPreviewDTO> reviews;

    public Integer getTotalReviewsCount() {
        return totalReviewsCount;
    }

    public List<ReviewPreviewDTO> getReviews() {
        return reviews;
    }

    public void setTotalReviewsCount(Integer totalReviewsCount) {
        this.totalReviewsCount = totalReviewsCount;
    }

    public void setReviews(List<ReviewPreviewDTO> reviews) {
        this.reviews = reviews;
    }
}
