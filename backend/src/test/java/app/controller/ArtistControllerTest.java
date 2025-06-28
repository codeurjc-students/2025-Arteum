package app.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Field;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import controller.ArtistController;
import model.Artist;
import service.ArtistService;
import service.ArtworkService;

class ArtistControllerTest {
	private MockMvc mockMvc;
	private ArtistService artistService;
	private ArtworkService artworkService;
	private ResourceLoader resourceLoader;

	private ArtistController controller;
	private Artist sampleArtist;

	@BeforeEach
	void setup() {
		artistService = mock(ArtistService.class);
		artworkService = mock(ArtworkService.class);
		resourceLoader = mock(ResourceLoader.class);
		controller = new ArtistController();

		TestUtils.inject(controller, "artistService", artistService);
		TestUtils.inject(controller, "artworkService", artworkService);
		TestUtils.inject(controller, "resourceLoader", resourceLoader);

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".html");

		mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();
		sampleArtist = new Artist();
		sampleArtist.setId(1L);
		sampleArtist.setName("Da Vinci");
		sampleArtist.setNationality("Italian");
		sampleArtist.setDateOfBirth(new GregorianCalendar(1452, Calendar.APRIL, 15).getTime());
	}

	@Test
	void getArtistDetails_withValidId_returnsView() throws Exception {
		when(artistService.getArtistById(1L)).thenReturn(sampleArtist);
		when(artworkService.getArtworksByArtistPage(eq(1L), anyString())).thenReturn(Collections.emptyList());
		when(artworkService.getArtworksByArtistPageAndMuseum(eq(1L), isNull(), // search == null
				isNull(), // museumIds == null
				any(org.springframework.data.domain.Pageable.class)))
				.thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

		mockMvc.perform(get("/artists/1")).andExpect(status().isOk()).andExpect(view().name("artist-details"))
				.andExpect(model().attributeExists("artist"));
	}

	@Test
	void getArtistImage_withImage_returnsBytes() throws Exception {
		byte[] imageBytes = "fake-image".getBytes();
		sampleArtist.setImage(imageBytes);
		when(artistService.findById(1L)).thenReturn(Optional.of(sampleArtist));

		mockMvc.perform(get("/artist/image/1")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.IMAGE_JPEG)).andExpect(content().bytes(imageBytes));
	}

	@Test
	void getArtistImage_withoutImage_returnsDefault() throws Exception {
		when(artistService.findById(1L)).thenReturn(Optional.of(sampleArtist));
		when(resourceLoader.getResource(anyString()))
				.thenReturn(new org.springframework.core.io.ByteArrayResource("default-image".getBytes()));

		mockMvc.perform(get("/artist/image/1")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.IMAGE_PNG))
				.andExpect(content().bytes("default-image".getBytes()));
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
