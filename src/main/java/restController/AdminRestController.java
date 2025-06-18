package restController;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

@RestController
@RequestMapping("/api/v1/admin")
@Secured("ROLE_ADMIN")
public class AdminRestController {

	@Autowired
	private UserService userService;

	@Autowired
	private ArtworkService artworkService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private MuseumService museumService;

	@Autowired
	private ArtistService artistService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Operation(summary = "Crear una nueva obra de arte", description = "Permite a un administrador crear una obra de arte con imagen, artista y museo.", responses = {
			@ApiResponse(responseCode = "201", description = "Obra creada correctamente"),
			@ApiResponse(responseCode = "404", description = "Artista o museo no encontrado"),
			@ApiResponse(responseCode = "400", description = "Datos inválidos"),
			@ApiResponse(responseCode = "403", description = "No autorizado (no es administrador)") })
	@Secured("ROLE_ADMIN")
	@PostMapping(value = "/artworks/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createArtwork(
			@Parameter(description = "Título de la obra", required = true) @RequestParam String title,
			@Parameter(description = "Año de creación", required = true) @RequestParam Integer creationYear,
			@Parameter(description = "Descripción de la obra", required = true) @RequestParam String description,
			@Parameter(description = "ID del artista asociado", required = true) @RequestParam Long artistId,
			@Parameter(description = "ID del museo asociado", required = true) @RequestParam Long museumId,
			@Parameter(description = "Archivo de imagen para la obra", required = true) @RequestPart("image") MultipartFile imageFile)
			throws IOException {

		Optional<Artist> artistOpt = artistService.findById(artistId);
		Optional<Museum> museumOpt = museumService.findById(museumId);

		if (artistOpt.isEmpty() || museumOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artista o museo no encontrado.");
		}

		Artwork artwork = new Artwork();
		artwork.setTitle(title);
		artwork.setCreationYear(creationYear);
		artwork.setDescription(description);
		artwork.setArtist(artistOpt.get());
		artwork.setMuseum(museumOpt.get());
		artwork.setAverageRating(0.0);

		if (!imageFile.isEmpty()) {
			artwork.setImage(imageFile.getBytes());
		}

		artworkService.save(artwork);

		return ResponseEntity.status(HttpStatus.CREATED).body("Obra creada correctamente - ID: " + artwork.getId());
	}

	@Operation(summary = "Editar una obra de arte", description = "Actualiza una obra de arte existente. Todos los campos son opcionales excepto el ID.", responses = {
			@ApiResponse(responseCode = "200", description = "Obra actualizada correctamente"),
			@ApiResponse(responseCode = "404", description = "Obra, artista o museo no encontrado"),
			@ApiResponse(responseCode = "400", description = "Solicitud inválida") })
	@PutMapping(value = "/artworks/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateArtwork(@PathVariable Long id, @RequestParam(required = false) String title,
			@RequestParam(required = false) Integer creationYear, @RequestParam(required = false) String description,
			@RequestParam(required = false) Long artistId, @RequestParam(required = false) Long museumId,
			@RequestPart(required = false) MultipartFile image) throws IOException {

		Optional<Artwork> artworkOpt = artworkService.findById(id);
		if (artworkOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Obra no encontrada.");
		}

		Artwork artwork = artworkOpt.get();

		if (title != null && !title.isBlank())
			artwork.setTitle(title);
		if (creationYear != null)
			artwork.setCreationYear(creationYear);
		if (description != null && !description.isBlank())
			artwork.setDescription(description);

		if (artistId != null) {
			Artist artist = artistService.findById(artistId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artista no encontrado"));
			artwork.setArtist(artist);
		}

		if (museumId != null) {
			Museum museum = museumService.findById(museumId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Museo no encontrado"));
			artwork.setMuseum(museum);
		}

		if (image != null && !image.isEmpty()) {
			artwork.setImage(image.getBytes());
		}

		artworkService.save(artwork);
		return ResponseEntity.ok("Obra actualizada correctamente.");
	}

	@Operation(summary = "Eliminar una obra de arte", description = "Elimina una obra existente por su ID.", responses = {
			@ApiResponse(responseCode = "200", description = "Obra eliminada correctamente"),
			@ApiResponse(responseCode = "404", description = "Obra no encontrada") })
	@DeleteMapping("/artworks/delete/{id}")
	public ResponseEntity<?> deleteArtwork(@PathVariable Long id) {
		Optional<Artwork> artworkOpt = artworkService.findById(id);
		if (artworkOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Obra no encontrada.");
		}

		Artwork artwork = artworkOpt.get();

		for (User user : artwork.getUsersWhoFavorited()) {
			user.getFavoriteArtworks().remove(artwork);
		}

		artworkService.delete(artwork);
		return ResponseEntity.ok("Obra eliminada correctamente.");
	}

	@Operation(summary = "Crear un nuevo artista", description = "Crea un artista con nombre, nacionalidad, biografía, fechas y una imagen opcional.", responses = {
			@ApiResponse(responseCode = "201", description = "Artista creado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
			@ApiResponse(responseCode = "403", description = "Acceso no autorizado") })
	@PostMapping(value = "/artists/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createArtist(@RequestParam("name") String name,
			@RequestParam("nationality") String nationality,
			@RequestParam("dateOfBirth") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirth,
			@RequestParam(value = "dateOfDeath", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfDeath,
			@RequestParam(value = "biography") String biography, @RequestParam(value = "image") MultipartFile imageFile)
			throws IOException {
		if (name == null || name.isBlank() || nationality == null || nationality.isBlank()) {
			return ResponseEntity.badRequest().body("El nombre y la nacionalidad son obligatorios.");
		}

		Artist artist = new Artist();
		artist.setName(name.trim());
		artist.setNationality(nationality.trim());
		artist.setDateOfBirth(dateOfBirth);
		if (dateOfDeath != null)
			artist.setDateOfDeath(dateOfDeath);
		if (biography != null && !biography.isBlank())
			artist.setBiography(biography.trim());
		if (imageFile != null && !imageFile.isEmpty())
			artist.setImage(imageFile.getBytes());
		artist.setInsertionDate(new Date());

		artistService.save(artist);

		return ResponseEntity.status(HttpStatus.CREATED).body("Artista creado correctamente - ID " + artist.getId());
	}

	@Operation(summary = "Actualizar un artista existente", description = "Permite modificar campos opcionales como nombre, nacionalidad, fechas, biografía e imagen de un artista.", responses = {
			@ApiResponse(responseCode = "200", description = "Artista actualizado correctamente"),
			@ApiResponse(responseCode = "404", description = "Artista no encontrado"),
			@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
			@ApiResponse(responseCode = "403", description = "Acceso no autorizado") })
	@PutMapping(value = "/artists/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateArtist(@PathVariable Long id, @RequestParam(required = false) String name,
			@RequestParam(required = false) String nationality,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfBirth,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfDeath,
			@RequestParam(required = false) String biography, @RequestParam(required = false) MultipartFile image)
			throws IOException {

		Optional<Artist> optionalArtist = artistService.findById(id);
		if (optionalArtist.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artista no encontrado");
		}

		Artist artist = optionalArtist.get();

		if (name != null && !name.isBlank())
			artist.setName(name.trim());
		if (nationality != null && !nationality.isBlank())
			artist.setNationality(nationality.trim());
		if (dateOfBirth != null)
			artist.setDateOfBirth(dateOfBirth);
		if (dateOfDeath != null)
			artist.setDateOfDeath(dateOfDeath);
		if (biography != null && !biography.isBlank())
			artist.setBiography(biography.trim());
		if (image != null && !image.isEmpty())
			artist.setImage(image.getBytes());

		artistService.save(artist);

		return ResponseEntity.ok("Artista actualizado correctamente");
	}

	@Operation(summary = "Eliminar un artista", description = "Elimina al artista y desvincula sus obras relacionadas", responses = {
			@ApiResponse(responseCode = "200", description = "Artista eliminado correctamente"),
			@ApiResponse(responseCode = "404", description = "Artista no encontrado"),
			@ApiResponse(responseCode = "403", description = "Acceso no autorizado") })
	@DeleteMapping("/artists/delete/{id}")
	public ResponseEntity<?> deleteArtist(@PathVariable Long id) {
		Optional<Artist> optionalArtist = artistService.findById(id);
		if (optionalArtist.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artista no encontrado");
		}

		Artist artist = optionalArtist.get();

		for (Artwork artwork : artist.getArtworks()) {
			artwork.setArtist(null);
			artworkService.save(artwork);
		}

		artistService.delete(artist);

		return ResponseEntity.ok("Artista eliminado correctamente");
	}

	@Operation(summary = "Crear un nuevo museo", description = "Permite a los administradores registrar un nuevo museo.")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Museo creado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos inválidos"),
			@ApiResponse(responseCode = "403", description = "Acceso no autorizado") })
	@PostMapping(value = "/museums/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createMuseum(@RequestParam String name, @RequestParam String location,
			@RequestParam Integer founded, @RequestParam(required = false) String description,
			@RequestParam(required = false) MultipartFile imageFile) throws IOException {

		if (name.isBlank() || location.isBlank() || founded < 1000 || founded > 2100) {
			return ResponseEntity.badRequest().body("Nombre, ubicación y año de fundación válido son obligatorios.");
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

		return ResponseEntity.status(HttpStatus.CREATED).body("Museo creado correctamente - ID " + museum.getId());
	}

	@Operation(summary = "Actualizar un museo existente", description = "Modifica los datos de un museo.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Museo actualizado correctamente"),
			@ApiResponse(responseCode = "404", description = "Museo no encontrado"),
			@ApiResponse(responseCode = "400", description = "Datos inválidos") })
	@PutMapping(value = "/museums/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateMuseum(@PathVariable Long id, @RequestParam(required = false) String name,
			@RequestParam(required = false) String location, @RequestParam(required = false) Integer founded,
			@RequestParam(required = false) String description, @RequestParam(required = false) MultipartFile image)
			throws IOException {

		Optional<Museum> optionalMuseum = museumService.findById(id);
		if (optionalMuseum.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Museo no encontrado");
		}

		Museum museum = optionalMuseum.get();

		if (name != null && !name.isBlank())
			museum.setName(name.trim());
		if (location != null && !location.isBlank())
			museum.setLocation(location.trim());
		if (founded != null && founded >= 1000 && founded <= 2100)
			museum.setFounded(founded);
		if (description != null && !description.isBlank())
			museum.setDescription(description.trim());
		if (image != null && !image.isEmpty())
			museum.setImage(image.getBytes());

		museumService.save(museum);

		return ResponseEntity.ok("Museo actualizado correctamente");
	}

	@Operation(summary = "Eliminar un museo", description = "Elimina el museo y desvincula sus obras.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Museo eliminado correctamente"),
			@ApiResponse(responseCode = "404", description = "Museo no encontrado") })
	@DeleteMapping("/museums/delete/{id}")
	public ResponseEntity<?> deleteMuseum(@PathVariable Long id) {
		Optional<Museum> optionalMuseum = museumService.findById(id);
		if (optionalMuseum.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Museo no encontrado");
		}

		Museum museum = optionalMuseum.get();

		for (Artwork artwork : museum.getArtworks()) {
			artwork.setMuseum(null);
			artworkService.save(artwork);
		}

		museumService.delete(museum);
		return ResponseEntity.ok("Museo eliminado correctamente");
	}

	@Operation(summary = "Crear un nuevo usuario", description = "Permite a un administrador crear una cuenta de usuario con roles y opcionalmente una imagen de perfil.")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
			@ApiResponse(responseCode = "400", description = "Datos inválidos"),
			@ApiResponse(responseCode = "409", description = "Usuario o email ya existente"),
			@ApiResponse(responseCode = "403", description = "Acceso no autorizado") })
	@PostMapping(value = "/users/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createUser(@RequestParam String username, @RequestParam String email,
			@RequestParam String password, @RequestParam String passwordConfirmation,
			@RequestParam(required = false) String location, @RequestParam(required = false) String biography,
			@RequestParam(name = "image", required = false) MultipartFile imageFile) throws IOException {
		if (username.isBlank() || email.isBlank()) {
			return ResponseEntity.badRequest().body("Username y email son obligatorios.");
		}
		if (userService.findByName(username).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Nombre de usuario ya existente.");
		}
		if (userService.findByEmail(email).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email ya en uso.");
		}
		if (password.isBlank() || passwordConfirmation.isBlank()) {
			return ResponseEntity.badRequest().body("Ambos campos de contraseña son obligatorios.");
		}
		if (!password.equals(passwordConfirmation)) {
			return ResponseEntity.badRequest().body("Las contraseñas no coinciden.");
		}

		User user = new User(username.trim(), email.trim(), passwordEncoder.encode(password));
		user.setCreatedAt(new Date());
		user.setRoles(List.of("USER"));

		if (location != null && !location.isBlank())
			user.setLocation(location.trim());
		if (biography != null && !biography.isBlank())
			user.setBiography(biography.trim());
		if (imageFile != null && !imageFile.isEmpty()) {
			user.setImage(imageFile.getBytes());
		}

		userService.save(user);

		return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado correctamente - ID " + user.getId());
	}

	@Operation(summary = "Actualizar usuario existente", description = "Permite a un administrador editar datos de un usuario, incluyendo contraseña, roles e imagen.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
			@ApiResponse(responseCode = "400", description = "Datos inválidos"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
			@ApiResponse(responseCode = "403", description = "Acceso no autorizado") })
	@PutMapping(value = "/users/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestParam(required = false) String name,
			@RequestParam(required = false) String email, @RequestParam(required = false) String biography,
			@RequestParam(required = false) String location, @RequestParam List<String> roles,
			@RequestParam(name = "image", required = false) MultipartFile image,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String passwordConfirmation) throws IOException {
		Optional<User> opt = userService.findById(id);
		if (opt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
		}
		User user = opt.get();

		if (password != null && passwordConfirmation != null) {
			if (!password.equals(passwordConfirmation)) {
				return ResponseEntity.badRequest().body("Las contraseñas no coinciden.");
			}
			user.setPassword(passwordEncoder.encode(password));
		}

		if (name != null && !name.isBlank())
			user.setName(name.trim());
		if (email != null && !email.isBlank())
			user.setEmail(email.trim());
		if (biography != null && !biography.isBlank())
			user.setBiography(biography.trim());
		if (location != null && !location.isBlank())
			user.setLocation(location.trim());

		user.setRoles(roles);

		if (image != null && !image.isEmpty()) {
			user.setImage(image.getBytes());
		}

		userService.save(user);

		return ResponseEntity.ok("Usuario actualizado correctamente.");
	}

	@Operation(summary = "Eliminar mi cuenta", description = "Permite al usuario autenticado eliminar su propia cuenta", security = @SecurityRequirement(name = "bearerAuth"), responses = {
			@ApiResponse(responseCode = "204", description = "Cuenta eliminada correctamente"),
			@ApiResponse(responseCode = "401", description = "No autenticado"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado") })
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/users/delete/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {

		Optional<User> optionalUser = userService.findById(id);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.badRequest().body("Usuario no encontrado");
		}

		userService.delete(optionalUser.get());
		return ResponseEntity.ok("Usuario eliminado correctamente");
	}

	@Operation(summary = "Crear una nueva review", description = "Permite a los administradores crear una review para cualquier usuario y obra.")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Review creada correctamente"),
			@ApiResponse(responseCode = "400", description = "Datos inválidos"),
			@ApiResponse(responseCode = "404", description = "Usuario o obra no encontrada"),
			@ApiResponse(responseCode = "409", description = "Ya existe una review del usuario para esta obra"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado") })
	@PostMapping(value = "/reviews/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createReview(HttpServletRequest request, @RequestParam Long userId,
			@RequestParam Long artworkId, @RequestParam Double rating, @RequestParam(required = false) String comment) {
		if (!request.isUserInRole("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
		}
		if (userId == null || artworkId == null || rating == null || rating < 1 || rating > 5) {
			return ResponseEntity.badRequest()
					.body("Parámetros inválidos: userId, artworkId y rating entre 1 y 5 son obligatorios");
		}

		Optional<User> optionalUser = userService.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
		}
		Optional<Artwork> optionalArtwork = artworkService.findById(artworkId);
		if (optionalArtwork.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Obra no encontrada");
		}

		User user = optionalUser.get();
		Artwork artwork = optionalArtwork.get();

		if (reviewService.existsByUserAndArtwork(user, artwork)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe una review para este usuario y obra");
		}

		Review review = new Review();
		review.setUser(user);
		review.setArtwork(artwork);
		review.setRating(rating);
		review.setComment(comment != null && !comment.isBlank() ? comment.trim() : null);
		review.setCreatedAt(new Date());

		reviewService.save(review);
		artworkService.updateAverageRating(artwork);

		return ResponseEntity.status(HttpStatus.CREATED).body("Review creada correctamente - ID " + review.getId());
	}

	@Operation(summary = "Editar una review existente", description = "Permite a los administradores editar cualquier review.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Review actualizada correctamente"),
			@ApiResponse(responseCode = "400", description = "Datos inválidos"),
			@ApiResponse(responseCode = "404", description = "Review, usuario o obra no encontrada"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado") })
	@PutMapping(value = "/reviews/edit/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> editReview(HttpServletRequest request, @PathVariable Long reviewId,
			@RequestParam Long userId, @RequestParam Long artworkId, @RequestParam Double rating,
			@RequestParam(required = false) String comment) {
		if (!request.isUserInRole("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
		}

		if (userId == null || artworkId == null || rating == null || rating < 1 || rating > 5) {
			return ResponseEntity.badRequest()
					.body("Parámetros inválidos: userId, artworkId y rating entre 1 y 5 son obligatorios");
		}

		Optional<Review> optionalReview = reviewService.findById(reviewId);
		if (optionalReview.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review no encontrada");
		}

		Optional<User> optionalUser = userService.findById(userId);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
		}
		Optional<Artwork> optionalArtwork = artworkService.findById(artworkId);
		if (optionalArtwork.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Obra no encontrada");
		}

		Review review = optionalReview.get();
		review.setUser(optionalUser.get());
		review.setArtwork(optionalArtwork.get());
		review.setRating(rating);
		review.setComment(comment != null && !comment.isBlank() ? comment.trim() : null);
		review.setCreatedAt(new Date());

		reviewService.save(review);
		artworkService.updateAverageRating(review.getArtwork());

		return ResponseEntity.ok("Review actualizada correctamente");
	}

	@Operation(summary = "Eliminar una review", description = "Permite a los administradores eliminar cualquier review.")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Review eliminada correctamente"),
			@ApiResponse(responseCode = "404", description = "Review no encontrada"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado") })
	@DeleteMapping("/reviews/delete/{reviewId}")
	public ResponseEntity<?> deleteReview(HttpServletRequest request, @PathVariable Long reviewId) {
		if (!request.isUserInRole("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado");
		}

		Optional<Review> optionalReview = reviewService.findById(reviewId);
		if (optionalReview.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review no encontrada");
		}

		Review review = optionalReview.get();
		Artwork artwork = review.getArtwork();

		reviewService.delete(review);
		artworkService.updateAverageRating(artwork);

		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Obtener estadísticas generales del sistema", description = "Devuelve estadísticas agregadas sobre reviews, usuarios, obras, artistas y museos. Solo accesible para usuarios con rol ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas correctamente"),
			@ApiResponse(responseCode = "403", description = "Acceso no autorizado") })
	@GetMapping("/stats")
	public ResponseEntity<Map<String, Object>> getStats() throws JsonProcessingException {
		List<Review> reviews = reviewService.findAll();
		List<User> users = userService.findAll();
		List<Artwork> artworks = artworkService.findAll();

		Map<String, Object> response = new LinkedHashMap<>();

		Map<Integer, Long> starsData = reviewService.countStarsGivenByEveryone();
		response.put("starsData", starsData);

		double avgGiven = round(reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0), 2);
		response.put("avgGiven", avgGiven);
		response.put("numReviews", reviews.size());

		Map<String, Long> favArtworks = reviews.stream()
				.collect(Collectors.groupingBy(
						r -> r.getArtwork().getTitle() + " (" + r.getArtwork().getArtist().getName() + ")",
						Collectors.counting()));
		response.put("top10favArtworks", topNByCount(favArtworks, 10));

		Map<String, Long> artistCounts = reviews.stream()
				.collect(Collectors.groupingBy(r -> r.getArtwork().getArtist().getName(), Collectors.counting()));
		response.put("top10Artists", topNByCount(artistCounts, 10));

		Map<String, Long> favMuseums = reviews.stream().collect(Collectors.groupingBy(
				r -> r.getArtwork().getMuseum().getName() + " (" + r.getArtwork().getMuseum().getLocation() + ")",
				Collectors.counting()));
		response.put("top10favMuseums", topNByCount(favMuseums, 10));

		Map<String, Long> topUsers = reviews.stream()
				.collect(Collectors.groupingBy(r -> r.getUser().getName(), Collectors.counting()));
		response.put("top10Users", topNByCount(topUsers, 10));

		Map<String, Long> top10FavUsers = users.stream()
				.filter(u -> u.getName() != null && !u.getFavoriteArtworks().isEmpty())
				.sorted(Comparator.comparingInt((User u) -> u.getFavoriteArtworks().size()).reversed()).limit(10)
				.collect(Collectors.toMap(User::getName, u -> (long) u.getFavoriteArtworks().size(), (e1, e2) -> e1,
						LinkedHashMap::new));
		response.put("top10FavUsers", top10FavUsers);

		Map<String, Long> top10ArtworksMostFavourited = artworks.stream()
				.filter(a -> a.getTitle() != null && a.getArtist() != null && a.getArtist().getName() != null)
				.sorted(Comparator.comparingInt((Artwork a) -> a.getUsersWhoFavorited().size()).reversed()).limit(10)
				.collect(Collectors.toMap(a -> a.getTitle() + " (" + a.getArtist().getName() + ")",
						a -> (long) a.getUsersWhoFavorited().size(), (e1, e2) -> e1, LinkedHashMap::new));
		response.put("top10ArtworksMostFavourited", top10ArtworksMostFavourited);

		response.put("numUsers", userService.count());
		response.put("numArtworks", artworkService.count());
		response.put("numArtists", artistService.count());
		response.put("numMuseums", museumService.count());

		return ResponseEntity.ok(response);
	}

	private <K> Map<K, Long> topNByCount(Map<K, Long> map, int n) {
		return map.entrySet().stream().sorted(Map.Entry.<K, Long>comparingByValue().reversed()).limit(n)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	private double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
