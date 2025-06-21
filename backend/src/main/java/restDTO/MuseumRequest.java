package restDTO;

import java.util.List;

import lombok.Data;

@Data
public class MuseumRequest {
	private Long id;
	private String name;
	private String location;
	private String description;
	private int founded;
	private List<ArtworkRequest> artworks;
}
