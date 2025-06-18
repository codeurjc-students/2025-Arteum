package restDTO;

import java.util.List;

import lombok.Data;

@Data
public class UserReviewsResponse {
    private int currentPage;
    private Integer previousPage;
    private Integer nextPage;
    private int totalReviews;
    private int totalPages;
    private String range;
    private List<ReviewRequest> reviews;
}