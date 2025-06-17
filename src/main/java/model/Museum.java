package model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Museum")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "artworks")
public class Museum {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String location;
	private String description;
	private int founded;
	@Lob
	@Column(name = "image", columnDefinition = "LONGBLOB")
	private byte[] image;

	@OneToMany(mappedBy = "museum")
	private List<Artwork> artworks = new ArrayList<>();

}