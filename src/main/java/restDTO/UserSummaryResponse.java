package restDTO;

import lombok.Data;

@Data
public class UserSummaryResponse {
    private Long id;
    private String name;
    private String image;
    private String location;
    private String createdAt;
    private boolean isFollowing;
}
