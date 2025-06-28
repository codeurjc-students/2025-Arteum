package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Artist;
import model.Artwork;
import model.Review;
import model.User;
import service.ArtworkService;
import service.ReviewService;
import service.UserPdfExportService;
import service.UserService;

@Controller
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private ArtworkService artworkService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private UserPdfExportService userPdfExportService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ResourceLoader resourceLoader;

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

	@GetMapping("/login")
	public String login(@RequestParam(required = false) String error, Model model) {
		if ("true".equals(error)) {
			model.addAttribute("errorMessage", "Usuario/email o contraseña incorrectos.");
		}
		return "login";
	}

	@GetMapping("/logout")
	public void logout(HttpServletResponse response, HttpServletRequest request) throws IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, null, auth);
		}
		response.sendRedirect("/");
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	// FALTA HTML
	@GetMapping("/forgot-password")
	public String forgotPassword() {
		return "forgot-password";
	}

	@GetMapping("/users")
	public String getUsersPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "12") int size,
			@RequestParam(required = false) String search, @RequestParam(defaultValue = "createdAt") String sort,
			Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			// Verify page limit
			if (page < 1) {
				return "redirect:/users?page=1"
						+ (search != null ? "&search=" + URLEncoder.encode(search, StandardCharsets.UTF_8) : "")
						+ (sort != null ? "&sort=" + sort : "");
			}
			Sort sortBy;
			switch (sort) {
			case "nameAsc":
				sortBy = Sort.by(Sort.Direction.ASC, "name");
				break;
			case "nameDesc":
				sortBy = Sort.by(Sort.Direction.DESC, "name");
				break;
			default:
				sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
			}
			// Pageable users
			Pageable pageable = PageRequest.of(page - 1, size, sortBy);

			if (search != null && !search.trim().isEmpty()) {
				boolean isAdmin = request.isUserInRole("ADMIN");

				if (isAdmin && search.trim().matches("\\d+")) {
					Long id = Long.parseLong(search.trim());
					Optional<User> userOpt = userService.findById(id);
					if (userOpt.isPresent()) {
						model.addAttribute("usersPage", new PageImpl<>(List.of(userOpt.get()), pageable, 1));
						model.addAttribute("totalUsers", 1);
						model.addAttribute("range", "01-01");
						model.addAttribute("sort", sort);
						model.addAttribute("search", search);
						model.addAttribute("currentPage", 1);
						model.addAttribute("previousPage", null);
						model.addAttribute("nextPage", null);
						return "users";
					}
				}
			}

			Page<User> usersPage = userService.getUsersPage(search, pageable);
			model.addAttribute("sort", sort);
			model.addAttribute("sortName", sort.equals("nameDesc"));
			model.addAttribute("sortNameAsc", sort.equals("nameAsc"));
			model.addAttribute("sortCreatedAt", sort.equals("createdAt"));
			// Verify page limit
			if (usersPage.getTotalPages() > 0 && page > usersPage.getTotalPages()) {
				return "redirect:/users?page=" + usersPage.getTotalPages()
						+ (search != null ? "&search=" + URLEncoder.encode(search, StandardCharsets.UTF_8) : "")
						+ (sort != null ? "&sort=" + sort : "");
			}

			// users range calculation
			int totalusers = (int) usersPage.getTotalElements();
			if (totalusers > 0) {
				int start = usersPage.getNumber() * usersPage.getSize() + 1;
				int end = Math.min(start + usersPage.getNumberOfElements() - 1, totalusers);

				User user = userService.findByEmail(principal.getName())
						.orElseGet(() -> userService.findByName(principal.getName())
								.orElseThrow(() -> new UsernameNotFoundException("User not found")));
				if (user.getRoles() != null && !user.getRoles().contains("ADMIN")) {
					List<Map<String, Object>> usersWithStatus = usersPage.stream().map(follower -> {
						Map<String, Object> map = new HashMap<>();
						map.put("id", follower.getId());
						map.put("name", follower.getName());
						map.put("image", follower.getImage());
						map.put("location", follower.getLocation());
						map.put("createdAt", follower.getCreatedAt());
						map.put("isFollowing", userService.isFollowing(user.getId(), follower.getId()));
						return map;
					}).toList();
					model.addAttribute("usersPage", usersWithStatus);
				} else {
					model.addAttribute("usersPage", usersPage);
				}
				model.addAttribute("totalUsers", totalusers);
				model.addAttribute("range", String.format("%02d-%02d", start, end));
			} else {
				model.addAttribute("totalUsers", 0);
				model.addAttribute("range", 0);
				model.addAttribute("usersEmpty", true);
			}
			if (search != null && !search.isEmpty() && !search.isBlank()) {
				model.addAttribute("search", search);
			}

			// Page numbers
			model.addAttribute("previousPage", usersPage.hasPrevious() ? page - 1 : null);
			model.addAttribute("currentPage", page);
			model.addAttribute("nextPage", usersPage.hasNext() ? page + 1 : null);

			model.addAttribute("header06", true);
			return "users";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/public-profile/{username}")
	public String publicProfile(@PathVariable String username, Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User user = userService.findByEmail(username)
					.orElseGet(() -> userService.findByName(username).orElse(null));
			if (user != null) {
				if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
					return "redirect:/";
				}
				User userprincipal = userService.findByEmail(principal.getName())
						.orElseGet(() -> userService.findByName(principal.getName()).orElse(null));
				if (user.getId() != userprincipal.getId()) {
					model.addAttribute("isNotMyProfile", true);
					model.addAttribute("isFollowing", userService.isFollowing(userprincipal.getId(), user.getId()));
				}

				model.addAttribute("name", user.getName());
				model.addAttribute("email", user.getEmail());
				model.addAttribute("biography", user.getBiography());
				model.addAttribute("location", user.getLocation());
				model.addAttribute("created", user.getCreatedAt());

				if (request.getUserPrincipal() != null) {
					model.addAttribute("id", user.getId());
				}

				model.addAttribute("followers", user.getFollowers().size());
				model.addAttribute("follows", user.getFollowing().size());

				List<Review> reviews = user.getReviews();
				model.addAttribute("reviews", reviews.size());
				if (!reviews.isEmpty()) {
					double averageRating = reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
					BigDecimal roundedAverage = BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP);
					model.addAttribute("reviews_avg", roundedAverage);
				} else {
					model.addAttribute("reviews_avg", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
				}
				// Artworks range calculation
				int totalArtworks = (int) user.getFavoriteArtworks().size();
				if (totalArtworks > 0) {
					if (request.getUserPrincipal() != null) {
						List<Map<String, Object>> artworksWithFavStatus = user.getFavoriteArtworks().stream()
								.sorted(Comparator.comparing(Artwork::getAverageRating).reversed()).limit(8)
								.map(artwork -> {
									Map<String, Object> map = new HashMap<>();
									map.put("id", artwork.getId());
									map.put("title", artwork.getTitle());
									map.put("artist", artwork.getArtist());
									map.put("museum", artwork.getMuseum());
									map.put("averageRating", artwork.getAverageRating());
									map.put("isFavorite", userService
											.artworkIsFavorite(request.getUserPrincipal().getName(), artwork.getId()));
									return map;
								}).collect(Collectors.toList());

						model.addAttribute("artworks", artworksWithFavStatus);
					} else {
						model.addAttribute("artworks",
								user.getFavoriteArtworks().stream().limit(4).collect(Collectors.toList()));
					}
				} else {
					model.addAttribute("artworksEmpty", true);
				}
				return "public-profile";
			} else {
				return "redirect:/";
			}
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/user/recommended-artworks")
	public String showRecommendedArtworks(Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User user = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName())
							.orElseThrow(() -> new UsernameNotFoundException("User not found")));

			List<Artwork> recommendedArtworks = recommendArtworks(user.getId(), 12);
			List<Map<String, Object>> top12artworksWithFavStatus = recommendedArtworks.stream().map(artwork -> {
				Map<String, Object> map = new HashMap<>();
				map.put("id", artwork.getId());
				map.put("title", artwork.getTitle());
				map.put("artist", artwork.getArtist());
				map.put("museum", artwork.getMuseum());
				map.put("averageRating", artwork.getAverageRating());
				map.put("isFavorite", userService.artworkIsFavorite(principal.getName(), artwork.getId()));
				return map;
			}).collect(Collectors.toList());
			model.addAttribute("recommendedArtworks", top12artworksWithFavStatus);
			model.addAttribute("header07", true);
			return "recommended-artworks";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/dashboard-profile")
	public String dashboardProfile(Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User user = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName())
							.orElseThrow(() -> new UsernameNotFoundException("User not found")));
			model.addAttribute("name", user.getName());
			model.addAttribute("email", user.getEmail());
			model.addAttribute("created", user.getCreatedAt());
			model.addAttribute("biography", user.getBiography());
			model.addAttribute("location", user.getLocation());
			model.addAttribute("hasimage", user.getImage() != null);
			return "dashboard-profile";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/user/generate-pdf")
	public ResponseEntity<byte[]> downloadUserPdf(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User user = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName()).orElse(null));

			byte[] pdfBytes = userPdfExportService.generateUserPdf(user.getId());
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuario_" + user.getName() + ".pdf")
					.body(pdfBytes);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/user/stats")
	public String userStats(Model model, HttpServletRequest request) throws JsonProcessingException {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User user = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName())
							.orElseThrow(() -> new UsernameNotFoundException("User not found")));
			model.addAttribute("name", user.getName());

			Map<Integer, Long> starsData = reviewService.countStarsGivenByUser(user.getId());
			model.addAttribute("starsData", new ObjectMapper().writeValueAsString(starsData));

			long following = user.getFollowing().size();
			model.addAttribute("following", following);

			long followers = user.getFollowers().size();
			model.addAttribute("followers", followers);

			double avgGiven = round(user.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0.0), 2);
			model.addAttribute("avgGiven", avgGiven);
			model.addAttribute("numReviews", user.getReviews().size());

			Map<String, Long> favByArtist = user.getFavoriteArtworks().stream().filter(a -> a.getArtist() != null)
					.collect(Collectors.groupingBy(a -> a.getArtist().getName(), Collectors.counting()));
			Map<String, Long> top10favByArtist = favByArtist.entrySet().stream()
					.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10).collect(Collectors
							.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			model.addAttribute("top10favByArtist", new ObjectMapper().writeValueAsString(top10favByArtist));

			Map<String, Long> artistCounts = user.getReviews().stream()
					.collect(Collectors.groupingBy(r -> r.getArtwork().getArtist().getName(), Collectors.counting()));
			Map<String, Long> top10Artists = artistCounts.entrySet().stream()
					.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10).collect(Collectors
							.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			model.addAttribute("artistCounts", new ObjectMapper().writeValueAsString(top10Artists));

			return "user-stats";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/user/reviews")
	public String userReviews(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "12") int size,
			@RequestParam(defaultValue = "createdAt") String sort, Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User user = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName())
							.orElseThrow(() -> new UsernameNotFoundException("User not found")));
			if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
				return "redirect:/";
			}
			if (page < 1) {
				return "redirect:/user/reviews?page=1";
			}
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
			Page<Review> reviewsPage = reviewService.getReviewsByUser(user.getId(), pageable);
			model.addAttribute("sort", sort);
			model.addAttribute("sortCreatedAt", sort.equals("createdAt"));
			model.addAttribute("sortRating", sort.equals("rating"));
			model.addAttribute("sortRatingAsc", sort.equals("ratingAsc"));
			model.addAttribute("sortCreatedAtAsc", sort.equals("createdAtAsc"));

			// Verify page limit
			if (reviewsPage.getTotalPages() > 0 && page > reviewsPage.getTotalPages()) {
				return "redirect:/user/reviews?page=" + (reviewsPage.getTotalPages());
			}

			// Reviews range calculation
			int totalReviews = (int) reviewsPage.getTotalElements();
			if (totalReviews > 0) {
				int start = reviewsPage.getNumber() * reviewsPage.getSize() + 1;
				int end = Math.min(start + reviewsPage.getNumberOfElements() - 1, totalReviews);

				List<Map<String, Object>> reviewsWithFavStatus = reviewsPage.stream().map(review -> {
					Map<String, Object> map = new HashMap<>();
					map.put("id", review.getId());
					map.put("comment",
							(review.getComment() != null && !review.getComment().isBlank()) ? review.getComment()
									: null);
					map.put("rating", review.getRating());
					map.put("artwork", review.getArtwork());
					map.put("isFavorite", userService.artworkIsFavorite(user.getName(), review.getArtwork().getId()));
					return map;
				}).collect(Collectors.toList());

				model.addAttribute("totalReviews", totalReviews);
				model.addAttribute("range", String.format("%02d-%02d", start, end));
				model.addAttribute("reviewsPage", reviewsWithFavStatus);
			} else {
				model.addAttribute("reviewsEmpty", true);
			}
			// Page numbers
			model.addAttribute("previousPage", reviewsPage.hasPrevious() ? page - 1 : null);
			model.addAttribute("currentPage", page);
			model.addAttribute("nextPage", reviewsPage.hasNext() ? page + 1 : null);
			return "reviews";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/user/followers")
	public String userFollowers(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "12") int size,
			@RequestParam(defaultValue = "createdAt") String sort, Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User user = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName())
							.orElseThrow(() -> new UsernameNotFoundException("User not found")));
			if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
				return "redirect:/";
			}
			if (page < 1) {
				return "redirect:/user/followers?page=1";
			}
			Sort sortBy;
			switch (sort) {
			case "nameAsc":
				sortBy = Sort.by(Sort.Direction.ASC, "name");
				break;
			case "nameDesc":
				sortBy = Sort.by(Sort.Direction.DESC, "name");
				break;
			default:
				sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
			}
			Pageable pageable = PageRequest.of(page - 1, size, sortBy);
			Page<User> followersPage = userService.getFollowersPage(user.getId(), pageable);
			model.addAttribute("sort", sort);
			model.addAttribute("sortName", sort.equals("nameDesc"));
			model.addAttribute("sortNameAsc", sort.equals("nameAsc"));
			model.addAttribute("sortCreatedAt", sort.equals("createdAt"));

			// Verify page limit
			if (followersPage.getTotalPages() > 0 && page > followersPage.getTotalPages()) {
				return "redirect:/user/followers?page=" + (followersPage.getTotalPages());
			}

			// Followers range calculation
			int totalFollowers = (int) followersPage.getTotalElements();
			if (totalFollowers > 0) {
				int start = followersPage.getNumber() * followersPage.getSize() + 1;
				int end = Math.min(start + followersPage.getNumberOfElements() - 1, totalFollowers);

				List<Map<String, Object>> followersWithStatus = followersPage.stream().map(follower -> {
					Map<String, Object> map = new HashMap<>();
					map.put("id", follower.getId());
					map.put("name", follower.getName());
					map.put("image", follower.getImage());
					map.put("location", follower.getLocation());
					map.put("createdAt", follower.getCreatedAt());
					map.put("isFollowing", userService.isFollowing(user.getId(), follower.getId())); // ✅ Aquí
					return map;
				}).toList();

				model.addAttribute("followersPage", followersWithStatus);

				model.addAttribute("totalFollowers", totalFollowers);
				model.addAttribute("range", String.format("%02d-%02d", start, end));
			} else {
				model.addAttribute("followersEmpty", true);
			}
			// Page numbers
			model.addAttribute("previousPage", followersPage.hasPrevious() ? page - 1 : null);
			model.addAttribute("currentPage", page);
			model.addAttribute("nextPage", followersPage.hasNext() ? page + 1 : null);
			return "followers";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/user/following")
	public String userFolloweds(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "12") int size,
			@RequestParam(defaultValue = "createdAt") String sort, Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User user = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName())
							.orElseThrow(() -> new UsernameNotFoundException("User not found")));
			if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
				return "redirect:/";
			}
			if (page < 1) {
				return "redirect:/user/following?page=1";
			}
			Sort sortBy;
			switch (sort) {
			case "nameAsc":
				sortBy = Sort.by(Sort.Direction.ASC, "name");
				break;
			case "nameDesc":
				sortBy = Sort.by(Sort.Direction.DESC, "name");
				break;
			default:
				sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
			}
			Pageable pageable = PageRequest.of(page - 1, size, sortBy);
			Page<User> followingPage = userService.getFollowingPage(user.getId(), pageable);
			model.addAttribute("sort", sort);
			model.addAttribute("sortName", sort.equals("nameDesc"));
			model.addAttribute("sortNameAsc", sort.equals("nameAsc"));
			model.addAttribute("sortCreatedAt", sort.equals("createdAt"));

			// Verify page limit
			if (followingPage.getTotalPages() > 0 && page > followingPage.getTotalPages()) {
				return "redirect:/user/following?page=" + (followingPage.getTotalPages());
			}

			// Following range calculation
			int totalFollowing = (int) followingPage.getTotalElements();
			if (totalFollowing > 0) {
				int start = followingPage.getNumber() * followingPage.getSize() + 1;
				int end = Math.min(start + followingPage.getNumberOfElements() - 1, totalFollowing);

				model.addAttribute("totalFollowing", totalFollowing);
				model.addAttribute("range", String.format("%02d-%02d", start, end));
				model.addAttribute("followingPage", followingPage);
			} else {
				model.addAttribute("followingEmpty", true);
			}
			// Page numbers
			model.addAttribute("previousPage", followingPage.hasPrevious() ? page - 1 : null);
			model.addAttribute("currentPage", page);
			model.addAttribute("nextPage", followingPage.hasNext() ? page + 1 : null);
			return "following";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/follow/{username}")
	public String followUser(@PathVariable String username, HttpServletRequest request,
			@RequestHeader(required = false) String referer) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User follower = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName())
							.orElseThrow(() -> new UsernameNotFoundException("User not found")));
			if (follower.getRoles() != null && follower.getRoles().contains("ADMIN")) {
				return "redirect:/";
			}
			User followed = userService.findByName(username).orElseThrow();
			if (followed.getRoles() != null && followed.getRoles().contains("ADMIN")) {
				return "redirect:/";
			}
			userService.followUser(follower.getId(), followed.getId());
			return "redirect:" + referer;
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/unfollow/{username}")
	public String unfollowUser(@PathVariable String username, HttpServletRequest request,
			@RequestHeader(required = false) String referer) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User follower = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName())
							.orElseThrow(() -> new UsernameNotFoundException("User not found")));
			if (follower.getRoles() != null && follower.getRoles().contains("ADMIN")) {
				return "redirect:/";
			}
			User followed = userService.findByName(username).orElseThrow();
			if (followed.getRoles() != null && followed.getRoles().contains("ADMIN")) {
				return "redirect:/";
			}
			userService.unfollowUser(follower.getId(), followed.getId());
			return "redirect:" + referer;
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/user/favouritesArtworks")
	public String favouritesArtworks(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "6") int size, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "title") String sort, @RequestParam(required = false) List<Long> artist,
			@RequestParam(required = false) List<String> ratingRanges, Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User user = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName())
							.orElseThrow(() -> new UsernameNotFoundException("User not found")));
			if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
				return "redirect:/";
			}

			// Verify page limit
			if (page < 1) {
				StringBuilder redirectUrl = new StringBuilder("redirect:/user/favouritesArtworks?page=1");
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

			// Artists checkbox
			Map<Artist, Long> topArtists = user.getFavoriteArtworks().stream()
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

			// Pageable artworks
			Pageable pageable = PageRequest.of(page - 1, size, sortBy);
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
				artworksPage = artworkService.findByUsersWhoFavorited_Id(user.getId(), search, artist,
						ratingRangesParsed, pageable);
			} else {
				List<Map<String, Object>> ratingRangeCheckboxes = List.of(Map.of("min", 4, "max", 5),
						Map.of("min", 3, "max", 4), Map.of("min", 2, "max", 3), Map.of("min", 1, "max", 2),
						Map.of("min", 0, "max", 1));
				model.addAttribute("ratingRanges", ratingRangeCheckboxes);
				artworksPage = artworkService.findByUsersWhoFavorited_Id(user.getId(), search, artist, pageable);
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
						"redirect:/user/favouritesArtworks?page=" + artworksPage.getTotalPages());
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
				model.addAttribute("artworksPage", artworksPage);
			} else {
				model.addAttribute("totalArtworks", 0);
				model.addAttribute("range", 0);
				model.addAttribute("previousPage", null);
				model.addAttribute("currentPage", 1);
				model.addAttribute("nextPage", null);
				model.addAttribute("artworksEmpty", true);
			}

			return "favouritesArtworks";
		} else {
			return "redirect:/";
		}
	}

	@PostMapping("/user/change-password")
	public String updatePassword(HttpServletRequest request, RedirectAttributes redirectAttrs, String password,
			String password2, String password3) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			if (password2.equals(password3)) {
				User user = userService.findByEmail(principal.getName())
						.orElseGet(() -> userService.findByName(principal.getName())
								.orElseThrow(() -> new UsernameNotFoundException("User not found")));
				String userPassword = user.getPassword();
				if (passwordEncoder.matches(password, userPassword)) {
					user.setPassword(passwordEncoder.encode(password2));
					userService.save(user);
					redirectAttrs.addFlashAttribute("Correcto", "Has cambiado la contraseña. !No te olvides¡");
				} else {
					redirectAttrs.addFlashAttribute("errorMessage",
							"La contraseña introducida no coincide con la del usuario");
				}
			} else {
				redirectAttrs.addFlashAttribute("errorMessage",
						"La nueva contraseña no coincide con la confirmación contraseña");
			}
			return "redirect:/dashboard-profile";
		} else {
			return "redirect:/";
		}
	}

	@PostMapping("/user/change-data")
	public String updateData(HttpServletRequest request, RedirectAttributes redirectAttrs, String location,
			String biography) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			boolean loc_exists = location != null && !location.isBlank() && !location.isEmpty();
			boolean bio_exists = biography != null && !biography.isBlank() && !biography.isEmpty();
			if (loc_exists || bio_exists) {
				User user = userService.findByEmail(principal.getName())
						.orElseGet(() -> userService.findByName(principal.getName())
								.orElseThrow(() -> new UsernameNotFoundException("User not found")));
				if (loc_exists) {
					user.setLocation(location);
				}
				if (bio_exists) {
					user.setBiography(biography);
				}
				userService.save(user);
			} else {
				redirectAttrs.addFlashAttribute("errorMessage", "Campos vacíos");
			}
			return "redirect:/dashboard-profile";
		} else {
			return "redirect:/";
		}
	}

	@PostMapping("/register/new")
	public String registerNewUser(HttpServletRequest request, RedirectAttributes redirectAttrs, String username,
			String email, String password, String password1, String location, String biography) {
		boolean email_exists = email != null && !email.isBlank() && !email.isEmpty();
		boolean username_exists = username != null && !username.isBlank() && !username.isEmpty();
		boolean password_exists = password != null && !password.isBlank() && !password.isEmpty();
		boolean password1_exists = password1 != null && !password1.isBlank() && !password1.isEmpty();
		if (email_exists && username_exists) {
			boolean email_taken = userService.findByEmail(email).orElse(null) != null;
			boolean username_taken = userService.findByName(username).orElse(null) != null;
			if (!username_taken) {
				if (!email_taken) {
					if (password_exists && password1_exists) {
						if (password.equals(password1)) {
							boolean loc_exists = location != null && !location.isBlank() && !location.isEmpty();
							boolean bio_exists = biography != null && !biography.isBlank() && !biography.isEmpty();
							User user = new User(username, email, passwordEncoder.encode(password));
							if (loc_exists || bio_exists) {
								if (loc_exists) {
									user.setLocation(location);
								}
								if (bio_exists) {
									user.setBiography(biography);
								}
							}
							userService.save(user);
							redirectAttrs.addFlashAttribute("success", "Usuario registrado correctamente.");
							return "redirect:/login";
						} else {
							redirectAttrs.addFlashAttribute("errorMessage", "Las contraseñas no coinciden");
							return "redirect:/register";
						}
					} else {
						redirectAttrs.addFlashAttribute("errorMessage", "No has introducido contraseña.");
						return "redirect:/register";
					}
				} else {
					redirectAttrs.addFlashAttribute("errorMessage", "Email ya en uso, pruebe a recuperar contraseña");
					return "redirect:/register";
				}
			} else {
				redirectAttrs.addFlashAttribute("errorMessage", "Usuario ya existente, use otro nombre de usuario");
				return "redirect:/register";
			}
		} else {
			redirectAttrs.addFlashAttribute("errorMessage", "No has introducido usuario y/o email.");
			return "redirect:/register";
		}
	}

	@PostMapping("/user/change-image")
	public String changeUserImage(@RequestParam MultipartFile image, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		Principal principal = request.getUserPrincipal();
		if (principal == null) {
			return "redirect:/";
		}

		if (image.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "No se pudo actualizar la imagen.");
			return "redirect:/dashboard-profile";
		}

		User user = userService.findByEmail(principal.getName()).orElseGet(() -> userService
				.findByName(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found")));
		if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
			return "redirect:/dashboard-profile";
		}
		try {
			user.setImage(image.getBytes());
			userService.save(user);
			redirectAttributes.addFlashAttribute("success", "Imagen actualizada correctamente.");
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar la imagen.");
		}

		return "redirect:/dashboard-profile";
	}

	@PostMapping("/user/delete-image")
	public String deleteUserImage(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Principal principal = request.getUserPrincipal();
		if (principal == null) {
			return "redirect:/";
		}

		User user = userService.findByEmail(principal.getName()).orElseGet(() -> userService
				.findByName(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found")));

		if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
			return "redirect:/dashboard-profile";
		}

		if (user.getImage() != null) {
			user.setImage(null);
			userService.save(user);
			redirectAttributes.addFlashAttribute("success",
					"Imagen eliminada. Se ha restaurado la imagen por defecto.");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "La imagen ya es por defecto.");
		}
		return "redirect:/dashboard-profile";
	}

	@GetMapping("/user/delete")
	public String deleteUser(HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal == null) {
			return "redirect:/";
		}

		User user = userService.findByEmail(principal.getName()).orElseGet(() -> userService
				.findByName(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found")));
		userService.deleteUser(user.getId());
		return "redirect:/logout";
	}

	@GetMapping("/user/image/{username}")
	public ResponseEntity<byte[]> getImage(@PathVariable String username, HttpServletRequest request)
			throws IOException {
		Principal principal = request.getUserPrincipal();
		if (principal == null) {
			return null;
		}

		User user = userService.findByEmail(username).orElseGet(() -> userService.findByName(username).orElse(null));
		Resource resource = resourceLoader.getResource("classpath:static/assets/img/user/default-user-icon.jpg");

		if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
			resource = resourceLoader.getResource("classpath:static/assets/img/user/admin_image.jpg");
		}
		if (user != null) {
			if (user.getImage() != null) {
				return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(user.getImage());
			}
		}
		byte[] defaultImage = StreamUtils.copyToByteArray(resource.getInputStream());
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(defaultImage);
	}

	// ---RECOMENDATION ALGORITHM---
	private Map<Long, Double> getUserRatings(Long userId) {
		return reviewService.findByUserId(userId).stream()
				.collect(Collectors.toMap(r -> r.getArtwork().getId(), Review::getRating, (r1, r2) -> (r1 + r2) / 2));
	}

	private List<Long> findSimilarUsers(Long userId, int topN) {
		Map<Long, Double> myRatings = getUserRatings(userId);
		List<User> others = userService.findAll();

		return others.stream().filter(u -> !u.getId().equals(userId)).map(u -> {
			Map<Long, Double> r = reviewService.findByUserId(u.getId()).stream().collect(
					Collectors.toMap(rr -> rr.getArtwork().getId(), Review::getRating, (r1, r2) -> (r1 + r2) / 2));
			double sim = cosineSimilarity(myRatings, r);
			return Map.entry(u.getId(), sim);
		}).filter(e -> e.getValue() > 0.1).sorted(Map.Entry.<Long, Double>comparingByValue().reversed()).limit(topN)
				.map(Map.Entry::getKey).toList();
	}

	private double cosineSimilarity(Map<Long, Double> a, Map<Long, Double> b) {
		Set<Long> common = new HashSet<>(a.keySet());
		common.retainAll(b.keySet());
		double dot = 0, na = 0, nb = 0;
		for (Long k : common)
			dot += a.get(k) * b.get(k);
		for (double v : a.values())
			na += v * v;
		for (double v : b.values())
			nb += v * v;
		return na == 0 || nb == 0 ? 0 : dot / (Math.sqrt(na) * Math.sqrt(nb));
	}

	private List<Long> getCandidatesFromSimilars(Long userId, List<Long> similars) {
		Set<Long> seen = getUserRatings(userId).keySet();
		return reviewService.findByUserIdIn(similars).stream().map(r -> r.getArtwork().getId())
				.filter(id -> !seen.contains(id)).collect(Collectors.groupingBy(id -> id, Collectors.counting()))
				.entrySet().stream().sorted(Map.Entry.<Long, Long>comparingByValue().reversed()).limit(20)
				.map(Map.Entry::getKey).toList();
	}

	private List<Artwork> filterAndRank(Long userId, List<Long> candidateIds) {
		Map<Long, Double> myRatings = getUserRatings(userId);
		return artworkService.findAllById(candidateIds).stream().sorted(Comparator.comparing(a -> {
			double score = a.getAverageRating();
			if (myRatings.values().stream().mapToDouble(d -> d).average().orElse(0) < a.getAverageRating()) {
				score += 1;
			}
			return -score;
		})).toList();
	}

	private List<Artwork> recommendArtworks(Long userId, int number) {
		List<Long> similars = findSimilarUsers(userId, number);
		List<Long> candidates = similars.isEmpty() ? List.of() : getCandidatesFromSimilars(userId, similars);

		if (candidates.isEmpty()) {
			return artworkService.findTop12ByOrderByAverageRatingDesc();
		}
		return filterAndRank(userId, candidates).stream().limit(number).toList();
	}

	// ---END RECOMENDATION ALGORITHM---

	private static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

}
