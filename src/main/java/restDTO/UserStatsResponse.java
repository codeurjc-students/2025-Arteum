package restDTO;

import java.util.Map;

import lombok.Data;

@Data
public class UserStatsResponse {
    private String name;
    private Map<Integer, Long> starsData;
    private long following;
    private long followers;
    private double avgGiven;
    private int numReviews;
    private Map<String, Long> top10favByArtist;
    private Map<String, Long> top10Artists;
}
