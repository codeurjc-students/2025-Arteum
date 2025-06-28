package app.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import controller.HomeController;
import model.Artwork;
import service.ArtistService;
import service.ArtworkService;
import service.UserService;

class HomeControllerTest {

    private MockMvc mockMvc;
    private ArtworkService artworkService;
    private UserService userService;
    private ArtistService artistService;
    private HomeController controller;

    private Artwork sampleArt1;
    private Artwork sampleArt2;

    @BeforeEach
    void setup() {
        artworkService = mock(ArtworkService.class);
        userService = mock(UserService.class);
        artistService = mock(ArtistService.class);

        controller = new HomeController();
        TestUtils.inject(controller, "artworkService", artworkService);
        TestUtils.inject(controller, "userService", userService);
        TestUtils.inject(controller, "artistService", artistService);

        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        vr.setPrefix("/WEB-INF/views/");
        vr.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(vr)
                .build();

        sampleArt1 = new Artwork();
        sampleArt1.setId(1L);
        sampleArt1.setTitle("A1");
        sampleArt1.setAverageRating(4.5);

        sampleArt2 = new Artwork();
        sampleArt2.setId(2L);
        sampleArt2.setTitle("A2");
        sampleArt2.setAverageRating(3.8);
    }

    @Test
    void home_withoutLogin_showsRawLists() throws Exception {
        when(artworkService.findTop7ByOrderByAverageRatingDesc())
            .thenReturn(List.of(sampleArt1, sampleArt2));
        when(artworkService.find7RandomArtworks())
            .thenReturn(List.of(sampleArt2, sampleArt1));
        when(artistService.findTop10ArtistsByAverageArtworkRating())
            .thenReturn(List.of(/* aquí objetos Artist */));

        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attribute("top7Artworks", contains(sampleArt1, sampleArt2)))
            .andExpect(model().attribute("find7RandomArtworks", contains(sampleArt2, sampleArt1)))
            .andExpect(model().attributeExists("top10Artists"))
            .andExpect(model().attribute("header01", is(true)))
            .andExpect(model().attribute("logged", is(false)));
    }

    @Test
    void home_withLogin_showsMapsWithFavStatus() throws Exception {
        when(artworkService.findTop7ByOrderByAverageRatingDesc())
            .thenReturn(List.of(sampleArt1));
        when(artworkService.find7RandomArtworks())
            .thenReturn(List.of(sampleArt2));
        when(artistService.findTop10ArtistsByAverageArtworkRating())
            .thenReturn(List.of(/* aquí objetos Artist */));
        when(userService.artworkIsFavorite("juan", 1L)).thenReturn(true);
        when(userService.artworkIsFavorite("juan", 2L)).thenReturn(false);

        Principal mockPrincipal = () -> "juan";

        mockMvc.perform(get("/").principal(mockPrincipal))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attributeExists("top7Artworks"))
            .andExpect(model().attribute("top7Artworks", hasSize(1)))
            .andExpect(model().attributeExists("find7RandomArtworks"))
            .andExpect(model().attribute("find7RandomArtworks", hasSize(1)))
            .andExpect(model().attributeExists("top10Artists"))
            .andExpect(model().attribute("header01", is(true)))
            .andExpect(model().attribute("logged", is(true)))
            .andExpect(model().attribute("userName", is("juan")))
            .andExpect(model().attribute("admin", is(false)));
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
