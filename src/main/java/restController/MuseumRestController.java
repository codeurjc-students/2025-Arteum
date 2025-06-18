package restController;

import java.io.IOException;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import model.Museum;
import restDTO.ArtworkRequest;
import restDTO.MuseumRequest;
import service.MuseumService;

@RestController
@RequestMapping("/api/v1/museums")
@Tag(name = "Museums", description = "Endpoints relacionados con museos")
public class MuseumRestController {

    @Autowired
    private MuseumService museumService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Operation(summary = "Obtener listado paginado de museos",
        description = "Devuelve una página de museos opcionalmente filtrados por búsqueda y ordenados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de museos obtenida correctamente",
                content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MuseumRequest.class)))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
        })
    @GetMapping
    public Page<MuseumRequest> getMuseumsPage(
        @Parameter(description = "Texto a buscar en nombre o ubicación") @RequestParam(required = false) String search,
        @Parameter(description = "Número de página (1-indexado)") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "6") int size,
        @Parameter(description = "Campo de ordenación: name | location | founded") @RequestParam(defaultValue = "name") String sort) {

        Sort sortBy;
        switch (sort) {
            case "location":
                sortBy = Sort.by(Sort.Direction.ASC, "location");
                break;
            case "founded":
                sortBy = Sort.by(Sort.Direction.ASC, "founded");
                break;
            default:
                sortBy = Sort.by(Sort.Direction.ASC, "name");
        }

        Pageable pageable = PageRequest.of(page - 1, size, sortBy);
        Page<Museum> museums = museumService.getMuseumsPage(search, pageable);

        return new PageImpl<>(
            museums.getContent().stream().map(this::toDto).collect(Collectors.toList()),
            pageable,
            museums.getTotalElements()
        );
    }

    @Operation(summary = "Obtener detalles de un museo",
        description = "Devuelve los datos detallados de un museo a partir de su ID.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Museo encontrado", content = @Content(schema = @Schema(implementation = MuseumRequest.class))),
            @ApiResponse(responseCode = "404", description = "Museo no encontrado", content = @Content)
        })
    @GetMapping("/{id}")
    public ResponseEntity<MuseumRequest> getMuseumById(
        @Parameter(description = "ID del museo") @PathVariable Long id) {

        Optional<Museum> optionalMuseum = museumService.findById(id);
        return optionalMuseum
            .map(museum -> ResponseEntity.ok(toDetailDto(museum)))
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener imagen de un museo",
        description = "Devuelve la imagen del museo, o una por defecto si no tiene.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Imagen obtenida correctamente",
                content = @Content(mediaType = "image/jpeg")),
            @ApiResponse(responseCode = "404", description = "Museo no encontrado", content = @Content)
        })
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(
        @Parameter(description = "ID del museo") @PathVariable Long id) throws IOException {

        Optional<Museum> museum = museumService.findById(id);
        if (museum.isPresent() && museum.get().getImage() != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(museum.get().getImage());
        } else {
            Resource resource = resourceLoader.getResource("classpath:static/assets/img/TBD.png");
            byte[] defaultImage = StreamUtils.copyToByteArray(resource.getInputStream());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(defaultImage);
        }
    }

    // Helper
    private MuseumRequest toDto(Museum museum) {
        MuseumRequest dto = new MuseumRequest();
        dto.setId(museum.getId());
        dto.setName(museum.getName());
        dto.setDescription(museum.getDescription());
        dto.setLocation(museum.getLocation());
        dto.setFounded(museum.getFounded());
        return dto;
    }
    
    private MuseumRequest toDetailDto(Museum museum) {
    	MuseumRequest dto = new MuseumRequest();
        dto.setId(museum.getId());
        dto.setName(museum.getName());
        dto.setLocation(museum.getLocation());
        dto.setDescription(museum.getDescription());
        dto.setFounded(museum.getFounded());

        List<ArtworkRequest> artworks = museum.getArtworks().stream().map(artwork -> {
        	ArtworkRequest a = new ArtworkRequest();
            a.setId(artwork.getId());
            a.setTitle(artwork.getTitle());
            a.setCreationYear(artwork.getCreationYear());
            a.setDescription(artwork.getDescription());
            a.setAverageRating(artwork.getAverageRating());
            return a;
        }).toList();

        dto.setArtworks(artworks);
        return dto;
    }
}
