package app.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.awt.print.Pageable;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.any;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import controller.MuseumController;
import model.Artwork;
import model.Museum;
import service.ArtworkService;
import service.MuseumService;
import service.UserService;

class MuseumControllerTest {

    private MockMvc mockMvc;
    private MuseumService museumService;
    private ArtworkService artworkService;
    private UserService userService;
    private ResourceLoader resourceLoader;
    private MuseumController controller;

    private Museum sampleMuseum;
    private Artwork sampleArtwork;
    private byte[] fakeImage;

    @BeforeEach
    void setup() {
        museumService   = mock(MuseumService.class);
        artworkService  = mock(ArtworkService.class);
        userService     = mock(UserService.class);
        resourceLoader  = mock(ResourceLoader.class);

        controller = new MuseumController();
        TestUtils.inject(controller, "museumService",   museumService);
        TestUtils.inject(controller, "artworkService",  artworkService);
        TestUtils.inject(controller, "userService",     userService);
        TestUtils.inject(controller, "resourceLoader",  resourceLoader);

        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        vr.setPrefix("/WEB-INF/views/");
        vr.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .setViewResolvers(vr)
                                 .build();

        sampleMuseum = new Museum();
        sampleMuseum.setId(7L);
        sampleMuseum.setName("MuseoX");

        sampleArtwork = new Artwork();
        sampleArtwork.setId(99L);
        sampleArtwork.setTitle("Obra99");

        fakeImage = "img-bytes".getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void getMuseumsPage_returnsOkAndView() throws Exception {
        when(museumService.findAll()).thenReturn(List.of(sampleMuseum));
        when(museumService.getMuseumsPage(any(), anyList(), (org.springframework.data.domain.Pageable) Mockito.<Pageable>any()))
            .thenReturn(new PageImpl<>(List.of(sampleMuseum)));

        mockMvc.perform(get("/museums"))
               .andExpect(status().isOk())
               .andExpect(view().name("museums"))
               .andExpect(model().attributeExists("museumsPage"))
               .andExpect(model().attribute("currentPage", is(1)));
    }

    @Test
    void getMuseumsPage_redirectsIfPageInvalid() throws Exception {
        mockMvc.perform(get("/museums?page=0"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrlPattern("/museums?page=1&sort=title"));
    }

    @Test
    void getMuseumDetails_withValidId_returnsView() throws Exception {
        sampleMuseum.setArtworks(List.of());
        when(museumService.findById(7L)).thenReturn(Optional.of(sampleMuseum));
        when(userService.artworkIsFavorite("ana", 99L)).thenReturn(true);
        when(artworkService.getArtworksByMuseumPage(eq(7L), any(), any(), any(), any()))
        .thenReturn(new PageImpl<>(List.of()));
        
        mockMvc.perform(get("/museum/7").principal(() -> "ana"))
               .andExpect(status().isOk())
               .andExpect(view().name("museum-details"))
               .andExpect(model().attributeExists("museum"))
               .andExpect(model().attributeExists("artworksPage"))
               .andExpect(model().attribute("logged", is(true)))
               .andExpect(model().attribute("userName", is("ana")));
    }

    @Test
    void getMuseumImage_withImage_returnsBytes() throws Exception {
        sampleMuseum.setImage(fakeImage);
        when(museumService.findById(7L)).thenReturn(Optional.of(sampleMuseum));

        mockMvc.perform(get("/museum/image/7"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.IMAGE_JPEG))
               .andExpect(content().bytes(fakeImage));
    }

    @Test
    void getMuseumImage_withoutImage_returnsDefault() throws Exception {
        when(museumService.findById(7L)).thenReturn(Optional.of(sampleMuseum));
        when(resourceLoader.getResource(anyString()))
            .thenReturn(new ByteArrayResource("default-img".getBytes(StandardCharsets.UTF_8)));

        mockMvc.perform(get("/museum/image/7"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.IMAGE_PNG))
               .andExpect(content().bytes("default-img".getBytes(StandardCharsets.UTF_8)));
    }

    private class TestUtils {
		public static void inject(Object target, String fieldName, Object toInject) {
			Field field = ReflectionUtils.findField(target.getClass(), fieldName);
			if (field == null) {
				throw new IllegalArgumentException(
						"No se encontró el campo '" + fieldName + "' en la clase " + target.getClass().getName());
			}
			field.setAccessible(true);
			ReflectionUtils.setField(field, target, toInject);
		}
	}
}
