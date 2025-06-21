package restDTO;

import java.util.List;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String biography;
    private String location;
    private String createdAt;
    private int followersCount;
    private int followingCount;
    private int reviewsCount;
    private Double reviewsAverage;
    private boolean isNotMyProfile;
    private boolean isFollowing;
    private List<ArtworkRequest> favoriteArtworks;
}
