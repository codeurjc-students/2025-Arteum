package restDTO;

import java.util.List;

import lombok.Data;

@Data
public class ArtistRequest {
	private Long id;
	private String name;
	private String nationality;
	private Integer dateOfBirth;
	private Integer dateOfDeath;
	private List<ArtworkRequest> artworks;
}
