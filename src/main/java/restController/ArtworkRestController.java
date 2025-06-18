package restController;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
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
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import model.Artwork;
import model.User;
import restDTO.ArtworkRequest;
import restDTO.ReviewRequest;
import service.ArtworkService;
import service.UserService;

@RestController
@RequestMapping("/api/v1/artworks")
@Tag(name = "Artworks", description = "Endpoints relacionados con obras de arte")
public class ArtworkRestController {

	@Autowired
	private ArtworkService artworkService;

	@Autowired
	private UserService userService;

	@Autowired
	private ResourceLoader resourceLoader;

	@Operation(summary = "Listar obras de arte", description = "Devuelve una lista paginada de obras de arte con filtros opcionales por búsqueda, artista y rango de puntuación.", responses = {
			@ApiResponse(responseCode = "200", description = "Lista obtenida correctamente", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArtworkRequest.class)))),
			@ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor") })
	@GetMapping
	public Page<ArtworkRequest> getArtworks(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "6") int size, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "title") String sort, @RequestParam(required = false) List<Long> artist,
			@RequestParam(required = false) List<String> ratingRanges) {

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
			artworksPage = artworkService.getArtworksPage(search, artist, rangesParsed, pageable);
		} else {
			artworksPage = artworkService.getArtworksPage(search, artist, pageable);
		}

		return new PageImpl<>(artworksPage.getContent().stream().map(this::toDto).collect(Collectors.toList()),
				pageable, artworksPage.getTotalElements());
	}

	@Operation(summary = "Obtener detalles de obra", description = "Devuelve los detalles de una obra de arte", responses = {
			@ApiResponse(responseCode = "200", description = "Detalles de la obra", content = @Content(schema = @Schema(implementation = ArtworkRequest.class))),
			@ApiResponse(responseCode = "404", description = "Obra no encontrada") })
	@GetMapping("/{id}")
	public ResponseEntity<ArtworkRequest> getArtworkById(@PathVariable Long id) {
		return artworkService.findById(id).map(a -> ResponseEntity.ok(toDtoDetails(a)))
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Añadir obra a favoritos", description = "Permite a un usuario autenticado añadir una obra a su lista de favoritos")
	@ApiResponse(responseCode = "200", description = "Obra añadida correctamente")
	@ApiResponse(responseCode = "404", description = "Obra o usuario no encontrado")
	@GetMapping("/favourite/add/{id}")
	public ResponseEntity<String> addFavorite(@PathVariable Long id, Principal principal) {
		if (principal != null) {
			Optional<User> optUser = userService.findByEmail(principal.getName());
			if (optUser.isEmpty()) {
				optUser = userService.findByName(principal.getName());
			}

			if (optUser.isPresent()) {
				User user = optUser.get();
				Optional<Artwork> optArtwork = artworkService.findById(id);
				if (optArtwork.isPresent()) {
					user.getFavoriteArtworks().add(optArtwork.get());
					userService.save(user);
					return ResponseEntity.ok("Artwork added to favorites.");
				}
			}
		}
		return ResponseEntity.status(404).body("User or artwork not found.");
	}

	@Operation(summary = "Eliminar obra de favoritos", description = "Permite a un usuario autenticado eliminar una obra de su lista de favoritos")
	@ApiResponse(responseCode = "200", description = "Obra eliminada correctamente")
	@ApiResponse(responseCode = "404", description = "Obra o usuario no encontrado")
	@GetMapping("/favourite/delete/{id}")
	public ResponseEntity<String> removeFavorite(@PathVariable Long id, Principal principal) {
		if (principal != null) {
			Optional<User> optUser = userService.findByEmail(principal.getName());
			if (optUser.isEmpty()) {
				optUser = userService.findByName(principal.getName());
			}

			if (optUser.isPresent()) {
				User user = optUser.get();
				Optional<Artwork> optArtwork = artworkService.findById(id);
				if (optArtwork.isPresent()) {
					user.getFavoriteArtworks().remove(optArtwork.get());
					userService.save(user);
					return ResponseEntity.ok("Artwork removed from favorites.");
				}
			}
		}
		return ResponseEntity.status(404).body("User or artwork not found.");
	}

	@Operation(summary = "Obtener imagen de obra", description = "Devuelve la imagen asociada a la obra de arte", responses = {
			@ApiResponse(responseCode = "200", description = "Imagen obtenida correctamente"),
			@ApiResponse(responseCode = "404", description = "Obra no encontrada o sin imagen") })
	@GetMapping("/image/{id}")
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
	
	@Operation(summary = "Obras más valoradas", description = "Devuelve las 7 obras con mayor puntuación promedio. Si el usuario está autenticado, se incluye si es su favorita.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Obras obtenidas correctamente",
	        content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArtworkRequest.class)))),
	    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/top-rated")
	public ResponseEntity<List<ArtworkRequest>> getTopRatedArtworks(Principal principal) {
	    List<Artwork> topArtworks = artworkService.findTop7ByOrderByAverageRatingDesc();
	    return ResponseEntity.ok(mapToDtoWithFavorite(topArtworks, principal));
	}

	@Operation(summary = "Obras aleatorias", description = "Devuelve 7 obras seleccionadas aleatoriamente. Si el usuario está autenticado, se indica si son sus favoritas.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Obras aleatorias obtenidas correctamente",
	        content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArtworkRequest.class)))),
	    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/random")
	public ResponseEntity<List<ArtworkRequest>> getRandomArtworks(Principal principal) {
	    List<Artwork> randomArtworks = artworkService.find7RandomArtworks();
	    return ResponseEntity.ok(mapToDtoWithFavorite(randomArtworks, principal));
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

	private ArtworkRequest toDtoDetails(Artwork a) {
		ArtworkRequest dto = new ArtworkRequest();
		dto.setId(a.getId());
		dto.setTitle(a.getTitle());
		dto.setCreationYear(a.getCreationYear());
		dto.setDescription(a.getDescription());
		dto.setAverageRating(a.getAverageRating());
		dto.setArtist(a.getArtist().getName() + " (" + (a.getArtist().getId()) + ")");
		dto.setMuseum(a.getMuseum().getName() + " (" + (a.getMuseum().getId()) + ")");

		if (a.getReviews() != null) {
			List<ReviewRequest> reviews = a.getReviews().stream().map(r -> {
				ReviewRequest rr = new ReviewRequest();
				rr.setId(r.getId());
				rr.setRating(r.getRating());
				rr.setComment(r.getComment());
				rr.setUsername(r.getUser().getName());
				return rr;
			}).collect(Collectors.toList());
			dto.setReviews(reviews);
		}

		return dto;
	}

}