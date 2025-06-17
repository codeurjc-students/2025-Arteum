package controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import model.Artist;
import model.Artwork;
import model.Review;
import model.User;
import service.ArtworkService;
import service.ReviewService;
import service.UserService;

@Controller
public class ArtworkController {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ArtworkService artworkService;

	@Autowired
	private UserService userService;

	@Autowired
	private ResourceLoader resourceLoader;

	ArtworkController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping("/artworks")
	public String getArtworksPage(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "6") int size, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "title") String sort, @RequestParam(required = false) List<Long> artist,
			@RequestParam(required = false) List<String> ratingRanges, Model model, HttpServletRequest request) {
		// Verify page limit
		if (page < 1) {
			StringBuilder redirectUrl = new StringBuilder("redirect:/artworks?page=1");
			if (search != null && !search.isBlank()) {
				redirectUrl.append("&search=").append(URLEncoder.encode(search, StandardCharsets.UTF_8));
			}
			if (sort != null && !sort.isBlank()) {
				redirectUrl.append("&sort=").append(URLEncoder.encode(sort, StandardCharsets.UTF_8));
			}
			if (artist != null) {
				for (Long artistId : artist) {
					redirectUrl.append("&artist=").append(artistId);
				}
			}
			if (ratingRanges != null) {
				for (String rating : ratingRanges) {
					redirectUrl.append("&rating=").append(URLEncoder.encode(rating, StandardCharsets.UTF_8));
				}
			}
			return redirectUrl.toString();
		}
		// Pageable artworks
		Sort sortBy;
		switch (sort) {
		case "averageRating":
			sortBy = Sort.by(Sort.Direction.DESC, "averageRating");
			break;
		case "artist.name":
			sortBy = Sort.by(Sort.Direction.ASC, "artist.name");
			break;
		case "creationYear":
			sortBy = Sort.by(Sort.Direction.ASC, "creationYear");
			break;
		case "museum.name":
			sortBy = Sort.by(Sort.Direction.ASC, "museum.name");
			break;
		default:
			sortBy = Sort.by(Sort.Direction.ASC, "title");
		}

		List<Artwork> allArtworks = artworkService.findAll();
		if (!allArtworks.isEmpty()) {
			Map<Artist, Long> topArtists = allArtworks.stream()
					.collect(Collectors.groupingBy(Artwork::getArtist, Collectors.counting()));
			List<Artist> mostFrequentArtists = topArtists.entrySet().stream()
					.sorted(Comparator.comparing(Map.Entry<Artist, Long>::getValue).reversed()
							.thenComparing(entry -> entry.getKey().getName(), String.CASE_INSENSITIVE_ORDER))
					.limit(10).map(Map.Entry::getKey).collect(Collectors.toList());
			Set<Long> selectedIds = artist != null ? new HashSet<>(artist) : Set.of();
			model.addAttribute("topArtists", mostFrequentArtists.stream().map(a -> {
				Map<String, Object> m = new HashMap<>();
				m.put("id", a.getId());
				m.put("name", a.getName());
				m.put("selected", selectedIds.contains(a.getId()));
				return m;
			}).toList());
		}

		Pageable pageable = PageRequest.of(page - 1, size, sortBy);

		if (search != null && !search.trim().isEmpty()) {
			boolean isAdmin = request.isUserInRole("ADMIN");

			if (isAdmin && search.trim().matches("\\d+")) {
				Long id = Long.parseLong(search.trim());
				Optional<Artwork> artworkOpt = artworkService.findById(id);
				if (artworkOpt.isPresent()) {
					List<Map<String, Object>> ratingRangeCheckboxes = List.of(Map.of("min", 4, "max", 5),
							Map.of("min", 3, "max", 4), Map.of("min", 2, "max", 3), Map.of("min", 1, "max", 2),
							Map.of("min", 0, "max", 1));
					model.addAttribute("ratingRanges", ratingRangeCheckboxes);
					model.addAttribute("artworksPage", new PageImpl<>(List.of(artworkOpt.get()), pageable, 1));
					model.addAttribute("totalArtworks", 1);
					model.addAttribute("range", "01-01");
					model.addAttribute("sort", sort);
					model.addAttribute("search", search);
					model.addAttribute("currentPage", 1);
					model.addAttribute("previousPage", null);
					model.addAttribute("nextPage", null);
					return "artworks";
				}
			}
		}

		Page<Artwork> artworksPage;

		if (ratingRanges != null && !ratingRanges.isEmpty()) {
			List<double[]> ratingRangesParsed = new ArrayList<>();
			for (String r : ratingRanges) {
				String[] parts = r.trim().split("-");
				double min = Double.parseDouble(parts[0].trim());
				double max = Double.parseDouble(parts[1].trim());
				ratingRangesParsed.add(new double[] { min, max });
			}
			List<Map<String, Object>> ratingRangeCheckboxes = List.of(
					Map.of("min", 4, "max", 5, "selected", ratingRanges.contains("4-5")),
					Map.of("min", 3, "max", 4, "selected", ratingRanges.contains("3-4")),
					Map.of("min", 2, "max", 3, "selected", ratingRanges.contains("2-3")),
					Map.of("min", 1, "max", 2, "selected", ratingRanges.contains("1-2")),
					Map.of("min", 0, "max", 1, "selected", ratingRanges.contains("0-1")));
			model.addAttribute("ratingRanges", ratingRangeCheckboxes);
			artworksPage = artworkService.getArtworksPage(search, artist, ratingRangesParsed, pageable);
		} else {
			List<Map<String, Object>> ratingRangeCheckboxes = List.of(Map.of("min", 4, "max", 5),
					Map.of("min", 3, "max", 4), Map.of("min", 2, "max", 3), Map.of("min", 1, "max", 2),
					Map.of("min", 0, "max", 1));
			model.addAttribute("ratingRanges", ratingRangeCheckboxes);
			artworksPage = artworkService.getArtworksPage(search, artist, pageable);
		}

		model.addAttribute("sort", sort);
		model.addAttribute("sortTitle", sort.equals("title"));
		model.addAttribute("sortRating", sort.equals("averageRating"));
		model.addAttribute("sortArtist", sort.equals("artist.name"));
		model.addAttribute("sortYear", sort.equals("creationYear"));
		model.addAttribute("sortMuseum", sort.equals("museum.name"));
		model.addAttribute("selectedRatingRanges", ratingRanges != null ? ratingRanges : List.of());

		if (search != null && !search.isEmpty() && !search.isBlank()) {
			model.addAttribute("search", search);
		}

		// Verify page limit
		if (artworksPage.getTotalPages() > 0 && page > artworksPage.getTotalPages()) {
			StringBuilder redirectUrl = new StringBuilder("redirect:/artworks?page=" + artworksPage.getTotalPages());
			if (search != null && !search.isBlank()) {
				redirectUrl.append("&search=").append(URLEncoder.encode(search, StandardCharsets.UTF_8));
			}
			if (sort != null && !sort.isBlank()) {
				redirectUrl.append("&sort=").append(URLEncoder.encode(sort, StandardCharsets.UTF_8));
			}
			if (artist != null) {
				for (Long artistId : artist) {
					redirectUrl.append("&artist=").append(artistId);
				}
			}
			if (ratingRanges != null) {
				for (String rating : ratingRanges) {
					redirectUrl.append("&rating=").append(URLEncoder.encode(rating, StandardCharsets.UTF_8));
				}
			}
			return redirectUrl.toString();
		}

		// Artworks range calculation
		int totalArtworks = (int) artworksPage.getTotalElements();
		if (totalArtworks > 0) {
			int start = artworksPage.getNumber() * artworksPage.getSize() + 1;
			int end = Math.min(start + artworksPage.getNumberOfElements() - 1, totalArtworks);

			model.addAttribute("totalArtworks", totalArtworks);
			model.addAttribute("range", String.format("%02d-%02d", start, end));
			model.addAttribute("previousPage", artworksPage.hasPrevious() ? page - 1 : null);
			model.addAttribute("currentPage", page);
			model.addAttribute("nextPage", artworksPage.hasNext() ? page + 1 : null);

			Principal principal = request.getUserPrincipal();
			if (principal != null) {
				List<Map<String, Object>> artworksWithFavStatus = artworksPage.stream().map(artwork -> {
					Map<String, Object> map = new HashMap<>();
					map.put("id", artwork.getId());
					map.put("title", artwork.getTitle());
					map.put("artist", artwork.getArtist());
					map.put("museum", artwork.getMuseum());
					map.put("averageRating", artwork.getAverageRating());
					map.put("isFavorite", userService.artworkIsFavorite(principal.getName(), artwork.getId()));
					return map;
				}).collect(Collectors.toList());
				model.addAttribute("artworksPage", artworksWithFavStatus);
			} else {
				model.addAttribute("artworksPage", artworksPage);
			}
		} else {
			model.addAttribute("totalArtworks", 0);
			model.addAttribute("range", 0);
			model.addAttribute("previousPage", null);
			model.addAttribute("currentPage", 1);
			model.addAttribute("nextPage", null);
			model.addAttribute("artworksEmpty", true);
		}
		return "artworks";
	}

	@GetMapping("/artworks/{id}")
	public String getArtworkDetails(@PathVariable Long id, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "12") int size, @RequestParam(defaultValue = "createdAt") String sort,
			Model model, HttpServletRequest request) {
		if (id == null) {
			return "redirect:/artworks";
		}
		Optional<Artwork> optionalArtwork = artworkService.findById(id);
		if (optionalArtwork.isPresent()) {
			Artwork artwork = optionalArtwork.get();
			model.addAttribute("artwork", artwork);
			model.addAttribute("find7RandomArtworks", artworkService.find7RandomArtworks());
			if (page < 1) {
				return "redirect:/artworks/" + artwork.getId() + "?page=1";
			}

			// REVIEWS START
			Sort sortBy;
			switch (sort) {
			case "rating":
				sortBy = Sort.by(Sort.Direction.DESC, "rating");
				break;
			case "ratingAsc":
				sortBy = Sort.by(Sort.Direction.ASC, "rating");
				break;
			case "createdAtAsc":
				sortBy = Sort.by(Sort.Direction.ASC, "createdAt");
				break;
			default:
				sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
			}
			Pageable pageable = PageRequest.of(page - 1, size, sortBy);
			Page<Review> reviewsPage = reviewService.getReviewsByArtworkPage(artwork.getId(), pageable);
			model.addAttribute("sort", sort);
			model.addAttribute("sortCreatedAt", sort.equals("createdAt"));
			model.addAttribute("sortRating", sort.equals("rating"));
			model.addAttribute("sortRatingAsc", sort.equals("ratingAsc"));
			model.addAttribute("sortCreatedAtAsc", sort.equals("createdAtAsc"));

			// Verify page limit
			if (reviewsPage.getTotalPages() > 0 && page > reviewsPage.getTotalPages()) {
				return "redirect:/artworks/" + artwork.getId() + "?page=" + (reviewsPage.getTotalPages());
			}

			// Reviews range calculation
			int totalReviews = (int) reviewsPage.getTotalElements();
			if (totalReviews > 0) {
				int start = reviewsPage.getNumber() * reviewsPage.getSize() + 1;
				int end = Math.min(start + reviewsPage.getNumberOfElements() - 1, totalReviews);

				model.addAttribute("totalReviews", totalReviews);
				model.addAttribute("range", String.format("%02d-%02d", start, end));
				model.addAttribute("reviewsPage", reviewsPage);
			} else {
				model.addAttribute("reviewsEmpty", true);
			}
			model.addAttribute("previousPage", reviewsPage.hasPrevious() ? page - 1 : null);
			model.addAttribute("currentPage", page);
			model.addAttribute("nextPage", reviewsPage.hasNext() ? page + 1 : null);

			List<Artwork> find7RandomArtworks = artworkService.find7RandomArtworks();
			Principal principal = request.getUserPrincipal();
			if (principal != null) {
				String userName = principal.getName();
				User user = userService.findByEmail(userName).orElseGet(() -> userService.findByName(userName)
						.orElseThrow(() -> new UsernameNotFoundException("User not found")));
				model.addAttribute("isFavoriteArtwork", userService.artworkIsFavorite(userName, artwork.getId()));

				Optional<Review> userReview = reviewService.findByUserAndArtwork(user, artwork);
				if (userReview.isPresent()) {
					Review review = userReview.get();
					Map<String, Object> reviewMap = new HashMap<>();
					reviewMap.put("rating", review.getRating());
					reviewMap.put("comment", review.getComment());
					reviewMap.put("is1", review.getRating() == 1.0);
					reviewMap.put("is2", review.getRating() == 2.0);
					reviewMap.put("is3", review.getRating() == 3.0);
					reviewMap.put("is4", review.getRating() == 4.0);
					reviewMap.put("is5", review.getRating() == 5.0);
					model.addAttribute("userReview", reviewMap);
				}

				List<Map<String, Object>> find7RandomArtworksWithFavStatus = find7RandomArtworks.stream().map(aw -> {
					Map<String, Object> map = new HashMap<>();
					map.put("id", aw.getId());
					map.put("title", aw.getTitle());
					map.put("artist", aw.getArtist());
					map.put("museum", artwork.getMuseum());
					map.put("averageRating", aw.getAverageRating());
					map.put("isFavorite", userService.artworkIsFavorite(principal.getName(), aw.getId()));
					return map;
				}).collect(Collectors.toList());
				model.addAttribute("find7RandomArtworks", find7RandomArtworksWithFavStatus);
			} else {
				model.addAttribute("find7RandomArtworks", find7RandomArtworks);
			}
			return "artwork-details";
		} else {
			return "error/404";
		}
	}

	@GetMapping("/artwork/favourite/add/{id}")
	public String addFavorite(@PathVariable Long id, Principal principal, @RequestHeader(required = false) String referer) {
		if (principal != null) {
			User user = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName())
							.orElseThrow(() -> new UsernameNotFoundException("User not found")));
			Optional<Artwork> optionalArtwork = artworkService.findById(id);
			if (optionalArtwork.isPresent()) {
				Artwork artwork = optionalArtwork.get();
				user.getFavoriteArtworks().add(artwork);
				userService.save(user);
			}
		}
		return "redirect:" + referer;
	}

	@GetMapping("/artwork/favourite/delete/{id}")
	public String removeFavorite(@PathVariable Long id, @RequestParam(defaultValue = "1") int page, Principal principal,
			@RequestHeader(required = false) String referer) {
		if (principal != null) {
			User user = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName())
							.orElseThrow(() -> new UsernameNotFoundException("User not found")));
			Optional<Artwork> optionalArtwork = artworkService.findById(id);
			if (optionalArtwork.isPresent()) {
				Artwork artwork = optionalArtwork.get();
				user.getFavoriteArtworks().remove(artwork);
				userService.save(user);
			}
		}
		return "redirect:" + referer;
	}

	@GetMapping("/artwork/image/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
		Optional<Artwork> artwork = artworkService.findById(id);
		if (artwork.isPresent() && artwork.get().getImage() != null) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(artwork.get().getImage());
		} else {
			Resource resource = resourceLoader.getResource("classpath:static/assets/img/TBD.png");
			byte[] defaultImage = StreamUtils.copyToByteArray(resource.getInputStream());
			return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(defaultImage);
		}
	}

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
		} else {
			model.addAttribute("logged", false);
		}
		model.addAttribute("header02", true);
	}
}
