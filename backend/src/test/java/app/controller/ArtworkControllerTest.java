package app.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import controller.ArtworkController;
import model.Artist;
import model.Artwork;
import service.ArtworkService;
import service.ReviewService;

class ArtworkControllerTest {
	private MockMvc mockMvc;
	private ArtworkService artworkService;
	private ReviewService reviewService;
	private ResourceLoader resourceLoader;
	private ArtworkController controller;

	private Artwork sampleArtwork;
	private byte[] dummyImage;

	@BeforeEach
	void setup() {
		artworkService = mock(ArtworkService.class);
		reviewService = mock(ReviewService.class);
		resourceLoader = mock(ResourceLoader.class);

		controller = new ArtworkController();
		TestUtils.inject(controller, "artworkService", artworkService);
		TestUtils.inject(controller, "reviewService", reviewService);
		TestUtils.inject(controller, "resourceLoader", resourceLoader);

		InternalResourceViewResolver vr = new InternalResourceViewResolver();
		vr.setPrefix("/WEB-INF/views/");
		vr.setSuffix(".html");
		mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(vr).build();

		sampleArtwork = new Artwork();
		sampleArtwork.setId(42L);
		Artist artist = new Artist();
		artist.setId(7L);
		artist.setName("Da Vinci");
		sampleArtwork.setArtist(artist);

		dummyImage = "fake-img".getBytes(StandardCharsets.UTF_8);
	}

	@Test
	void getArtworksPage_returnsOkAndView() throws Exception {
		Page<Artwork> pageImpl = new PageImpl<>(List.of(sampleArtwork));
		when(artworkService.getArtworksPage(anyString(), anyList(), any(Pageable.class))).thenReturn(pageImpl);
		when(artworkService.getArtworksPage(anyString(), anyList(), anyList(), any(Pageable.class)))
				.thenReturn(pageImpl);
	}

	@Test
	void getArtworkDetails_withValidId_andNoReviews_showsEmptyFlag() throws Exception {
		when(artworkService.findById(42L)).thenReturn(Optional.of(sampleArtwork));
		when(artworkService.find7RandomArtworks()).thenReturn(List.of());
		when(reviewService.getReviewsByArtworkPage(eq(42L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

		mockMvc.perform(get("/artworks/42")).andExpect(status().isOk()).andExpect(view().name("artwork-details"))
				.andExpect(model().attributeExists("artwork")).andExpect(model().attribute("reviewsEmpty", is(true)));
	}

	@Test
	void getArtworkImage_withImage_returnsBytes() throws Exception {
		sampleArtwork.setImage(dummyImage);
		when(artworkService.findById(42L)).thenReturn(Optional.of(sampleArtwork));

		mockMvc.perform(get("/artwork/image/42")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.IMAGE_JPEG)).andExpect(content().bytes(dummyImage));
	}

	@Test
	void getArtworkImage_withoutImage_returnsDefault() throws Exception {
		when(artworkService.findById(42L)).thenReturn(Optional.of(sampleArtwork));
		when(resourceLoader.getResource(anyString()))
				.thenReturn(new ByteArrayResource("default".getBytes(StandardCharsets.UTF_8)));

		mockMvc.perform(get("/artwork/image/42")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.IMAGE_PNG))
				.andExpect(content().bytes("default".getBytes(StandardCharsets.UTF_8)));
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
