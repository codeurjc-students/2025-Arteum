package restController;

import java.net.URI;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import model.Artwork;
import model.Review;
import model.User;
import restDTO.ReviewRequest;
import service.ArtworkService;
import service.ReviewService;
import service.UserService;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reviews", description = "Operaciones relacionadas con valoraciones de obras")
public class ReviewRestController {

	@Autowired
	private ReviewService reviewService;
	@Autowired
	private ArtworkService artworkService;
	@Autowired
	private UserService userService;

	@Operation(summary = "Crear una nueva review", description = "Permite a un usuario logueado crear una review sobre una obra de arte", security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"))
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Review creada correctamente"),
			@ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
			@ApiResponse(responseCode = "404", description = "Obra no encontrada", content = @Content),
			@ApiResponse(responseCode = "409", description = "Ya existe una review del usuario para esta obra", content = @Content) })
	@PostMapping
	public ResponseEntity<?> createReview(@Valid @RequestBody ReviewRequest reviewRequest,
			@AuthenticationPrincipal UserDetails userDetails) {
		User user = findUser(userDetails);
		Artwork artwork = artworkService.findById(reviewRequest.getArtworkId())
				.orElseThrow(() -> new IllegalArgumentException("Obra no encontrada"));

		if (reviewService.existsByUserAndArtwork(user, artwork)) {
			return ResponseEntity.status(409).body("Ya has valorado esta obra");
		}

		Review review = new Review();
		review.setUser(user);
		review.setArtwork(artwork);
		review.setRating(reviewRequest.getRating());
		review.setComment(reviewRequest.getComment());
		review.setCreatedAt(new Date());

		reviewService.save(review);
		artworkService.updateAverageRating(artwork);

		return ResponseEntity.created(URI.create("/api/v1/reviews/" + artwork.getId())).body("Review creada");
	}

	@Operation(summary = "Editar una review existente", description = "Permite a un usuario editar su propia review para una obra específica", security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"))
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Review actualizada correctamente"),
			@ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
			@ApiResponse(responseCode = "404", description = "Obra o review no encontrada", content = @Content) })
	@PutMapping("/{artworkId}")
	public ResponseEntity<?> editReview(@PathVariable Long artworkId, @Valid @RequestBody ReviewRequest reviewRequest,
			@AuthenticationPrincipal UserDetails userDetails) {
		User user = findUser(userDetails);
		Artwork artwork = artworkService.findById(artworkId)
				.orElseThrow(() -> new IllegalArgumentException("Obra no encontrada"));

		Review review = reviewService.findByUserAndArtwork(user, artwork)
				.orElseThrow(() -> new IllegalArgumentException("Review no encontrada"));

		review.setRating(reviewRequest.getRating());
		review.setComment(reviewRequest.getComment());
		review.setCreatedAt(new Date());
		reviewService.save(review);
		artworkService.updateAverageRating(artwork);

		return ResponseEntity.ok("Review actualizada");
	}

	@Operation(summary = "Eliminar una review", description = "Permite a un usuario autenticado eliminar su review sobre una obra de arte específica", security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"))
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Review eliminada correctamente"),
			@ApiResponse(responseCode = "404", description = "Review no encontrada o no pertenece al usuario", content = @Content) })
	@DeleteMapping("/{artworkId}")
	public ResponseEntity<?> deleteReview(@PathVariable Long artworkId,
			@AuthenticationPrincipal UserDetails userDetails) {
		User user = findUser(userDetails);
		Artwork artwork = artworkService.findById(artworkId)
				.orElseThrow(() -> new IllegalArgumentException("Obra no encontrada"));

		Review review = reviewService.findByUserAndArtwork(user, artwork)
				.orElseThrow(() -> new IllegalArgumentException("Review no encontrada"));

		reviewService.delete(review);
		artworkService.updateAverageRating(artwork);

		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Obtener la review del usuario para una obra", description = "Devuelve la review si existe para el usuario autenticado", security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth"))
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Review encontrada"),
			@ApiResponse(responseCode = "404", description = "No existe review", content = @Content) })
	@GetMapping("/{artworkId}")
	public ResponseEntity<?> getReviewByUserAndArtwork(@PathVariable Long artworkId,
			@AuthenticationPrincipal UserDetails userDetails) {
		User user = findUser(userDetails);
		Artwork artwork = artworkService.findById(artworkId)
				.orElseThrow(() -> new IllegalArgumentException("Obra no encontrada"));

		return reviewService.findByUserAndArtwork(user, artwork).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	private User findUser(UserDetails userDetails) {
		return userService.findByEmail(userDetails.getUsername())
				.orElseGet(() -> userService.findByName(userDetails.getUsername())
						.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado")));
	}
}
