package service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.experimental.Delegate;
import model.Artist;
import repository.ArtistRepository;

@Service
public class ArtistService {

	@Autowired
	@Delegate
	private ArtistRepository artistRepository;

	public Page<Artist> getArtistsPage(Pageable pageable) {
		return artistRepository.findAll(pageable);
	}

	public Page<Artist> getArtistsPage(String search, Pageable pageable) {
		if (search != null && !search.trim().isEmpty()) {
			return artistRepository.searchByNameContainingIgnoreCase(search.trim(), pageable);
		}
		return artistRepository.findAll(pageable);
	}

	public Page<Artist> getArtistsPage(String search, List<String> nationalities, Pageable pageable) {
		if ((search == null || search.isBlank()) && (nationalities == null || nationalities.isEmpty())) {
			return artistRepository.findAll(pageable);
		}
		if (search != null && !search.isBlank() && nationalities != null && !nationalities.isEmpty()) {
			return artistRepository.findByNameContainingIgnoreCaseAndNationalityIn(search, nationalities, pageable);
		}
		if (search != null && !search.isBlank()) {
			return artistRepository.findByNameContainingIgnoreCase(search, pageable);
		}
		return artistRepository.findByNationalityIn(nationalities, pageable);
	}

	public List<Artist> getArtistsPage(String search, List<String> nationalities) {
		if ((search == null || search.isBlank()) && (nationalities == null || nationalities.isEmpty())) {
			return artistRepository.findAll();
		}
		if (search != null && !search.isBlank() && nationalities != null && !nationalities.isEmpty()) {
			return artistRepository.findByNameContainingIgnoreCaseAndNationalityIn(search, nationalities);
		}
		if (search != null && !search.isBlank()) {
			return artistRepository.findByNameContainingIgnoreCase(search);
		}
		return artistRepository.findByNationalityIn(nationalities);
	}

	public Artist getArtistById(Long id) {
		return artistRepository.findById(id).orElseThrow(() -> new RuntimeException("Artist not found with id: " + id));
	}

	public void save(Artist artist) {
		artistRepository.save(artist);
	}

	public void delete(Artist artist) {
		artistRepository.delete(artist);
	}

	public List<Map<String, Object>> findTop10ArtistsByAverageArtworkRating() {
		List<Artist> artists = artistRepository.findTop10ArtistsByAverageArtworkRating();
		List<Map<String, Object>> artistsWithYear = new ArrayList<Map<String, Object>>();

		for (Artist artist : artists) {
			Map<String, Object> artistData = new HashMap<String, Object>();
			artistData.put("id", artist.getId());
			artistData.put("name", artist.getName());
			artistData.put("nationality", artist.getNationality());

			if (artist.getDateOfBirth() != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(artist.getDateOfBirth());
				int birthYear = calendar.get(Calendar.YEAR);
				artistData.put("dateOfBirth", birthYear);
			}

			if (artist.getDateOfDeath() != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(artist.getDateOfDeath());
				int deathYear = calendar.get(Calendar.YEAR);
				artistData.put("dateOfDeath", deathYear);
			}

			artistsWithYear.add(artistData);
		}
		return artistsWithYear;
	}
	
	public List<Artist> findTop10ArtistsByAverageArtworkRatingRaw() {
	    return artistRepository.findTop10ArtistsByAverageArtworkRating();
	}
}
