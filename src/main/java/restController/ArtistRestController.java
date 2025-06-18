package restController;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import model.Artist;
import restDTO.ArtistRequest;
import restDTO.ArtworkRequest;
import restDTO.PagedResponse;
import service.ArtistService;

@RestController
@RequestMapping("/api/v1/artists")
@Tag(name = "Artists", description = "Gestión de artistas y filtrado")
public class ArtistRestController {

	@Autowired
	private ArtistService artistService;
	@Autowired
	private ResourceLoader resourceLoader;

	@Operation(summary = "Obtener lista paginada de artistas", description = "Filtro por nombre, nacionalidad, siglos y orden. "
			+ "Devuelve DTOs resumidos.", responses = {
					@ApiResponse(responseCode = "200", description = "Lista de artistas", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ArtistRequest.class)))),
					@ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content),
					@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content) })
	@GetMapping
	public PagedResponse<ArtistRequest> getArtists(
			@Parameter(description = "Filtro de texto en nombre") @RequestParam(required = false) String search,
			@Parameter(description = "Orden: name,dateOfBirth,nationality") @RequestParam(defaultValue = "name") String sort,
			@Parameter(description = "Filtrar por nacionalidades") @RequestParam(required = false) List<String> nationalityFilters,
			@Parameter(description = "Filtrar por siglos") @RequestParam(required = false) List<Integer> centuries,
			@Parameter(description = "Página (1...n)") @RequestParam(defaultValue = "1") int page,
			@Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "6") int size) {
		Sort sortBy = switch (sort) {
		case "dateOfBirth" -> Sort.by(Sort.Direction.ASC, "dateOfBirth");
		case "nationality" -> Sort.by(Sort.Direction.ASC, "nationality");
		default -> Sort.by(Sort.Direction.ASC, "name");
		};
		PageRequest pageable = PageRequest.of(page - 1, size, sortBy);

		Page<Artist> artists = artistService.getArtistsPage(search, nationalityFilters, pageable);
		List<ArtistRequest> content = artists.getContent().stream().map(artist -> {
			ArtistRequest dto = new ArtistRequest();
			dto.setId(artist.getId());
			dto.setName(artist.getName());
			dto.setNationality(artist.getNationality());
			if (artist.getDateOfBirth() != null)
				dto.setDateOfBirth(getYear(artist.getDateOfBirth()));
			if (artist.getDateOfDeath() != null)
				dto.setDateOfDeath(getYear(artist.getDateOfDeath()));
			return dto;
		}).collect(Collectors.toList());

		return toPagedResponse(content, artists);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener detalles de un artista")
	@ApiResponse(responseCode = "200", description = "Datos del artista", content = @Content(mediaType = "application/json"))
	@ApiResponse(responseCode = "404", description = "Artista no encontrado", content = @Content)
	public ResponseEntity<ArtistRequest> getArtist(@PathVariable Long id) {
		return artistService.findById(id).map(this::toArtistResponse).map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Operation(summary = "Obtener imagen del artista o placeholder")
	@ApiResponse(responseCode = "200", content = @Content(mediaType = "image/jpeg"))
	@GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getArtistImage(@PathVariable Long id) throws IOException {
		Optional<Artist> opt = artistService.findById(id);
		if (opt.isPresent() && opt.get().getImage() != null) {
			return ResponseEntity.ok().body(opt.get().getImage());
		}
		Resource r = resourceLoader.getResource("classpath:static/assets/img/TBD.png");
		byte[] defaultImage = StreamUtils.copyToByteArray(r.getInputStream());
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(defaultImage);
	}

	@Operation(summary = "Top 10 artistas", description = "Devuelve los 10 artistas con mayor puntuación media en sus obras.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Artistas obtenidos correctamente", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArtistRequest.class)))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor") })
	@GetMapping("/top-rated")
	public ResponseEntity<List<ArtistRequest>> getTopArtists() {
		List<Artist> topArtists = artistService.findTop10ArtistsByAverageArtworkRatingRaw();
		List<ArtistRequest> response = topArtists.stream().map(this::toArtistResponseTopRated).toList();
		return ResponseEntity.ok(response);
	}

	private ArtistRequest toArtistResponseTopRated(Artist artist) {
		ArtistRequest dto = new ArtistRequest();
		dto.setId(artist.getId());
		dto.setName(artist.getName());
		dto.setNationality(artist.getNationality());
		if (artist.getDateOfBirth() != null)
			dto.setDateOfBirth(getYear(artist.getDateOfBirth()));
		if (artist.getDateOfDeath() != null)
			dto.setDateOfDeath(getYear(artist.getDateOfDeath()));
		return dto;
	}

	private ArtistRequest toArtistResponse(Artist artist) {
		ArtistRequest dto = new ArtistRequest();
		dto.setId(artist.getId());
		dto.setName(artist.getName());
		dto.setNationality(artist.getNationality());
		if (artist.getDateOfBirth() != null)
			dto.setDateOfBirth(getYear(artist.getDateOfBirth()));
		if (artist.getDateOfDeath() != null)
			dto.setDateOfDeath(getYear(artist.getDateOfDeath()));

		List<ArtworkRequest> artworks = artist.getArtworks().stream().map(a -> {
			ArtworkRequest r = new ArtworkRequest();
			r.setId(a.getId());
			r.setTitle(a.getTitle());
			r.setCreationYear(a.getCreationYear());
			r.setDescription(a.getDescription());
			r.setAverageRating(a.getAverageRating());
			return r;
		}).toList();

		dto.setArtworks(artworks);
		return dto;
	}

	private <T> PagedResponse<T> toPagedResponse(List<T> content, Page<?> page) {
		PagedResponse<T> resp = new PagedResponse<>();
		resp.setContent(content);
		resp.setPage(page.getNumber() + 1);
		resp.setSize(page.getSize());
		resp.setTotalElements(page.getTotalElements());
		resp.setTotalPages(page.getTotalPages());
		return resp;
	}

	private Integer getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
}
