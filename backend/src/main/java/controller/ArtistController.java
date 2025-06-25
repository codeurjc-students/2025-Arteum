package controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import model.Artist;
import model.Artwork;
import model.Museum;
import service.ArtistService;
import service.ArtworkService;
import service.UserService;

@Controller
public class ArtistController {
	@Autowired
	private ArtistService artistService;

	@Autowired
	private ArtworkService artworkService;

	@Autowired
	private UserService userService;

	@Autowired
	private ResourceLoader resourceLoader;

	@GetMapping("/artists")
	public String getArtistsPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "6") int size,
			@RequestParam(required = false) String search, @RequestParam(defaultValue = "title") String sort,
			@RequestParam(required = false) List<String> nationalityFilters,
			@RequestParam(required = false) List<Integer> centuries, Model model, HttpServletRequest request) {
		// Verify page limit
		if (page < 1) {
			StringBuilder redirectUrl = new StringBuilder("redirect:/artists?page=1");
			if (search != null && !search.isBlank()) {
				redirectUrl.append("&search=").append(URLEncoder.encode(search, StandardCharsets.UTF_8));
			}
			if (sort != null && !sort.isBlank()) {
				redirectUrl.append("&sort=").append(URLEncoder.encode(sort, StandardCharsets.UTF_8));
			}
			if (nationalityFilters != null) {
				for (String nationality : nationalityFilters) {
					redirectUrl.append("&nationalityFilters=").append(nationality);
				}
			}
			return redirectUrl.toString();
		}

		// Pageable artists
		Sort sortBy;
		switch (sort) {
		case "dateOfBirth":
			sortBy = Sort.by(Sort.Direction.ASC, "dateOfBirth");
			break;
		case "nationality":
			sortBy = Sort.by(Sort.Direction.ASC, "nationality");
			break;
		default:
			sortBy = Sort.by(Sort.Direction.ASC, "name");
		}

		List<Artist> allArtists = artistService.findAll();
		Map<String, Long> nationalityCount = allArtists.stream()
				.filter(a -> a.getNationality() != null && !a.getNationality().isBlank())
				.collect(Collectors.groupingBy(Artist::getNationality, Collectors.counting()));

		List<String> topNationalities = nationalityCount.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10).map(Map.Entry::getKey)
				.toList();
		model.addAttribute("nationalityFilters", topNationalities.stream().map(
				nat -> Map.of("name", nat, "selected", nationalityFilters != null && nationalityFilters.contains(nat)))
				.toList());

		Map<Integer, Long> centuryCount = allArtists.stream().filter(a -> a.getDateOfBirth() != null)
				.map(a -> getCentury(a.getDateOfBirth())).filter(c -> c > 0)
				.collect(Collectors.groupingBy(c -> c, Collectors.counting()));

		List<Integer> topCenturies = centuryCount.entrySet().stream()
				.sorted(Map.Entry.<Integer, Long>comparingByValue().reversed()).limit(10).map(Map.Entry::getKey)
				.sorted(Comparator.reverseOrder()).toList();

		model.addAttribute("centuryFilters", topCenturies.stream().map(c -> Map.of("century", c, "label", "Siglo " + c,
				"selected", centuries != null && centuries.contains(c))).toList());

		Pageable pageable = PageRequest.of(page - 1, size, sortBy);

		if (search != null && !search.trim().isEmpty()) {
			boolean isAdmin = request.isUserInRole("ADMIN");

			if (isAdmin && search.trim().matches("\\d+")) {
				Long id = Long.parseLong(search.trim());
				Optional<Artist> artistOpt = artistService.findById(id);
				if (artistOpt.isPresent()) {
					Artist artist = artistOpt.get();
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
					model.addAttribute("artistsPage", artistData);
					model.addAttribute("totalArtists", 1);
					model.addAttribute("range", "01-01");
					model.addAttribute("sort", sort);
					model.addAttribute("search", search);
					model.addAttribute("currentPage", 1);
					model.addAttribute("previousPage", null);
					model.addAttribute("nextPage", null);
					return "artists";
				}
			}
		}

		Comparator<Artist> comparator;
		switch (sort) {
		case "dateOfBirth":
			comparator = Comparator.comparing(Artist::getDateOfBirth, Comparator.nullsLast(Comparator.naturalOrder()));
			break;
		case "nationality":
			comparator = Comparator.comparing(Artist::getNationality, Comparator.nullsLast(Comparator.naturalOrder()));
			break;
		default: // "name"
			comparator = Comparator.comparing(Artist::getName, Comparator.nullsLast(Comparator.naturalOrder()));
		}

		List<Artist> allFiltered = artistService.getArtistsPage(search, nationalityFilters);
		allFiltered = allFiltered.stream().sorted(comparator).toList();

		if (centuries != null && !centuries.isEmpty()) {
			allFiltered = allFiltered.stream()
					.filter(a -> a.getDateOfBirth() != null && centuries.contains(getCentury(a.getDateOfBirth())))
					.toList();
		}

		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), allFiltered.size());
		Page<Artist> artistsPage = new PageImpl<>(allFiltered.subList(start, end), pageable, allFiltered.size());

		model.addAttribute("sort", sort);
		model.addAttribute("sortName", sort.equals("name"));
		model.addAttribute("sortBirthDate", sort.equals("dateOfBirth"));
		model.addAttribute("sortNationality", sort.equals("nationality"));

		if (search != null && !search.isEmpty() && !search.isBlank()) {
			model.addAttribute("search", search);
		}

		// Verify page limit
		if (artistsPage.getTotalPages() > 0 && page > artistsPage.getTotalPages()) {
			StringBuilder redirectUrl = new StringBuilder("redirect:/artists?page=" + artistsPage.getTotalPages());
			if (search != null && !search.isBlank()) {
				redirectUrl.append("&search=").append(URLEncoder.encode(search, StandardCharsets.UTF_8));
			}
			if (sort != null && !sort.isBlank()) {
				redirectUrl.append("&sort=").append(URLEncoder.encode(sort, StandardCharsets.UTF_8));
			}
			if (nationalityFilters != null) {
				for (String nationality : nationalityFilters) {
					redirectUrl.append("&nationalityFilters=").append(nationality);
				}
			}
			return redirectUrl.toString();
		}

		// Artists range calculation
		int totalArtists = (int) artistsPage.getTotalElements();
		if (totalArtists > 0) {

			List<Map<String, Object>> artistsWithYear = new ArrayList<Map<String, Object>>();
			for (Artist artist : artistsPage) {
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

			model.addAttribute("totalArtists", totalArtists);
			model.addAttribute("range", String.format("%02d-%02d", start, end));
			model.addAttribute("artistsPage", artistsWithYear);
		} else {
			model.addAttribute("totalArtists", 0);
			model.addAttribute("range", 0);
			model.addAttribute("artistsEmpty", true);
		}

		// Page numbers
		model.addAttribute("previousPage", artistsPage.hasPrevious() ? page - 1 : null);
		model.addAttribute("currentPage", page);
		model.addAttribute("nextPage", artistsPage.hasNext() ? page + 1 : null);
		return "artists";
	}

	@GetMapping("/artists/{id}")
	public String getArtistDetails(@PathVariable Long id, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "6") int size, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "title") String sort, @RequestParam(required = false) List<Long> museumIds,
			@RequestParam(required = false) List<String> ratingRanges, Model model, HttpServletRequest request) {
		if (id == null) {
			return "redirect:/artists";
		}
		Artist artist = artistService.getArtistById(id);
		// Verify page limit
		if (page < 1) {
			StringBuilder redirectUrl = new StringBuilder("redirect:/artists?page=1");
			if (search != null && !search.isBlank()) {
				redirectUrl.append("&search=").append(URLEncoder.encode(search, StandardCharsets.UTF_8));
			}
			if (sort != null && !sort.isBlank()) {
				redirectUrl.append("&sort=").append(URLEncoder.encode(sort, StandardCharsets.UTF_8));
			}
			if (museumIds != null) {
				for (Long museumId : museumIds) {
					redirectUrl.append("&museumIds=").append(museumId);
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

		List<Artwork> allArtworks = artworkService.getArtworksByArtistPage(id, search);
		if (!allArtworks.isEmpty()) {
			Map<Museum, Long> topMuseums = allArtworks.stream().filter(a -> a.getMuseum() != null)
					.collect(Collectors.groupingBy(Artwork::getMuseum, Collectors.counting()));

			List<Museum> mostFrequentMuseums = topMuseums.entrySet().stream()
					.sorted(Comparator.comparing(Map.Entry<Museum, Long>::getValue).reversed()
							.thenComparing(entry -> entry.getKey().getName(), String.CASE_INSENSITIVE_ORDER))
					.limit(10).map(Map.Entry::getKey).toList();

			Set<Long> selectedMuseumIds = museumIds != null ? new HashSet<>(museumIds) : Set.of();

			model.addAttribute("topMuseums", mostFrequentMuseums.stream().map(m -> {
				Map<String, Object> mMap = new HashMap<>();
				mMap.put("id", m.getId());
				mMap.put("name", m.getName());
				mMap.put("selected", selectedMuseumIds.contains(m.getId()));
				return mMap;
			}).toList());
		}

		// Pageable artworks
		Pageable pageable = PageRequest.of(page - 1, size, sortBy);

		if (search != null && !search.trim().isEmpty()) {
			boolean isAdmin = request.isUserInRole("ADMIN");

			if (isAdmin && search.trim().matches("\\d+")) {
				Long searchID = Long.parseLong(search.trim());
				Optional<Artwork> artworkOpt = artworkService.findById(searchID);
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
			artworksPage = artworkService.getArtworksByArtistPage(id, search, ratingRangesParsed, museumIds, pageable);
		} else {
			List<Map<String, Object>> ratingRangeCheckboxes = List.of(Map.of("min", 4, "max", 5),
					Map.of("min", 3, "max", 4), Map.of("min", 2, "max", 3), Map.of("min", 1, "max", 2),
					Map.of("min", 0, "max", 1));
			model.addAttribute("ratingRanges", ratingRangeCheckboxes);
			artworksPage = artworkService.getArtworksByArtistPageAndMuseum(id, search, museumIds, pageable);
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
			StringBuilder redirectUrl = new StringBuilder(
					"redirect:/artists/" + id + "?page=" + artworksPage.getTotalPages());
			if (search != null && !search.isBlank()) {
				redirectUrl.append("&search=").append(URLEncoder.encode(search, StandardCharsets.UTF_8));
			}
			if (sort != null && !sort.isBlank()) {
				redirectUrl.append("&sort=").append(URLEncoder.encode(sort, StandardCharsets.UTF_8));
			}
			if (museumIds != null) {
				for (Long museumId : museumIds) {
					redirectUrl.append("&museumIds=").append(museumId);
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
			if (start != end) {
				model.addAttribute("range", String.format("%02d-%02d", start, end));
			} else {
				model.addAttribute("range", start);
			}
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
			model.addAttribute("previousPage", 0);
			model.addAttribute("currentPage", 0);
			model.addAttribute("nextPage", 0);
			model.addAttribute("artworksEmpty", true);
		}

		model.addAttribute("artist", artist);
		return "artist-details";
	}

	@GetMapping("/artist/image/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
		Optional<Artist> artist = artistService.findById(id);
		if (artist.isPresent() && artist.get().getImage() != null) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(artist.get().getImage());
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
		model.addAttribute("header03", true);
	}

	private int getCentury(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int birthYear = calendar.get(Calendar.YEAR);
		return (birthYear - 1) / 100 + 1;
	}
}
