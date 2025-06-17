package service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.experimental.Delegate;
import model.Artwork;
import repository.ArtworkRepository;
import repository.ReviewRepository;

@Service
public class ArtworkService {

	@Autowired
	@Delegate
	private ArtworkRepository artworkRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	public Page<Artwork> getArtworksPage(Pageable pageable) {
		return artworkRepository.findAll(pageable);
	}

	public Page<Artwork> getArtworksByArtistPage(Long artistId, Pageable pageable) {
		if (artistId != null) {
			return artworkRepository.findByArtistId(artistId, pageable);
		} else {
			return artworkRepository.findAll(pageable);
		}
	}

	public List<Artwork> getArtworksByArtistPage(Long artistId, String search) {
		if (artistId != null) {
			if (search != null && !search.trim().isEmpty()) {
				return artworkRepository.findByArtistIdAndTitleContainingIgnoreCase(artistId, search);
			} else {
				return artworkRepository.findByArtistId(artistId);
			}
		} else {
			return null;
		}
	}

	public Page<Artwork> getArtworksByArtistPage(Long artistId, String search, Pageable pageable) {
		if (artistId != null) {
			if (search != null && !search.trim().isEmpty()) {
				return artworkRepository.findByArtistIdAndTitleContainingIgnoreCase(artistId, search, pageable);
			} else {
				return getArtworksByArtistPage(artistId, pageable);
			}
		} else {
			return artworkRepository.findAll(pageable);
		}
	}

	public Page<Artwork> getArtworksByArtistPage(Long artistId, String search, List<double[]> ratingRanges,
			Pageable pageable) {
		if (ratingRanges != null && !ratingRanges.isEmpty()) {
			List<Artwork> artworks;
			if ((search == null || search.isBlank())) {
				artworks = artworkRepository.findByArtistId(artistId);
			} else {
				artworks = artworkRepository.findByArtistIdAndTitleContainingIgnoreCase(artistId, search);
			}
			artworks = artworks.stream().filter(a -> {
				double avg = a.getAverageRating();
				return ratingRanges.stream().anyMatch(r -> avg >= r[0] && avg <= r[1]);
			}).toList();
			List<Artwork> mutableList = new ArrayList<>(artworks);

			Comparator<Artwork> comparator = getComparatorFromSort(pageable.getSort());
			if (comparator != null) {
				mutableList.sort(comparator);
			}
			int start = (int) pageable.getOffset();
			int end = Math.min(start + pageable.getPageSize(), mutableList.size());
			return new PageImpl<>(mutableList.subList(start, end), pageable, mutableList.size());
		} else {
			return getArtworksByArtistPage(artistId, search, pageable);
		}
	}

	public Page<Artwork> getArtworksByArtistPageAndMuseum(Long artistId, String search, List<Long> museumIds,
			Pageable pageable) {
		boolean hasSearch = search != null && !search.trim().isEmpty();
		boolean hasMuseums = museumIds != null && !museumIds.isEmpty();

		if (hasSearch && !hasMuseums) {
			return artworkRepository.findByArtistIdAndTitleContainingIgnoreCase(artistId, search.trim(), pageable);
		} else if (!hasSearch && hasMuseums) {
			return artworkRepository.findByArtistIdAndMuseumIdIn(artistId, museumIds, pageable);
		} else if (hasSearch && hasMuseums) {
			return artworkRepository.findByArtistIdAndTitleContainingIgnoreCaseAndMuseumIdIn(artistId, search.trim(),
					museumIds, pageable);
		} else {
			return getArtworksByArtistPage(artistId, pageable);
		}
	}

	public Page<Artwork> getArtworksByArtistPage(Long artistId, String search, List<double[]> ratingRanges,
			List<Long> museumIds, Pageable pageable) {
		if (ratingRanges != null && !ratingRanges.isEmpty()) {
			List<Artwork> artworks;

			if ((search == null || search.isBlank()) && (museumIds == null || museumIds.isEmpty())) {
				artworks = artworkRepository.findByArtistId(artistId);
			} else if (search != null && !search.isBlank() && museumIds != null && !museumIds.isEmpty()) {
				artworks = artworkRepository.findByArtistIdAndTitleContainingIgnoreCaseAndMuseumIdIn(artistId, search,
						museumIds);
			} else if (search != null && !search.isBlank()) {
				artworks = artworkRepository.findByArtistIdAndTitleContainingIgnoreCase(artistId, search);
			} else {
				artworks = artworkRepository.findByArtistIdAndMuseumIdIn(artistId, museumIds);
			}

			artworks = artworks.stream().filter(a -> {
				double avg = a.getAverageRating();
				return ratingRanges.stream().anyMatch(r -> avg >= r[0] && avg <= r[1]);
			}).toList();
			List<Artwork> mutableList = new ArrayList<>(artworks);

			Comparator<Artwork> comparator = getComparatorFromSort(pageable.getSort());
			if (comparator != null) {
				mutableList.sort(comparator);
			}
			int start = (int) pageable.getOffset();
			int end = Math.min(start + pageable.getPageSize(), mutableList.size());
			return new PageImpl<>(mutableList.subList(start, end), pageable, mutableList.size());
		} else {
			return getArtworksByArtistPage(artistId, search, pageable);
		}
	}

	public Page<Artwork> getArtworksPage(String search, List<Long> artistIds, Pageable pageable) {
		if ((search == null || search.isBlank()) && (artistIds == null || artistIds.isEmpty())) {
			return artworkRepository.findAll(pageable);
		} else if (search != null && !search.isBlank() && artistIds != null && !artistIds.isEmpty()) {
			return artworkRepository.findByTitleContainingIgnoreCaseAndArtistIdIn(search, artistIds, pageable);
		} else if (search != null && !search.isBlank()) {
			return getArtworksPage(search, pageable);
		} else {
			return artworkRepository.findByArtistIdIn(artistIds, pageable);
		}
	}

	public Page<Artwork> getArtworksByMuseumPage(Long museumId, Pageable pageable) {
		if (museumId != null) {
			return artworkRepository.findByMuseumId(museumId, pageable);
		} else {
			return artworkRepository.findAll(pageable);
		}
	}

	public Page<Artwork> getArtworksPage(String search, List<Long> artistIds, List<double[]> ratingRanges,
			Pageable pageable) {
		if (ratingRanges != null && !ratingRanges.isEmpty()) {
			List<Artwork> artworks;
			if ((search == null || search.isBlank()) && (artistIds == null || artistIds.isEmpty())) {
				artworks = artworkRepository.findAll();
			} else if (search != null && !search.isBlank() && artistIds != null && !artistIds.isEmpty()) {
				artworks = artworkRepository.findByTitleContainingIgnoreCaseAndArtistIdIn(search, artistIds);
			} else if (search != null && !search.isBlank()) {
				artworks = artworkRepository.searchByTitleContainingIgnoreCase(search);
			} else {
				artworks = artworkRepository.findByArtistIdIn(artistIds);
			}
			artworks = artworks.stream().filter(a -> {
				double avg = a.getAverageRating();
				return ratingRanges.stream().anyMatch(r -> avg >= r[0] && avg <= r[1]);
			}).toList();
			List<Artwork> mutableList = new ArrayList<>(artworks);

			Comparator<Artwork> comparator = getComparatorFromSort(pageable.getSort());
			if (comparator != null) {
				mutableList.sort(comparator);
			}
			int start = (int) pageable.getOffset();
			int end = Math.min(start + pageable.getPageSize(), mutableList.size());
			return new PageImpl<>(mutableList.subList(start, end), pageable, mutableList.size());
		} else {
			return artworkRepository.findByTitleContainingIgnoreCaseAndArtistIdIn(search, artistIds, pageable);
		}
	}

	public Page<Artwork> getArtworksByMuseumPage(Long museumId, String search, List<Long> artistIds,
			List<double[]> ratingRanges, Pageable pageable) {
		if (museumId == null) {
			return Page.empty(pageable);
		}

		Page<Artwork> page = artworkRepository.findByMuseumId(museumId, Pageable.unpaged());

		Stream<Artwork> stream = page.getContent().stream();

		if (search != null && !search.isBlank()) {
			String searchLower = search.trim().toLowerCase();
			stream = stream.filter(a -> a.getTitle() != null && a.getTitle().toLowerCase().contains(searchLower));
		}

		if (artistIds != null && !artistIds.isEmpty()) {
			Set<Long> artistSet = new HashSet<>(artistIds);
			stream = stream.filter(a -> a.getArtist() != null && artistSet.contains(a.getArtist().getId()));
		}

		if (ratingRanges != null && !ratingRanges.isEmpty()) {
			stream = stream.filter(a -> {
				double rating = a.getAverageRating();
				return ratingRanges.stream().anyMatch(range -> rating >= range[0] && rating < range[1]);
			});
		}

		List<Artwork> filtered = stream.sorted(getComparator(pageable.getSort())).toList();

		int total = filtered.size();
		int start = Math.toIntExact(pageable.getOffset());
		int end = Math.min(start + pageable.getPageSize(), total);

		List<Artwork> content = (start <= end) ? filtered.subList(start, end) : List.of();

		return new PageImpl<>(content, pageable, total);
	}

	public Page<Artwork> getArtworksPage(String search, Pageable pageable) {
		if (search != null && !search.trim().isEmpty()) {
			return artworkRepository.searchByTitleContainingIgnoreCase(search.trim(), pageable);
		}
		return artworkRepository.findAll(pageable);
	}

	public Page<Artwork> getFavoriteArtworksPage(Long userId, Pageable pageable) {
		return artworkRepository.findByUsersWhoFavorited_Id(userId, pageable);
	}

	public Artwork getArtworkById(Long id) {
		return artworkRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Artwork not found with id: " + id));
	}

	public void save(Artwork artwork) {
		artworkRepository.save(artwork);
	}

	public void delete(Artwork artwork) {
		artworkRepository.delete(artwork);
	}

	public List<Artwork> findTop7ArtworksWithStars() {
		return artworkRepository.findTop7ByOrderByAverageRatingDesc();
	}

	public List<Artwork> findTop12ArtworksWithStars() {
		return artworkRepository.findTop12ByOrderByAverageRatingDesc();
	}

	public Page<Artwork> findByUsersWhoFavorited_Id(Long id, String search, List<Long> artistIds,
			List<double[]> ratingRanges, Pageable pageable) {
		if (ratingRanges != null && !ratingRanges.isEmpty()) {
			List<Artwork> artworks;
			if ((search == null || search.isBlank()) && (artistIds == null || artistIds.isEmpty())) {
				artworks = artworkRepository.findByUsersWhoFavorited_Id(id);
			} else if (search != null && !search.isBlank() && artistIds != null && !artistIds.isEmpty()) {
				artworks = artworkRepository.findByUsersWhoFavorited_IdAndTitleContainingIgnoreCaseAndArtistIdIn(id,
						search, artistIds);
			} else if (search != null && !search.isBlank()) {
				artworks = artworkRepository.findByUsersWhoFavorited_IdAndTitleContainingIgnoreCase(id, search);
			} else {
				artworks = artworkRepository.findByUsersWhoFavorited_IdAndArtistIdIn(id, artistIds);
			}
			artworks = artworks.stream().filter(a -> {
				double avg = a.getAverageRating();
				return ratingRanges.stream().anyMatch(r -> avg >= r[0] && avg <= r[1]);
			}).toList();
			List<Artwork> mutableList = new ArrayList<>(artworks);

			Comparator<Artwork> comparator = getComparatorFromSort(pageable.getSort());
			if (comparator != null) {
				mutableList.sort(comparator);
			}
			int start = (int) pageable.getOffset();
			int end = Math.min(start + pageable.getPageSize(), mutableList.size());
			return new PageImpl<>(mutableList.subList(start, end), pageable, mutableList.size());
		} else {
			return artworkRepository.findByUsersWhoFavorited_IdAndTitleContainingIgnoreCaseAndArtistIdIn(id, search,
					artistIds, pageable);
		}
	}

	public Page<Artwork> findByUsersWhoFavorited_Id(Long id, String search, List<Long> artistIds, Pageable pageable) {
		if ((search == null || search.isBlank()) && (artistIds == null || artistIds.isEmpty())) {
			return artworkRepository.findByUsersWhoFavorited_Id(id, pageable);
		} else if (search != null && !search.isBlank() && artistIds != null && !artistIds.isEmpty()) {
			return artworkRepository.findByUsersWhoFavorited_IdAndTitleContainingIgnoreCaseAndArtistIdIn(id, search,
					artistIds, pageable);
		} else if (search != null && !search.isBlank()) {
			return artworkRepository.findByUsersWhoFavorited_IdAndTitleContainingIgnoreCase(id, search, pageable);
		} else {
			return artworkRepository.findByUsersWhoFavorited_IdAndArtistIdIn(id, artistIds, pageable);
		}
	}

	@Transactional
	public void updateAverageRating(Artwork artwork) {
		Double avg = reviewRepository.findAverageRatingByArtwork(artwork);

		double roundedAvg = 0.0;

		if (avg != null && !avg.isNaN() && !avg.isInfinite()) {
			roundedAvg = new BigDecimal(String.valueOf(avg)).setScale(2, RoundingMode.HALF_UP).doubleValue();
		}

		artwork.setAverageRating(roundedAvg);
		artworkRepository.save(artwork);
	}

	private Comparator<Artwork> getComparatorFromSort(Sort sort) {
		for (Sort.Order order : sort) {
			Comparator<Artwork> comparator = switch (order.getProperty()) {
			case "averageRating" -> Comparator.comparing(Artwork::getAverageRating);
			case "artist.name" -> Comparator.comparing(a -> a.getArtist().getName(), String.CASE_INSENSITIVE_ORDER);
			case "creationYear" -> Comparator.comparing(Artwork::getCreationYear);
			case "museum.name" -> Comparator.comparing(a -> a.getMuseum().getName(), String.CASE_INSENSITIVE_ORDER);
			default -> Comparator.comparing(Artwork::getTitle, String.CASE_INSENSITIVE_ORDER);
			};
			if (comparator != null) {
				return order.isAscending() ? comparator : comparator.reversed();
			}
		}

		return null;
	}

	private Comparator<Artwork> getComparator(Sort sort) {
		Comparator<Artwork> comparator = null;

		for (Sort.Order order : sort) {
			Comparator<Artwork> next;

			switch (order.getProperty()) {
			case "averageRating" -> next = Comparator.comparingDouble(Artwork::getAverageRating);
			case "creationYear" -> next = Comparator.comparingInt(Artwork::getCreationYear);
			case "artist.name" -> next = Comparator.comparing(a -> a.getArtist() != null ? a.getArtist().getName() : "",
					Comparator.nullsLast(String::compareToIgnoreCase));
			case "museum.name" -> next = Comparator.comparing(a -> a.getMuseum() != null ? a.getMuseum().getName() : "",
					Comparator.nullsLast(String::compareToIgnoreCase));
			default ->
				next = Comparator.comparing(Artwork::getTitle, Comparator.nullsLast(String::compareToIgnoreCase));
			}

			if (order.isDescending()) {
				next = next.reversed();
			}

			if (comparator == null) {
				comparator = next;
			} else {
				comparator = comparator.thenComparing(next);
			}
		}

		return comparator != null ? comparator : Comparator.comparing(Artwork::getId);
	}

}
