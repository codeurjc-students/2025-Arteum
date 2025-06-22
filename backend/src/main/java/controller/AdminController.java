package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import model.Artist;
import model.Artwork;
import model.Museum;
import model.Review;
import model.User;
import service.ArtistService;
import service.ArtworkService;
import service.MuseumService;
import service.ReviewService;
import service.UserService;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private ArtworkService artworkService;

	@Autowired
	private ArtistService artistService;

	@Autowired
	private MuseumService museumService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/dashboard")
	public String showDashboard(Model model, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN"))
			return "redirect:/";
		return "admin/admin-dashboard";
	}

	@GetMapping("/artwork/new")
	public String showCreateArtworkForm(Model model, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN"))
			return "redirect:/";
		return "admin/create-artwork";
	}

	@PostMapping("/artwork/new")
	public String createArtwork(@RequestParam String title, @RequestParam Integer creationYear,
			@RequestParam String description, @RequestParam Long artistId, @RequestParam Long museumId,
			@RequestParam("image") MultipartFile imageFile, RedirectAttributes redirectAttrs,
			HttpServletRequest request) throws IOException {

		if (!request.isUserInRole("ADMIN"))
			return "redirect:/";

		Optional<Artist> artistOpt = artistService.findById(artistId);
		Optional<Museum> museumOpt = museumService.findById(museumId);
		if (artistOpt.isEmpty() || museumOpt.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "Artista o museo no encontrado.");
			return "redirect:/admin/artwork/new";
		}

		Artwork artwork = new Artwork();
		artwork.setTitle(title);
		artwork.setCreationYear(creationYear);
		artwork.setDescription(description);
		artwork.setArtist(artistOpt.get());
		artwork.setMuseum(museumOpt.get());
		artwork.setAverageRating(0);

		if (!imageFile.isEmpty()) {
			artwork.setImage(imageFile.getBytes());
		}

		artworkService.save(artwork);
		redirectAttrs.addFlashAttribute("success", "Obra creada correctamente - ID " + artwork.getId());
		return "redirect:/admin/artwork/new";
	}

	@GetMapping("/artwork/edit/{id}")
	public String showEditArtworkForm(@PathVariable Long id, Model model, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN"))
			return "redirect:/";

		Optional<Artwork> optionalArtwork = artworkService.findById(id);
		if (optionalArtwork.isEmpty()) {
			model.addAttribute("error", "Obra no encontrada");
			return "redirect:/artworks";
		}
		model.addAttribute("artwork", optionalArtwork.get());
		return "admin/edit-artwork";
	}

	@PostMapping("/artwork/edit/{id}")
	public String updateArtwork(@PathVariable Long id, String title, Integer creationYear, String description,
			Long artistId, Long museumId, MultipartFile image, RedirectAttributes redirectAttrs,
			HttpServletRequest request) throws IOException {
		if (!request.isUserInRole("ADMIN"))
			return "redirect:/";

		Optional<Artwork> optionalArtwork = artworkService.findById(id);
		if (optionalArtwork.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "Obra no encontrada");
			return "redirect:/admin/artworks";
		}

		Optional<Artist> artistOpt = java.util.Optional.empty();
		if (artistId != null) {
			artistOpt = artistService.findById(artistId);
			if (artistOpt.isEmpty()) {
				redirectAttrs.addFlashAttribute("error", "Artista no encontrado.");
				return "redirect:/admin/artwork/edit/" + id;
			}
		}

		Optional<Museum> museumOpt = java.util.Optional.empty();
		if (museumId != null) {
			museumOpt = museumService.findById(museumId);
			if (museumOpt.isEmpty()) {
				redirectAttrs.addFlashAttribute("error", "Museo no encontrado.");
				return "redirect:/admin/artwork/edit/" + id;
			}
		}

		Artwork artwork = optionalArtwork.get();

		if (title != null && !title.isBlank()) {
			artwork.setTitle(title);
		}

		if (creationYear != null) {
			artwork.setCreationYear(creationYear);
		}

		if (description != null && !description.isBlank()) {
			artwork.setDescription(description);
		}

		artistOpt.ifPresent(artwork::setArtist);
		museumOpt.ifPresent(artwork::setMuseum);

		if (image != null && !image.isEmpty()) {
			artwork.setImage(image.getBytes());
		}

		artworkService.save(artwork);
		redirectAttrs.addFlashAttribute("success", "Obra actualizada correctamente");
		return "redirect:/admin/artwork/edit/" + id;
	}

	@GetMapping("/artwork/delete/{id}")
	public String deleteArtwork(@PathVariable Long id, RedirectAttributes redirectAttrs, HttpServletRequest request,
			@RequestHeader(required = false) String referer) {
		if (!request.isUserInRole("ADMIN"))
			return "redirect:/";

		Optional<Artwork> optionalArtwork = artworkService.findById(id);
		if (optionalArtwork.isPresent()) {
			Artwork artwork = optionalArtwork.get();
			for (User user : artwork.getUsersWhoFavorited()) {
				user.getFavoriteArtworks().remove(artwork);
			}
			artworkService.delete(artwork);
			redirectAttrs.addFlashAttribute("success", "Obra eliminada correctamente");
		} else {
			redirectAttrs.addFlashAttribute("error", "Obra no encontrada");
		}

		if (referer != null && (referer.contains("/admin/") || referer.contains("/artworks/" + id))) {
			return "redirect:/artworks";
		} else {
			return "redirect:" + (referer != null ? referer : "/artworks");
		}
	}

	@GetMapping("/artist/new")
	public String showCreateArtistForm(Model model, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}
		model.addAttribute("artist", new Artist());
		return "admin/create-artist";
	}

	@PostMapping("/artist/new")
	public String createArtist(@RequestParam("name") String name, @RequestParam("nationality") String nationality,
			@RequestParam(value = "dateOfBirth") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirth,
			@RequestParam(value = "dateOfDeath", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfDeath,
			@RequestParam(value = "biography") String biography, @RequestParam(value = "image") MultipartFile imageFile,
			RedirectAttributes redirectAttrs, HttpServletRequest request) throws IOException {

		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}

		if (name == null || name.isBlank() || nationality == null || nationality.isBlank()) {
			redirectAttrs.addFlashAttribute("error", "Nombre y nacionalidad son obligatorios.");
			return "redirect:/admin/artist/new";
		}

		Artist artist = new Artist();
		artist.setName(name.trim());
		artist.setNationality(nationality.trim());

		if (dateOfBirth != null) {
			artist.setDateOfBirth(dateOfBirth);
		}
		if (dateOfDeath != null) {
			artist.setDateOfDeath(dateOfDeath);
		}
		if (biography != null && !biography.isBlank()) {
			artist.setBiography(biography.trim());
		}
		if (imageFile != null && !imageFile.isEmpty()) {
			artist.setImage(imageFile.getBytes());
		}

		artist.setInsertionDate(new Date());

		artistService.save(artist);
		redirectAttrs.addFlashAttribute("success", "Artista creado correctamente - ID " + artist.getId());
		return "redirect:/admin/artist/new";
	}

	@GetMapping("/artist/edit/{id}")
	public String showEditArtistForm(@PathVariable Long id, Model model, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}

		Optional<Artist> optionalArtist = artistService.findById(id);
		if (optionalArtist.isPresent()) {
			model.addAttribute("artist", optionalArtist.get());
			return "admin/edit-artist";
		} else {
			model.addAttribute("error", "Artista no encontrado");
			return "redirect:/artists";
		}
	}

	@PostMapping("/artist/edit/{id}")
	public String updateArtist(@PathVariable Long id, @RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "nationality", required = false) String nationality,
			@RequestParam(value = "dateOfBirth", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirth,
			@RequestParam(value = "dateOfDeath", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfDeath,
			@RequestParam(value = "biography", required = false) String biography,
			@RequestParam(value = "image", required = false) MultipartFile imageFile, RedirectAttributes redirectAttrs,
			HttpServletRequest request) throws IOException {
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}

		Optional<Artist> optionalArtist = artistService.findById(id);
		if (optionalArtist.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "Artista no encontrado");
			return "redirect:/artists";
		}

		Artist artist = optionalArtist.get();
		if (name != null && !name.isBlank() && !name.isEmpty()) {
			artist.setName(name);
		}
		if (nationality != null && !nationality.isBlank() && !nationality.isEmpty()) {
			artist.setNationality(nationality);
		}
		if (dateOfBirth != null) {
			artist.setDateOfBirth(dateOfBirth);
		}
		if (dateOfDeath != null) {
			artist.setDateOfDeath(dateOfDeath);
		}
		if (biography != null && !biography.isBlank()) {
			artist.setBiography(biography.trim());
		}
		if (imageFile != null && !imageFile.isEmpty()) {
			artist.setImage(imageFile.getBytes());
		}

		artistService.save(artist);

		redirectAttrs.addFlashAttribute("success", "Artista actualizado correctamente");
		return "redirect:/admin/artist/edit/" + id;
	}

	@GetMapping("/artist/delete/{id}")
	public String deleteArtist(@PathVariable Long id, RedirectAttributes redirectAttrs, HttpServletRequest request,
			@RequestHeader(required = false) String referer) {
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}

		Optional<Artist> optionalArtist = artistService.findById(id);
		if (optionalArtist.isPresent()) {
			Artist artist = optionalArtist.get();
			for (Artwork artwork : artist.getArtworks()) {
				artwork.setArtist(null);
				artworkService.save(artwork);
			}
			artistService.delete(artist);
			redirectAttrs.addFlashAttribute("success", "Artista eliminado correctamente");
		} else {
			redirectAttrs.addFlashAttribute("error", "Artista no encontrado");
		}

		if (referer != null && (referer.contains("/admin/") || referer.contains("/artists/" + id))) {
			return "redirect:/artists";
		} else {
			return "redirect:" + (referer != null ? referer : "/artists");
		}
	}

	@GetMapping("/museum/new")
	public String showCreateMuseumForm(Model model, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}
		model.addAttribute("museum", new Museum());
		return "admin/create-museum";
	}

	@PostMapping("/museum/new")
	public String createMuseum(@RequestParam("name") String name, @RequestParam("location") String location,
			@RequestParam("founded") Integer founded, @RequestParam("description") String description,
			@RequestParam("image") MultipartFile imageFile, RedirectAttributes redirectAttrs,
			HttpServletRequest request) throws IOException {

		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}

		if (name == null || name.isBlank() || location == null || location.isBlank() || founded == null
				|| founded < 1000 || founded > 2100) {
			redirectAttrs.addFlashAttribute("error", "Nombre, ubicación y año de fundación válido son obligatorios.");
			return "redirect:/admin/museum/new";
		}

		Museum museum = new Museum();
		museum.setName(name.trim());
		museum.setLocation(location.trim());
		museum.setFounded(founded);
		museum.setDescription(description != null ? description.trim() : "");

		if (imageFile != null && !imageFile.isEmpty()) {
			museum.setImage(imageFile.getBytes());
		}

		museumService.save(museum);
		redirectAttrs.addFlashAttribute("success", "Museo creado correctamente - ID " + museum.getId());
		return "redirect:/admin/museum/new";
	}

	@GetMapping("/museum/edit/{id}")
	public String showEditMuseumForm(@PathVariable Long id, Model model, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}

		Optional<Museum> optionalMuseum = museumService.findById(id);
		if (optionalMuseum.isPresent()) {
			model.addAttribute("museum", optionalMuseum.get());
			return "admin/edit-museum";
		} else {
			model.addAttribute("error", "Museo no encontrado");
			return "redirect:/museums";
		}
	}

	@PostMapping("/museum/edit/{id}")
	public String updateMuseum(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttrs,
			@RequestParam(required = false) String name, @RequestParam(required = false) String location,
			@RequestParam(required = false) Integer founded, @RequestParam(required = false) String description,
			@RequestParam(required = false, value = "image") MultipartFile imageFile) throws IOException {
		if (!request.isUserInRole("ADMIN"))
			return "redirect:/";
		
		Optional<Museum> optionalMuseum = museumService.findById(id);
		if (optionalMuseum.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "Museo no encontrado");
			return "redirect:/museums";
		}

		Museum existing = optionalMuseum.get();

		if (!isBlank(name))
			existing.setName(name);
		if (!isBlank(location)) 
			existing.setLocation(location);
		if (founded != null && founded > 1000)
			existing.setFounded(founded);
		if (!isBlank(description))
			existing.setDescription(description);
		if (imageFile != null && !imageFile.isEmpty())
			existing.setImage(imageFile.getBytes());
		
		museumService.save(existing);

		redirectAttrs.addFlashAttribute("success", "Museo actualizado correctamente");
		return "redirect:/admin/museum/edit/" + id;
	}

	@GetMapping("/museum/delete/{id}")
	public String deleteMuseum(@PathVariable Long id, RedirectAttributes redirectAttrs, HttpServletRequest request,
			@RequestHeader(required = false) String referer) {
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}

		Optional<Museum> optionalMuseum = museumService.findById(id);
		if (optionalMuseum.isPresent()) {
			Museum museum = optionalMuseum.get();
			for (Artwork artwork : museum.getArtworks()) {
				artwork.setMuseum(null);
				artworkService.save(artwork);
			}
			museumService.delete(museum);
			redirectAttrs.addFlashAttribute("success", "Museo eliminado correctamente");
		} else {
			redirectAttrs.addFlashAttribute("error", "Museo no encontrado");
		}

		if (referer != null && (referer.contains("/admin/") || referer.contains("/museum/" + id))) {
			return "redirect:/museums";
		} else {
			return "redirect:" + referer;
		}
	}

	@GetMapping("/user/new")
	public String showCreateUserForm(Model model, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}
		model.addAttribute("user", new User());
		return "admin/create-user";
	}

	@PostMapping("/user/new")
	public String createUser(HttpServletRequest request, RedirectAttributes redirectAttrs,
			@RequestParam String username, @RequestParam String email, @RequestParam String password,
			@RequestParam String password1, @RequestParam(required = false) String location,
			@RequestParam(required = false) String biography, @RequestParam("image") MultipartFile imageFile)
			throws IOException {

		if (isBlank(username) || isBlank(email)) {
			redirectAttrs.addFlashAttribute("error", "Usuario y email son obligatorios.");
			return "redirect:/admin/user/new";
		}

		if (userService.findByName(username).isPresent()) {
			redirectAttrs.addFlashAttribute("error", "Usuario ya existente, use otro nombre de usuario.");
			return "redirect:/admin/user/new";
		}

		if (userService.findByEmail(email).isPresent()) {
			redirectAttrs.addFlashAttribute("error", "Email ya en uso, pruebe a recuperar contraseña.");
			return "redirect:/admin/user/new";
		}

		if (isBlank(password) || isBlank(password1)) {
			redirectAttrs.addFlashAttribute("error", "Debe introducir la contraseña en ambos campos.");
			return "redirect:/admin/user/new";
		}

		if (!password.equals(password1)) {
			redirectAttrs.addFlashAttribute("error", "Las contraseñas no coinciden.");
			return "redirect:/admin/user/new";
		}

		User user = new User(username.trim(), email.trim(), passwordEncoder.encode(password));
		user.setCreatedAt(new Date());
		user.setRoles(List.of("USER"));

		if (!isBlank(location))
			user.setLocation(location.trim());
		if (!isBlank(biography))
			user.setBiography(biography.trim());
		if (imageFile != null && !imageFile.isEmpty())
			user.setImage(imageFile.getBytes());

		userService.save(user);
		redirectAttrs.addFlashAttribute("success", "Usuario registrado correctamente - ID " + user.getId());
		return "redirect:/admin/user/new";
	}

	@GetMapping("/user/edit/{id}")
	public String showEditUserForm(@PathVariable Long id, Model model, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN"))
			return "redirect:/";

		Optional<User> optionalUser = userService.findById(id);
		if (optionalUser.isEmpty()) {
			model.addAttribute("error", "Usuario no encontrado");
			return "redirect:/admin/dashboard";
		}
		User user = optionalUser.get();
		model.addAttribute("hasUserRole", user.getRoles().contains("USER"));
		model.addAttribute("hasAdminRole", user.getRoles().contains("ADMIN"));
		model.addAttribute("user", user);
		return "admin/edit-user";
	}

	@PostMapping("/user/edit/{id}")
	public String updateUser(@PathVariable Long id, @RequestParam String name, @RequestParam String email,
			@RequestParam(required = false) String biography, @RequestParam(required = false) String location,
			@RequestParam List<String> roles, @RequestParam(required = false) MultipartFile image,
			@RequestParam(required = false) String password, @RequestParam(required = false) String password2,
			RedirectAttributes redirectAttrs, HttpServletRequest request) throws IOException {

		if (!request.isUserInRole("ADMIN"))
			return "redirect:/";

		Optional<User> optionalUser = userService.findById(id);
		if (optionalUser.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "Usuario no encontrado.");
			return "redirect:/admin/users";
		}

		User user = optionalUser.get();
		if (!isBlank(password) && !isBlank(password2)) {
			if (password.equals(password2)) {
				user.setPassword(passwordEncoder.encode(password));
			} else {
				redirectAttrs.addFlashAttribute("error", "Las contraseñas no coinciden");
				return "redirect:/admin/user/edit/" + id;
			}
		}
		if (!isBlank(name))
			user.setName(name.trim());
		if (!isBlank(email))
			user.setEmail(email.trim());
		if (!isBlank(biography))
			user.setBiography(biography.trim());
		if (!isBlank(location))
			user.setLocation(location.trim());
		user.setRoles(roles);
		if (image != null && !image.isEmpty()) {
			user.setImage(image.getBytes());
		}

		userService.save(user);
		redirectAttrs.addFlashAttribute("success", "Usuario actualizado correctamente.");
		return "redirect:/admin/user/edit/" + id;
	}

	@GetMapping("/user/delete/{id}")
	public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttrs, HttpServletRequest request,
			@RequestHeader(required = false) String referer) {
		if (!request.isUserInRole("ADMIN"))
			return "redirect:/";

		Optional<User> optionalUser = userService.findById(id);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			userService.delete(user);
			redirectAttrs.addFlashAttribute("success", "Usuario eliminado correctamente.");
		} else {
			redirectAttrs.addFlashAttribute("error", "Usuario no encontrado.");
		}

		if (referer != null && (referer.contains("/admin/") || referer.contains("/users/" + id))) {
			return "redirect:/users";
		} else {
			return "redirect:" + (referer != null ? referer : "/users");
		}
	}

	@GetMapping("/review/new")
	public String showCreateReviewForm(Model model, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}
		model.addAttribute("review", new Review());
		return "admin/create-review";
	}

	@PostMapping("/review/new")
	public String createReview(HttpServletRequest request, RedirectAttributes redirectAttrs,
			@RequestParam("artworkId") Long artworkId, @RequestParam("userId") Long userId,
			@RequestParam("rating") Double rating, @RequestParam(value = "comment", required = false) String comment) {
		Principal principal = request.getUserPrincipal();
		if (principal == null) {
			return "redirect:/login";
		}
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}
		if (artworkId == null || rating == null || rating.isNaN() || rating.isInfinite() || rating < 1 || rating > 5) {
			redirectAttrs.addFlashAttribute("error", "Datos inválidos en la valoración.");
			return "redirect:/admin/review/new";
		}

		Optional<Artwork> optionalArtwork = artworkService.findById(artworkId);
		if (optionalArtwork.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "La obra seleccionada no existe.");
			return "redirect:/admin/review/new";
		}

		Optional<User> optionalUser = userService.findById(userId);
		if (optionalUser.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "El usuario seleccionado no existe.");
			return "redirect:/admin/review/new";
		}

		User user = optionalUser.get();
		Artwork artwork = optionalArtwork.get();

		if (reviewService.existsByUserAndArtwork(user, artwork)) {
			redirectAttrs.addFlashAttribute("error", "Ya ha valorado esta obra.");
			return "redirect:/admin/review/new";
		}

		Review review = new Review();
		review.setArtwork(artwork);
		review.setUser(user);
		review.setRating(rating);
		review.setComment((comment != null && !comment.isBlank()) ? comment.trim() : null);
		review.setCreatedAt(new Date());

		reviewService.save(review);
		artworkService.updateAverageRating(artwork);

		redirectAttrs.addFlashAttribute("success", "Review creada correctamente - ID " + review.getId());
		return "redirect:/admin/review/new";
	}

	@GetMapping("/review/edit/{id}")
	public String showEditReviewForm(@PathVariable Long id, Model model, HttpServletRequest request) {
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}

		Optional<Review> optionalReview = reviewService.findById(id);
		if (optionalReview.isPresent()) {
			Review review = optionalReview.get();
			model.addAttribute("review", review);
			model.addAttribute("is1", review.getRating() == 1.0);
			model.addAttribute("is2", review.getRating() == 2.0);
			model.addAttribute("is3", review.getRating() == 3.0);
			model.addAttribute("is4", review.getRating() == 4.0);
			model.addAttribute("is5", review.getRating() == 5.0);
			return "admin/edit-review";
		} else {
			model.addAttribute("error", "Review no encontrada");
			return "redirect:/admin/dashboard";
		}
	}

	@PostMapping("/review/edit/{id}")
	public String editReview(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttrs,
			@RequestParam("artworkId") Long artworkId, @RequestParam("userId") Long userId,
			@RequestParam("rating") Double rating, @RequestParam(value = "comment", required = false) String comment) {
		Principal principal = request.getUserPrincipal();
		if (principal == null) {
			return "redirect:/login";
		}
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}

		if (artworkId == null || rating == null || rating.isNaN() || rating.isInfinite() || rating < 1 || rating > 5) {
			redirectAttrs.addFlashAttribute("error", "Datos inválidos en la valoración.");
			return "redirect:/admin/review/edit/" + id;
		}

		Optional<Review> optionalReview = reviewService.findById(id);
		if (optionalReview.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "La review no existe.");
			return "redirect:/admin/review/edit/" + id;
		}

		Optional<Artwork> optionalArtwork = artworkService.findById(artworkId);
		if (optionalArtwork.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "Obra no encontrada.");
			return "redirect:/admin/review/edit/" + id;
		}

		Optional<User> optionalUser = userService.findById(userId);
		if (optionalUser.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "El usuario seleccionado no existe.");
			return "redirect:/admin/review/edit/" + id;
		}

		User user = optionalUser.get();
		Artwork artwork = optionalArtwork.get();

		Review review = optionalReview.get();
		review.setArtwork(artwork);
		review.setUser(user);
		review.setRating(rating);
		review.setComment((comment != null && !comment.isBlank()) ? comment.trim() : null);

		reviewService.save(review);
		artworkService.updateAverageRating(artwork);

		redirectAttrs.addFlashAttribute("success", "Review editada correctamente");
		return "redirect:/admin/review/edit/" + id;
	}

	@GetMapping("/review/delete/{id}")
	public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttrs, HttpServletRequest request,
			@RequestHeader(required = false) String referer) {
		if (!request.isUserInRole("ADMIN"))
			return "redirect:/";

		Optional<Review> optionalReview = reviewService.findById(id);
		if (optionalReview.isPresent()) {
			Review review = optionalReview.get();
			Artwork artwork = review.getArtwork();
			reviewService.delete(review);
			artworkService.updateAverageRating(artwork);
			redirectAttrs.addFlashAttribute("success", "Review eliminada correctamente.");
		} else {
			redirectAttrs.addFlashAttribute("error", "Review no encontrada.");
		}

		if (referer != null && (referer.contains("/admin/") || referer.contains("/review/" + id))) {
			return "redirect:/admin/dashboard";
		} else {
			return "redirect:" + (referer != null ? referer : "/admin/dashboard");
		}
	}

	@GetMapping("/stats")
	public String showStats(Model model, HttpServletRequest request) throws JsonProcessingException {
		if (!request.isUserInRole("ADMIN")) {
			return "redirect:/";
		}

		Map<Integer, Long> starsData = reviewService.countStarsGivenByEveryone();
		model.addAttribute("starsData", new ObjectMapper().writeValueAsString(starsData));

		List<Review> reviews = reviewService.findAll();
		double avgGiven = round(reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0), 2);
		model.addAttribute("avgGiven", avgGiven);
		model.addAttribute("numReviews", reviews.size());

		Map<String, Long> favArtworks = reviews.stream()
				.collect(Collectors.groupingBy(
						r -> (r.getArtwork().getTitle() + " (" + r.getArtwork().getArtist().getName() + ")"),
						Collectors.counting()));
		Map<String, Long> top10favArtworks = favArtworks.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		model.addAttribute("top10favArtworks", new ObjectMapper().writeValueAsString(top10favArtworks));

		Map<String, Long> artistCounts = reviews.stream()
				.collect(Collectors.groupingBy(r -> r.getArtwork().getArtist().getName(), Collectors.counting()));
		Map<String, Long> top10Artists = artistCounts.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		model.addAttribute("artistCounts", new ObjectMapper().writeValueAsString(top10Artists));

		Map<String, Long> favMuseums = reviews.stream().collect(Collectors.groupingBy(
				r -> (r.getArtwork().getMuseum().getName() + " (" + r.getArtwork().getMuseum().getLocation() + ")"),
				Collectors.counting()));
		Map<String, Long> top10favMuseums = favMuseums.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		model.addAttribute("top10favMuseums", new ObjectMapper().writeValueAsString(top10favMuseums));

		Map<String, Long> topUsers = reviews.stream()
				.collect(Collectors.groupingBy(r -> r.getUser().getName(), Collectors.counting()));
		Map<String, Long> top10Users = topUsers.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		model.addAttribute("top10Users", new ObjectMapper().writeValueAsString(top10Users));

		List<User> users = userService.findAll();
		Map<String, Long> top10FavUsers = users.stream()
				.filter(u -> u.getName() != null && u.getFavoriteArtworks().size() > 0)
				.sorted(Comparator.comparingInt((User u) -> u.getFavoriteArtworks().size()).reversed()).limit(10)
				.collect(Collectors.toMap(u -> u.getName(), u -> (long) u.getFavoriteArtworks().size(), (e1, e2) -> e1,
						LinkedHashMap::new));
		model.addAttribute("top10FavUsers", new ObjectMapper().writeValueAsString(top10FavUsers));

		List<Artwork> artworks = artworkService.findAll();
		Map<String, Long> top10ArtworksMostFavourited = artworks.stream()
				.filter(a -> a.getTitle() != null && a.getArtist() != null && a.getArtist().getName() != null)
				.sorted(Comparator.comparingInt((Artwork a) -> a.getUsersWhoFavorited().size()).reversed()).limit(10)
				.collect(Collectors.toMap(a -> a.getTitle() + " (" + a.getArtist().getName() + ")",
						a -> (long) a.getUsersWhoFavorited().size(), (e1, e2) -> e1, LinkedHashMap::new));
		model.addAttribute("top10ArtworksMostFavourited",
				new ObjectMapper().writeValueAsString(top10ArtworksMostFavourited));

		model.addAttribute("numUsers", userService.count());
		model.addAttribute("numArtworks", artworkService.count());
		model.addAttribute("numArtists", artistService.count());
		model.addAttribute("numMuseums", museumService.count());

		return "admin/stats";
	}

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
			model.addAttribute("headerAdmin", request.isUserInRole("ADMIN"));
		} else {
			model.addAttribute("logged", false);
		}
	}

	private boolean isBlank(String str) {
		return str == null || str.trim().isEmpty() || str.isBlank();
	}

	private static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

}