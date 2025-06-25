package restController;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import model.Artwork;
import model.Review;
import model.User;
import restDTO.ArtworkRequest;
import restDTO.ChangePasswordRequest;
import restDTO.ReviewRequest;
import restDTO.UpdateUserDataRequest;
import restDTO.UserProfileResponse;
import restDTO.UserReviewsResponse;
import restDTO.UserStatsResponse;
import restDTO.UserSummaryResponse;
import service.ArtworkService;
import service.ReviewService;
import service.UserPdfExportService;
import service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User Management API")
public class UserRestController {

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

	@Operation(summary = "Listar usuarios", description = "Devuelve una lista paginada de usuarios con filtros opcionales por búsqueda y ordenamiento", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserSummaryResponse.class)))),
			@ApiResponse(responseCode = "401", description = "No autorizado", content = @Content) })
	@GetMapping
	public ResponseEntity<Page<UserSummaryResponse>> getUsers(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "12") int size, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "createdAt") String sort, @AuthenticationPrincipal UserDetails userDetails) {

		User currentUser = findUser(userDetails);

		if (page < 1)
			page = 1;
		Pageable pageable = PageRequest.of(page - 1, size, getSortFromParam(sort));

		if (isAdmin(currentUser) && search != null && search.matches("\\d+")) {
			Long id = Long.parseLong(search);
			Optional<User> userOpt = userService.findById(id);
			if (userOpt.isPresent()) {
				UserSummaryResponse userDto = toUserSummary(userOpt.get(), currentUser);
				return ResponseEntity.ok(new PageImpl<>(List.of(userDto), pageable, 1));
			} else {
				return ResponseEntity.ok(new PageImpl<>(List.of(), pageable, 0));
			}
		}

		Page<User> usersPage = userService.getUsersPage(search, pageable);
		List<UserSummaryResponse> dtos = usersPage.getContent().stream().map(u -> toUserSummary(u, currentUser))
				.toList();

		return ResponseEntity.ok(new PageImpl<>(dtos, pageable, usersPage.getTotalElements()));
	}
	
	@Operation(summary = "Get the information of the user logged in")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Showing information of the user", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content),
			@ApiResponse(responseCode = "401", description = "Not permissions needed", content = @Content) })
	@GetMapping("/me")
	public ResponseEntity<User> get_me(HttpServletRequest request) throws IOException {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			User user = userService.findByEmail(principal.getName())
					.orElseGet(() -> userService.findByName(principal.getName()).orElse(null));
			user.setPassword(null);
			user.setFollowers(null);
			user.setFollowing(null);
			user.setReviews(null);
			user.setFavoriteArtworks(null);
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@Operation(summary = "Perfil público de un usuario", description = "Obtiene información pública de un usuario por su nombre de usuario")
	@ApiResponse(responseCode = "200", description = "Perfil encontrado", content = @Content(schema = @Schema(implementation = UserProfileResponse.class)))
	@ApiResponse(responseCode = "404", description = "Usuario no encontrado")
	@GetMapping("/public-profile/{username}")
	public ResponseEntity<UserProfileResponse> getPublicProfile(@PathVariable String username,
			@AuthenticationPrincipal UserDetails userDetails) {
		Optional<User> optUser = userService.findByEmail(username).or(() -> userService.findByName(username));
		if (optUser.isEmpty())
			return ResponseEntity.notFound().build();

		User user = optUser.get();
		if (user.getRoles().contains("ADMIN"))
			return ResponseEntity.notFound().build();

		User currentUser = userService.findByEmail(userDetails.getUsername())
				.orElseGet(() -> userService.findByName(userDetails.getUsername()).orElse(null));

		UserProfileResponse dto = new UserProfileResponse();
		dto.setName(user.getName());
		dto.setBiography(user.getBiography());
		dto.setLocation(user.getLocation());
		dto.setFollowersCount(user.getFollowers().size());
		dto.setFollowingCount(user.getFollowing().size());
		dto.setReviewsCount(user.getReviews().size());
		double avgRating = user.getReviews().stream().mapToDouble(r -> r.getRating()).average().orElse(0.0);
		dto.setReviewsAverage(Math.round(avgRating * 100.0) / 100.0);

		List<model.Review> reviews = user.getReviews();
		dto.setReviewsCount(reviews.size());
		double avg = round(reviews.stream().mapToDouble(model.Review::getRating).average().orElse(0.0), 2);
		dto.setReviewsAverage(avg);

		if (currentUser != null && !Objects.equals(user.getId(), currentUser.getId())) {
			dto.setFollowing(userService.isFollowing(currentUser.getId(), user.getId()));
		}

		List<ArtworkRequest> artworks = user.getFavoriteArtworks().stream()
				.sorted(Comparator.comparing(Artwork::getAverageRating).reversed()).limit(8).map(this::toDto)
				.collect(Collectors.toList());
		dto.setFavoriteArtworks(artworks);
		return ResponseEntity.ok(dto);
	}

	@Operation(summary = "Obtener obras recomendadas", description = "Devuelve una lista de obras recomendadas para el usuario autenticado.", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Obras recomendadas obtenidas correctamente", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArtworkRequest.class)))),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado", content = @Content) })
	@GetMapping("/recommended-artworks")
	public ResponseEntity<List<ArtworkRequest>> getRecommendedArtworks(@AuthenticationPrincipal UserDetails userDetails,
			Principal principal) {
		User user = findUser(userDetails);

		List<Artwork> recommendedArtworks = recommendArtworks(user.getId(), 12);

		return ResponseEntity.ok(mapToDtoWithFavorite(recommendedArtworks, principal));
	}

	@Operation(summary = "Generar PDF del perfil de usuario", description = "Devuelve un PDF con información detallada del usuario")
	@ApiResponse(responseCode = "200", description = "PDF generado correctamente")
	@GetMapping("/pdf")
	public ResponseEntity<byte[]> downloadUserPdf(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
		User user = userService.findByEmail(userDetails.getUsername())
				.orElseGet(() -> userService.findByName(userDetails.getUsername()).orElse(null));

		byte[] pdfBytes = userPdfExportService.generateUserPdf(user.getId());

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuario_" + user.getName() + ".pdf")
				.body(pdfBytes);
	}

	@Operation(summary = "Eliminar mi cuenta", description = "Permite al usuario autenticado eliminar su propia cuenta", security = @SecurityRequirement(name = "bearerAuth"), responses = {
			@ApiResponse(responseCode = "204", description = "Cuenta eliminada correctamente"),
			@ApiResponse(responseCode = "401", description = "No autenticado"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado") })
	@DeleteMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal UserDetails userDetails) {
		User user = findUser(userDetails);

		if (user != null) {
			userService.deleteUser(user.getId());
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.notFound().build();
	}

	@Operation(summary = "Estadísticas del usuario autenticado", description = "Devuelve datos estadísticos del usuario autenticado como estrellas dadas, seguidores, promedio de reseñas y artistas favoritos")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas correctamente"),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado") })
	@GetMapping("/stats")
	public ResponseEntity<UserStatsResponse> getUserStats(@AuthenticationPrincipal UserDetails userDetails) {

		User user = findUser(userDetails);

		UserStatsResponse response = new UserStatsResponse();

		response.setName(user.getName());

		Map<Integer, Long> starsData = reviewService.countStarsGivenByUser(user.getId());
		response.setStarsData(starsData);

		response.setFollowing(user.getFollowing().size());
		response.setFollowers(user.getFollowers().size());

		double avgGiven = user.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0.0);
		response.setAvgGiven(Math.round(avgGiven * 100.0) / 100.0);

		response.setNumReviews(user.getReviews().size());

		Map<String, Long> favByArtist = user.getFavoriteArtworks().stream().filter(a -> a.getArtist() != null)
				.collect(Collectors.groupingBy(a -> a.getArtist().getName(), Collectors.counting()));

		Map<String, Long> top10favByArtist = favByArtist.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		response.setTop10favByArtist(top10favByArtist);

		Map<String, Long> artistCounts = user.getReviews().stream()
				.filter(r -> r.getArtwork() != null && r.getArtwork().getArtist() != null)
				.collect(Collectors.groupingBy(r -> r.getArtwork().getArtist().getName(), Collectors.counting()));

		Map<String, Long> top10Artists = artistCounts.entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		response.setTop10Artists(top10Artists);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Reseñas del usuario autenticado paginadas", description = "Obtiene las reseñas del usuario autenticado con paginación y ordenamiento. También indica si la obra está marcada como favorita.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Reseñas obtenidas correctamente"),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para usuarios ADMIN"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado") })
	@GetMapping("/reviews")
	public ResponseEntity<UserReviewsResponse> getUserReviews(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "12") int size, @RequestParam(defaultValue = "createdAt") String sort,
			Principal principal, @AuthenticationPrincipal UserDetails userDetails) {

		User user = findUser(userDetails);

		if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		if (page < 1) {
			page = 1;
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

		if (reviewsPage.getTotalPages() > 0 && page > reviewsPage.getTotalPages()) {
			pageable = PageRequest.of(reviewsPage.getTotalPages() - 1, size, sortBy);
			reviewsPage = reviewService.getReviewsByUser(user.getId(), pageable);
			page = reviewsPage.getNumber() + 1;
		}

		UserReviewsResponse response = new UserReviewsResponse();
		response.setCurrentPage(page);
		response.setPreviousPage(reviewsPage.hasPrevious() ? page - 1 : null);
		response.setNextPage(reviewsPage.hasNext() ? page + 1 : null);
		response.setTotalReviews((int) reviewsPage.getTotalElements());
		response.setTotalPages(reviewsPage.getTotalPages());

		if (reviewsPage.getTotalElements() > 0) {
			int start = reviewsPage.getNumber() * reviewsPage.getSize() + 1;
			int end = Math.min(start + reviewsPage.getNumberOfElements() - 1, (int) reviewsPage.getTotalElements());
			response.setRange(String.format("%02d-%02d", start, end));

			List<ReviewRequest> reviewsDto = reviewsPage.stream().map(review -> {
				ReviewRequest r = new ReviewRequest();
				r.setId(review.getId());
				r.setComment(
						(review.getComment() != null && !review.getComment().isBlank()) ? review.getComment() : null);
				r.setRating(review.getRating());

				Artwork artwork = review.getArtwork();
				ArtworkRequest artSummary = new ArtworkRequest();
				artSummary.setId(artwork.getId());
				artSummary.setTitle(artwork.getTitle());
				artSummary.setArtist(artwork.getArtist() != null ? artwork.getArtist().getName() : null);
				r.setArtworkId(artSummary.getId());
				artSummary.setFavorite(userService.artworkIsFavorite(user.getName(), artwork.getId()));

				return r;
			}).collect(Collectors.toList());

			response.setReviews(reviewsDto);
		} else {
			response.setRange(null);
			response.setReviews(List.of());
		}

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Lista paginada de seguidos", description = "Obtiene seguidos del usuario autenticado con paginación y ordenamiento")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Seguidos obtenidos con éxito", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserSummaryResponse.class)))),
			@ApiResponse(responseCode = "401", description = "No autenticado") })
	@GetMapping("/following")
	public ResponseEntity<Page<UserSummaryResponse>> getUserFollowing(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "12") int size,
			@RequestParam(defaultValue = "createdAt") String sort, Principal principal) {

		User currentUser = findUser(userDetails);

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

		Page<User> followersPage = userService.getFollowingPage(currentUser.getId(), pageable);

		Page<UserSummaryResponse> dtoPage = followersPage.map(follower -> {
			UserSummaryResponse dto = new UserSummaryResponse();
			dto.setId(follower.getId());
			dto.setName(follower.getName());
			dto.setLocation(follower.getLocation());
			dto.setCreatedAt(follower.getCreatedAt().toString());
			dto.setFollowing(userService.isFollowing(currentUser.getId(), follower.getId()));
			return dto;
		});

		return ResponseEntity.ok(dtoPage);
	}

	@Operation(summary = "Lista paginada de seguidores", description = "Obtiene seguidores del usuario autenticado con paginación y ordenamiento")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Seguidores obtenidos con éxito", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserSummaryResponse.class)))),
			@ApiResponse(responseCode = "401", description = "No autenticado") })
	@GetMapping("/followers")
	public ResponseEntity<Page<UserSummaryResponse>> getUserFollowers(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "12") int size,
			@RequestParam(defaultValue = "createdAt") String sort, Principal principal) {

		User currentUser = findUser(userDetails);

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

		Page<User> followersPage = userService.getFollowersPage(currentUser.getId(), pageable);

		Page<UserSummaryResponse> dtoPage = followersPage.map(follower -> {
			UserSummaryResponse dto = new UserSummaryResponse();
			dto.setId(follower.getId());
			dto.setName(follower.getName());
			dto.setLocation(follower.getLocation());
			dto.setCreatedAt(follower.getCreatedAt().toString());
			dto.setFollowing(userService.isFollowing(currentUser.getId(), follower.getId()));
			return dto;
		});

		return ResponseEntity.ok(dtoPage);
	}

	@Operation(summary = "Seguir a un usuario", description = "Permite al usuario autenticado seguir a otro usuario especificado por nombre de usuario.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Usuario seguido exitosamente"),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para usuarios ADMIN"),
			@ApiResponse(responseCode = "404", description = "Usuario a seguir no encontrado") })
	@PostMapping("/follow/{username}")
	public ResponseEntity<Map<String, String>> followUser(@PathVariable String username, Principal principal) {

		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
		}

		// Obtener usuario actual
		User follower = userService.findByEmail(principal.getName())
				.orElseGet(() -> userService.findByName(principal.getName())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")));

		if (follower.getRoles() != null && follower.getRoles().contains("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "Los administradores no pueden seguir usuarios"));
		}

		User followed = userService.findByName(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario a seguir no encontrado"));

		if (followed.getRoles() != null && followed.getRoles().contains("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "No se puede seguir a un administrador"));
		}

		userService.followUser(follower.getId(), followed.getId());

		return ResponseEntity.ok(Map.of("message", "Usuario seguido exitosamente"));
	}

	@Operation(summary = "Dejar de seguir a un usuario", description = "Permite al usuario autenticado dejar de seguir a otro usuario especificado por nombre de usuario.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Usuario dejado de seguir exitosamente"),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
			@ApiResponse(responseCode = "403", description = "Acceso denegado para usuarios ADMIN"),
			@ApiResponse(responseCode = "404", description = "Usuario a dejar de seguir no encontrado") })
	@PostMapping("/unfollow/{username}")
	public ResponseEntity<Map<String, String>> unfollowUser(@PathVariable String username, Principal principal) {

		if (principal == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
		}

		User follower = userService.findByEmail(principal.getName())
				.orElseGet(() -> userService.findByName(principal.getName())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")));

		if (follower.getRoles() != null && follower.getRoles().contains("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "Los administradores no pueden dejar de seguir usuarios"));
		}

		User followed = userService.findByName(username).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario a dejar de seguir no encontrado"));

		if (followed.getRoles() != null && followed.getRoles().contains("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "No se puede dejar de seguir a un administrador"));
		}

		userService.unfollowUser(follower.getId(), followed.getId());

		return ResponseEntity.ok(Map.of("message", "Usuario dejado de seguir exitosamente"));
	}

	@Operation(summary = "Listar obras de arte favoritas", description = "Devuelve una lista paginada de obras de arte con filtros opcionales por búsqueda, artista y rango de puntuación.", responses = {
			@ApiResponse(responseCode = "200", description = "Lista obtenida correctamente", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArtworkRequest.class)))),
			@ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor") })
	@GetMapping("/favourites-artworks")
	public Page<ArtworkRequest> getFavouritesArtworks(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "6") int size,
			@RequestParam(required = false) String search, @RequestParam(defaultValue = "title") String sort,
			@RequestParam(required = false) List<Long> artist,
			@RequestParam(required = false) List<String> ratingRanges) {
		User user = findUser(userDetails);

		Sort sortBy = switch (sort) {
		case "averageRating" -> Sort.by(Sort.Direction.DESC, "averageRating");
		case "artist.name" -> Sort.by(Sort.Direction.ASC, "artist.name");
		case "creationYear" -> Sort.by(Sort.Direction.ASC, "creationYear");
		case "museum.name" -> Sort.by(Sort.Direction.ASC, "museum.name");
		default -> Sort.by(Sort.Direction.ASC, "title");
		};

		Pageable pageable = PageRequest.of(page - 1, size, sortBy);
		Page<Artwork> artworksPage;

		if (ratingRanges != null && !ratingRanges.isEmpty()) {
			List<double[]> rangesParsed = ratingRanges.stream().map(r -> {
				String[] p = r.split("-");
				return new double[] { Double.parseDouble(p[0]), Double.parseDouble(p[1]) };
			}).toList();
			artworksPage = artworkService.findByUsersWhoFavorited_Id(user.getId(), search, artist, rangesParsed,
					pageable);
		} else {
			artworksPage = artworkService.findByUsersWhoFavorited_Id(user.getId(), search, artist, pageable);
		}

		return new PageImpl<>(artworksPage.getContent().stream().map(this::toDto).collect(Collectors.toList()),
				pageable, artworksPage.getTotalElements());
	}

	@Operation(summary = "Cambiar la contraseña del usuario autenticado", description = "Permite al usuario autenticado cambiar su contraseña proporcionando la actual y la nueva.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Contraseña cambiada correctamente"),
			@ApiResponse(responseCode = "400", description = "Datos inválidos o no coinciden las contraseñas"),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado") })
	@PostMapping("/change-password")
	public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		User user = findUser(userDetails);
		 System.out.println("Received ChangePasswordRequest: " + request);
		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "La nueva contraseña no coincide con la confirmación"));
		}

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			return ResponseEntity.badRequest().body(Map.of("error", "La contraseña actual no coincide"));
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userService.save(user);

		return ResponseEntity.ok(Map.of("message", "Contraseña cambiada correctamente"));
	}

	@Operation(summary = "Actualizar datos del perfil del usuario autenticado", description = "Permite actualizar la biografía y la ubicación del usuario autenticado.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Datos actualizados correctamente"),
			@ApiResponse(responseCode = "400", description = "No se proporcionaron datos para actualizar"),
			@ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado") })
	@PostMapping("/change-data")
	public ResponseEntity<?> updateUserData(@Valid @RequestBody UpdateUserDataRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		User user = findUser(userDetails);

		boolean hasLocation = request.getLocation() != null && !request.getLocation().isBlank();
		boolean hasBiography = request.getBiography() != null && !request.getBiography().isBlank();

		if (!hasLocation && !hasBiography) {
			return ResponseEntity.badRequest().body(Map.of("error", "No se proporcionaron datos para actualizar"));
		}

		if (hasLocation) {
			user.setLocation(request.getLocation());
		}
		if (hasBiography) {
			user.setBiography(request.getBiography());
		}

		userService.save(user);

		return ResponseEntity.ok(Map.of("message", "Datos actualizados correctamente"));
	}

	@Operation(summary = "Actualizar imagen de usuario", description = "Carga una nueva imagen de perfil para el usuario autenticado")
	@ApiResponse(responseCode = "200", description = "Imagen actualizada correctamente")
	@ApiResponse(responseCode = "400", description = "Imagen vacía o error al procesarla")
	@PostMapping("/image")
	public ResponseEntity<?> uploadUserImage(@RequestParam("image") MultipartFile image, @AuthenticationPrincipal UserDetails userDetails) {
		User user = findUser(userDetails);

		if (image == null || image.isEmpty()) {
			return ResponseEntity.badRequest().body("No se pudo actualizar la imagen. Archivo vacío.");
		}

		if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Los administradores no pueden cambiar su imagen");
		}

		try {
			user.setImage(image.getBytes());
			userService.save(user);
			return ResponseEntity.ok("Imagen actualizada correctamente.");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la imagen.");
		}
	}

	@Operation(summary = "Eliminar imagen de usuario", description = "Elimina la imagen de perfil del usuario y restaura la imagen por defecto")
	@ApiResponse(responseCode = "200", description = "Imagen eliminada correctamente")
	@DeleteMapping("/image")
	public ResponseEntity<?> deleteUserImage(@AuthenticationPrincipal UserDetails userDetails) {
		User user = findUser(userDetails);

		if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Los administradores no pueden eliminar su imagen");
		}

		if (user.getImage() != null) {
			user.setImage(null);
			userService.save(user);
			return ResponseEntity.ok("Imagen eliminada. Se ha restaurado la imagen por defecto.");
		} else {
			return ResponseEntity.badRequest().body("La imagen ya es por defecto.");
		}
	}

	@Operation(summary = "Obtener imagen de usuario", description = "Devuelve la imagen de perfil del usuario por su nombre")
	@ApiResponse(responseCode = "200", description = "Imagen devuelta correctamente", content = @Content(mediaType = "image/jpeg"))
	@GetMapping("/image/{username}")
	public ResponseEntity<byte[]> getUserImage(@PathVariable String username) throws IOException {
		Optional<User> optionalUser = userService.findByEmail(username);
		if (optionalUser.isEmpty()) {
			optionalUser = userService.findByName(username);
		}

		User user = optionalUser.orElse(null);

		Resource defaultImage = new ClassPathResource("static/assets/img/user/default-user-icon.jpg");

		if (user != null) {
			if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
				defaultImage = new ClassPathResource("static/assets/img/user/admin_image.jpg");
			}
			if (user.getImage() != null) {
				return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(user.getImage());
			}
		}

		byte[] defaultImgBytes = StreamUtils.copyToByteArray(defaultImage.getInputStream());
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(defaultImgBytes);
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

	protected User findUser(UserDetails userDetails) {
		String username = userDetails.getUsername();

		return userService.findByEmail(username).or(() -> userService.findByName(username))
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
	}

	private boolean isAdmin(User user) {
		return user.getRoles() != null && user.getRoles().contains("ADMIN");
	}

	private Sort getSortFromParam(String sort) {
		return switch (sort) {
		case "nameAsc" -> Sort.by(Sort.Direction.ASC, "name");
		case "nameDesc" -> Sort.by(Sort.Direction.DESC, "name");
		default -> Sort.by(Sort.Direction.DESC, "createdAt");
		};
	}

	private UserSummaryResponse toUserSummary(User u, User currentUser) {
		UserSummaryResponse dto = new UserSummaryResponse();
		dto.setId(u.getId());
		dto.setName(u.getName());
		dto.setLocation(u.getLocation());
		dto.setCreatedAt(u.getCreatedAt().toString());
		dto.setFollowing(userService.isFollowing(currentUser.getId(), u.getId()));
		return dto;
	}

	private List<ArtworkRequest> mapToDtoWithFavorite(List<Artwork> artworks, Principal principal) {
		return artworks.stream().map(a -> {
			ArtworkRequest dto = toDto(a);
			if (principal != null) {
				boolean isFav = userService.artworkIsFavorite(principal.getName(), a.getId());
				dto.setFavorite(isFav);
			}
			return dto;
		}).collect(Collectors.toList());
	}

	private ArtworkRequest toDto(Artwork a) {
		ArtworkRequest dto = new ArtworkRequest();
		dto.setId(a.getId());
		dto.setTitle(a.getTitle());
		dto.setCreationYear(a.getCreationYear());
		dto.setDescription(a.getDescription());
		dto.setAverageRating(a.getAverageRating());
		dto.setArtist(a.getArtist().getName() + " (" + (a.getArtist().getId()) + ")");
		dto.setMuseum(a.getMuseum().getName() + " (" + (a.getMuseum().getId()) + ")");
		return dto;
	}

	private static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
