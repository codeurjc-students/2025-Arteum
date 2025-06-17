package model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Artwork")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "museum")
public class Artwork {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private int creationYear;
	private String description;
	private double averageRating;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "artist_id")
	private Artist artist;
	@Lob
	@Column(name = "image", columnDefinition = "LONGBLOB")
	private byte[] image;
	@ManyToOne
	@JoinColumn(name = "museum_id")
	private Museum museum;
	
	@OneToMany(mappedBy = "artwork", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @ManyToMany(mappedBy = "favoriteArtworks", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> usersWhoFavorited;

	public Artwork(Long id, String title, Artist artist, int creationYear, String description, byte[] image,
			Museum museum) {
		super();
		this.id = id;
		this.title = title;
		this.artist = artist;
		this.creationYear = creationYear;
		this.description = description;
		this.image = image;
		this.museum = museum;
	}

}
