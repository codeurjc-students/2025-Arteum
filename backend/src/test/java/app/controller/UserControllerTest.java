package app.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import controller.UserController;
import model.User;
import service.ArtworkService;
import service.ReviewService;
import service.UserPdfExportService;
import service.UserService;

public class UserControllerTest {

	private MockMvc mockMvc;
	private UserService userService;
	private ArtworkService artworkService;
	private ReviewService reviewService;
	private UserPdfExportService userPdfExportService;
	private PasswordEncoder passwordEncoder;
	private ResourceLoader resourceLoader;

	private User sampleUser;

	@BeforeEach
	void setup() {
		userService = mock(UserService.class);
		artworkService = mock(ArtworkService.class);
		reviewService = mock(ReviewService.class);
		userPdfExportService = mock(UserPdfExportService.class);
		resourceLoader  = mock(ResourceLoader.class);
		passwordEncoder  = mock(PasswordEncoder.class);
		
		UserController controller = new UserController();
		inject(controller, "userService", userService);
		inject(controller, "artworkService", artworkService);
		inject(controller, "reviewService", reviewService);
		inject(controller, "userPdfExportService", userPdfExportService);
		inject(controller, "resourceLoader",  resourceLoader);
		inject(controller, "passwordEncoder",  passwordEncoder);
		
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".html");

		mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();

		sampleUser = new User();
		sampleUser.setId(1L);
		sampleUser.setName("ana");
		sampleUser.setEmail("ana@example.com");
		sampleUser.setCreatedAt(new Date());
	}

	@Test
	void loginPage_withErrorParam_displaysErrorMessage() throws Exception {
		mockMvc.perform(get("/login").param("error", "true")).andExpect(status().isOk()).andExpect(view().name("login"))
				.andExpect(model().attributeExists("errorMessage"))
				.andExpect(model().attribute("errorMessage", is("Usuario/email o contraseña incorrectos.")));
	}

	@Test
	void loginPage_withoutError_displaysLoginView() throws Exception {
		mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
	}

	@Test
	void registerPage_returnsRegisterView() throws Exception {
		mockMvc.perform(get("/register")).andExpect(status().isOk()).andExpect(view().name("register"));
	}

	@Test
	void forgotPasswordPage_returnsForgotPasswordView() throws Exception {
		mockMvc.perform(get("/forgot-password")).andExpect(status().isOk()).andExpect(view().name("forgot-password"));
	}

	@Test
	void publicProfile_withValidUsername_returnsView() throws Exception {
	    sampleUser.setReviews(List.of());
	    sampleUser.setFavoriteArtworks(Set.of());

	    when(userService.findByEmail("ana")).thenReturn(Optional.of(sampleUser));
	    when(userService.findByName("ana")).thenReturn(Optional.of(sampleUser));
	    when(userService.isFollowing(anyLong(), anyLong())).thenReturn(false);

	    mockMvc.perform(get("/public-profile/ana").principal(() -> "ana"))
	           .andExpect(status().isOk())
	           .andExpect(view().name("public-profile"))
	           .andExpect(model().attributeExists("name"))
	           .andExpect(model().attribute("name", is("ana")))
	           .andExpect(model().attribute("email", is("ana@example.com")));
	}

    @Test
    void dashboardProfile_withAuthenticatedUser_returnsView() throws Exception {
        when(userService.findByEmail("ana")).thenReturn(Optional.of(sampleUser));
        when(userService.findByName("ana")).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/dashboard-profile").principal(() -> "ana"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard-profile"))
                .andExpect(model().attribute("name", is("ana")))
                .andExpect(model().attribute("email", is("ana@example.com")));
    }

    @Test
    void downloadUserPdf_withAuthenticatedUser_returnsPdf() throws Exception {
        when(userService.findByEmail("ana")).thenReturn(Optional.of(sampleUser));
        when(userService.findByName("ana")).thenReturn(Optional.of(sampleUser));
        when(userPdfExportService.generateUserPdf(1L)).thenReturn("PDF".getBytes());

        mockMvc.perform(get("/user/generate-pdf").principal(() -> "ana"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=usuario_ana.pdf")))
                .andExpect(content().contentType("application/pdf"));
    }

    @Test
	void followUser_redirectsToReferer() throws Exception {
		when(userService.findByEmail("ana")).thenReturn(Optional.of(sampleUser));
		when(userService.findByName("juan")).thenReturn(Optional.of(new User("juan", "j@example.com", "pass")));
		mockMvc.perform(get("/follow/juan").principal(() -> "ana").header("referer", "/some-page"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/some-page"));
	}

	@Test
	void unfollowUser_redirectsToReferer() throws Exception {
		when(userService.findByEmail("ana")).thenReturn(Optional.of(sampleUser));
		when(userService.findByName("juan")).thenReturn(Optional.of(new User("juan", "j@example.com", "pass")));
		mockMvc.perform(get("/unfollow/juan").principal(() -> "ana").header("referer", "/another-page"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/another-page"));
	}

	@Test
	void changePassword_success_redirectsWithMessage() throws Exception {
		when(userService.findByEmail("ana")).thenReturn(Optional.of(sampleUser));
		when(passwordEncoder.matches("oldPass", sampleUser.getPassword())).thenReturn(true);
		mockMvc.perform(post("/user/change-password").param("password", "oldPass")
					.param("password2", "newPass").param("password3", "newPass").principal(() -> "ana"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard-profile"));
	}

	@Test
	void changeImage_success_redirectsWithSuccess() throws Exception {
		MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "data".getBytes());
		when(userService.findByEmail("ana")).thenReturn(Optional.of(sampleUser));
		mockMvc.perform(multipart("/user/change-image").file(image).principal(() -> "ana"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard-profile"));
	}

	@Test
	void deleteUserImage_redirectsToDashboard() throws Exception {
		sampleUser.setImage("imagebytes".getBytes());
		when(userService.findByEmail("ana")).thenReturn(Optional.of(sampleUser));
		mockMvc.perform(post("/user/delete-image").principal(() -> "ana"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/dashboard-profile"));
	}

	@Test
	void deleteUser_redirectsToLogout() throws Exception {
		when(userService.findByEmail("ana")).thenReturn(Optional.of(sampleUser));
		mockMvc.perform(get("/user/delete").principal(() -> "ana"))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/logout"));
	}
	
	private void inject(Object target, String fieldName, Object toInject) {
		try {
			var field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(target, toInject);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
