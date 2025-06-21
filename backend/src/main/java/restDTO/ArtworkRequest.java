package restDTO;

import java.util.List;

import lombok.Data;

@Data
public class ArtworkRequest {
	private Long id;
	private String title;
	private int creationYear;
	private String description;
	private double averageRating;
	private String artist;
	private String museum;
    private List<ReviewRequest> reviews;
    private boolean favorite;

}
