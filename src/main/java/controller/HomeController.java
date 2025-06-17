package controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import model.Artwork;
import service.ArtistService;
import service.ArtworkService;
import service.UserService;

@Controller
public class HomeController {

	@Autowired
	private ArtworkService artworkService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ArtistService artistService;

	@GetMapping("/")
	public String home(Model model, HttpServletRequest request) {
		List<Artwork> top7artworks = artworkService.findTop7ByOrderByAverageRatingDesc();
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			List<Map<String, Object>> top7artworksWithFavStatus = top7artworks.stream().map(artwork -> {
				Map<String, Object> map = new HashMap<>();
				map.put("id", artwork.getId());
				map.put("title", artwork.getTitle());
				map.put("artist", artwork.getArtist());
				map.put("museum", artwork.getMuseum());
				map.put("averageRating", artwork.getAverageRating());
				map.put("isFavorite", userService.artworkIsFavorite(principal.getName(), artwork.getId()));
				return map;
			}).collect(Collectors.toList());
			model.addAttribute("top7Artworks", top7artworksWithFavStatus);
		} else {
			model.addAttribute("top7Artworks", top7artworks);
		}
		List<Artwork> find7RandomArtworks = artworkService.find7RandomArtworks();
		if (principal != null) {
			List<Map<String, Object>> find7RandomArtworksWithFavStatus = find7RandomArtworks.stream().map(artwork -> {
				Map<String, Object> map = new HashMap<>();
				map.put("id", artwork.getId());
				map.put("title", artwork.getTitle());
				map.put("artist", artwork.getArtist());
				map.put("museum", artwork.getMuseum());
				map.put("averageRating", artwork.getAverageRating());
				map.put("isFavorite", userService.artworkIsFavorite(principal.getName(), artwork.getId()));
				return map;
			}).collect(Collectors.toList());
			model.addAttribute("find7RandomArtworks", find7RandomArtworksWithFavStatus);
		} else {
			model.addAttribute("find7RandomArtworks", find7RandomArtworks);
		}
		model.addAttribute("top10Artists", artistService.findTop10ArtistsByAverageArtworkRating());
		model.addAttribute("header01", true);
		return "index";
	}

	@GetMapping("/faq")
	public String faq(Model model) {
		model.addAttribute("header04", true);
		return "faq";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("header04", true);
		return "about";
	}

	@GetMapping("/contact")
	public String contact(Model model) {
		model.addAttribute("header04", true);
		return "contact";
	}

	@GetMapping("/terms-condition")
	public String termsCondition(Model model) {
		model.addAttribute("header04", true);
		return "terms-condition";
	}

	@GetMapping("/privacy-policy")
	public String privacyPolicy(Model model) {
		model.addAttribute("header04", true);
		return "privacy-policy";
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
	}

}
