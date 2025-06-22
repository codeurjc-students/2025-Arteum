package controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
import service.ArtworkService;
import service.MuseumService;
import service.UserService;

@Controller
public class MuseumController {

	@Autowired
	private MuseumService museumService;

	@Autowired
	private ArtworkService artworkService;

	@Autowired
	private UserService userService;

	@Autowired
	private ResourceLoader resourceLoader;

	@GetMapping("/museums")
	public String getmuseumsPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "6") int size,
			@RequestParam(required = false) String search, @RequestParam(defaultValue = "title") String sort,
			Model model, HttpServletRequest request) {
		// Verify page limit
		if (page < 1) {
			return "redirect:/museums?page=1"
					+ (search != null ? "&search=" + URLEncoder.encode(search, StandardCharsets.UTF_8) : "")
					+ (sort != null ? "&sort=" + sort : "");
		}
		Sort sortBy;
		switch (sort) {
		case "founded":
			sortBy = Sort.by(Sort.Direction.ASC, "founded");
			break;
		case "location":
			sortBy = Sort.by(Sort.Direction.ASC, "location");
			break;
		default:
			sortBy = Sort.by(Sort.Direction.ASC, "name");
		}

	    String[] rawFilters = request.getParameterValues("locationFilters");
	    List<String> locationFilters = rawFilters != null
	        ? Arrays.stream(rawFilters)
	                .map(String::trim)
	                .collect(Collectors.toList())
	        : Collections.emptyList();

	    List<Museum> allMuseums = museumService.findAll();

	    Map<String, Long> locationCount = allMuseums.stream()
	        .map(Museum::getLocation)
	        .filter(Objects::nonNull)
	        .map(String::trim)
	        .filter(s -> !s.isEmpty())
	        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

	    List<String> topLocations = locationCount.entrySet().stream()
	        .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
	        .limit(10)
	        .map(Map.Entry::getKey)
	        .toList();

	    List<Map<String, Object>> locationFiltersList = topLocations.stream()
	        .map(loc -> Map.<String, Object>of(
	            "name", loc,
	            "selected", locationFilters.stream()
	                              .anyMatch(f -> f.equalsIgnoreCase(loc))
	        ))
	        .toList();

	    model.addAttribute("locationFiltersList", locationFiltersList);

		if (search != null && !search.isBlank()) {
			model.addAttribute("search", search);
		}
		
		Pageable pageable = PageRequest.of(page - 1, size, sortBy);

		if (search != null && !search.trim().isEmpty()) {
			boolean isAdmin = request.isUserInRole("ADMIN");

			if (isAdmin && search.trim().matches("\\d+")) {
				Long id = Long.parseLong(search.trim());
				Optional<Museum> museumOpt = museumService.findById(id);
				if (museumOpt.isPresent()) {
					model.addAttribute("museumsPage", new PageImpl<>(List.of(museumOpt.get()), pageable, 1));
					model.addAttribute("totalMuseums", 1);
					model.addAttribute("range", "01-01");
					model.addAttribute("sort", sort);
					model.addAttribute("search", search);
					model.addAttribute("currentPage", 1);
					model.addAttribute("previousPage", null);
					model.addAttribute("nextPage", null);
					return "museums";
				}
			}
		}

		Page<Museum> museumsPage = museumService.getMuseumsPage(search, locationFilters, pageable);
		model.addAttribute("sort", sort);
		model.addAttribute("sortName", sort.equals("name"));
		model.addAttribute("sortFounded", sort.equals("founded"));
		model.addAttribute("sortLocation", sort.equals("location"));
		// Verify page limit
		if (museumsPage.getTotalPages() > 0 && page > museumsPage.getTotalPages()) {
			return "redirect:/museums?page=" + museumsPage.getTotalPages()
					+ (search != null ? "&search=" + URLEncoder.encode(search, StandardCharsets.UTF_8) : "")
					+ (sort != null ? "&sort=" + sort : "");
		}

		// museums range calculation
		int totalmuseums = (int) museumsPage.getTotalElements();
		if (totalmuseums > 0) {
			int start = museumsPage.getNumber() * museumsPage.getSize() + 1;
			int end = Math.min(start + museumsPage.getNumberOfElements() - 1, totalmuseums);

			model.addAttribute("totalMuseums", totalmuseums);
			model.addAttribute("range", String.format("%02d-%02d", start, end));
			model.addAttribute("museumsPage", museumsPage);
		} else {
			model.addAttribute("totalMuseums", 0);
			model.addAttribute("range", 0);
			model.addAttribute("museumsEmpty", true);
		}

		// Page numbers
		model.addAttribute("previousPage", museumsPage.hasPrevious() ? page - 1 : null);
		model.addAttribute("currentPage", page);
		model.addAttribute("nextPage", museumsPage.hasNext() ? page + 1 : null);
		return "museums";
	}

	@GetMapping("/museum/{id}")
	public String getMuseumDetails(@PathVariable Long id, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "6") int size, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "title") String sort, @RequestParam(required = false) List<Long> artist,
			@RequestParam(required = false) List<String> ratingRanges, Model model, HttpServletRequest request) {
		if (id == null) {
			return "redirect:/museums";
		}

		Optional<Museum> optionalMuseum = museumService.findById(id);
		if (optionalMuseum.isEmpty())
			return "redirect:/museums";

		Museum museum = optionalMuseum.get();
		model.addAttribute("museum", museum);

		// Sorting logic
		Sort sortBy = switch (sort) {
		case "averageRating" -> Sort.by(Sort.Direction.DESC, "averageRating");
		case "artist.name" -> Sort.by(Sort.Direction.ASC, "artist.name");
		case "creationYear" -> Sort.by(Sort.Direction.ASC, "creationYear");
		case "museum.name" -> Sort.by(Sort.Direction.ASC, "museum.name");
		default -> Sort.by(Sort.Direction.ASC, "title");
		};
		model.addAttribute("sort", sort);
		model.addAttribute("sortTitle", sort.equals("title"));
		model.addAttribute("sortRating", sort.equals("averageRating"));
		model.addAttribute("sortArtist", sort.equals("artist.name"));
		model.addAttribute("sortYear", sort.equals("creationYear"));
		model.addAttribute("sortMuseum", sort.equals("museum.name"));

		// Artist filter UI
		List<Artwork> allArtworks = museum.getArtworks();
		if (!allArtworks.isEmpty()) {
			Map<Artist, Long> topArtists = allArtworks.stream()
					.collect(Collectors.groupingBy(Artwork::getArtist, Collectors.counting()));
			model.addAttribute("topArtists",
					topArtists.entrySet().stream()
							.sorted(Map.Entry.<Artist, Long>comparingByValue().reversed()
									.thenComparing(e -> e.getKey().getName(), String.CASE_INSENSITIVE_ORDER))
							.limit(10).map(e -> Map.of("id", e.getKey().getId(), "name", e.getKey().getName(),
									"selected", artist != null && artist.contains(e.getKey().getId())))
							.toList());
		} else {
			model.addAttribute("topArtistsEmpty", true);
		}

		// Rating checkboxes
		List<double[]> ratingRangesParsed = new ArrayList<>();
		if (ratingRanges != null && !ratingRanges.isEmpty()) {
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
		} else {
			List<Map<String, Object>> ratingRangeCheckboxes = List.of(Map.of("min", 4, "max", 5),
					Map.of("min", 3, "max", 4), Map.of("min", 2, "max", 3), Map.of("min", 1, "max", 2),
					Map.of("min", 0, "max", 1));
			model.addAttribute("ratingRanges", ratingRangeCheckboxes);
		}

		if (search != null && !search.isEmpty() && !search.isBlank()) {
			model.addAttribute("search", search);
		}

		Pageable pageable = PageRequest.of(page - 1, size, sortBy);

		// Búsqueda directa por ID si admin
		if (search != null && request.isUserInRole("ADMIN") && search.trim().matches("\\d+")) {
			Long artworkId = Long.parseLong(search.trim());
			Optional<Artwork> artworkOpt = artworkService.findById(artworkId);
			if (artworkOpt.isPresent()) {
				model.addAttribute("artworksPage", new PageImpl<>(List.of(artworkOpt.get()), pageable, 1));
				model.addAttribute("totalArworks", 1);
				model.addAttribute("range", "01-01");
				return "museum-details";
			}
		}

		// Filtro principal
		Page<Artwork> artworksPage = artworkService.getArtworksByMuseumPage(id, search, artist, ratingRangesParsed,
				pageable);

		// Favoritos
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
		
		// Arworks range calculation
		int totalartworks = (int) artworksPage.getTotalElements();
		if (totalartworks > 0) {
			int start = artworksPage.getNumber() * artworksPage.getSize() + 1;
			int end = Math.min(start + artworksPage.getNumberOfElements() - 1, totalartworks);

			model.addAttribute("totalArtworks", totalartworks);
			model.addAttribute("range", String.format("%02d-%02d", start, end));
		} else {
			model.addAttribute("totalArtworks", 0);
			model.addAttribute("range", 0);
			model.addAttribute("artworksEmpty", true);
		}

		// Page numbers
		model.addAttribute("previousPage", artworksPage.hasPrevious() ? page - 1 : null);
		model.addAttribute("currentPage", page);
		model.addAttribute("nextPage", artworksPage.hasNext() ? page + 1 : null);
		return "museum-details";
	}

	@GetMapping("/museum/image/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
		Optional<Museum> museum = museumService.findById(id);
		if (museum.isPresent() && museum.get().getImage() != null) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(museum.get().getImage());
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
		model.addAttribute("header05", true);
	}

}
