package app.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import controller.ReviewController;
import model.Artwork;
import model.Review;
import model.User;
import service.ArtworkService;
import service.ReviewService;
import service.UserService;

public class ReviewControllerTest {

    private MockMvc mockMvc;
    private ReviewService reviewService;
    private ArtworkService artworkService;
    private UserService userService;

    private Artwork artwork;
    private User user;

    @BeforeEach
    void setup() {
        reviewService = mock(ReviewService.class);
        artworkService = mock(ArtworkService.class);
        userService = mock(UserService.class);

        ReviewController controller = new ReviewController();
        inject(controller, "reviewService", reviewService);
        inject(controller, "artworkService", artworkService);
        inject(controller, "userService", userService);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();

        user = new User();
        user.setId(1L);
        user.setName("ana");
        user.setEmail("ana@example.com");

        artwork = new Artwork();
        artwork.setId(99L);
        artwork.setTitle("Test Artwork");
    }

    @Test
    void newReview_withValidData_savesReviewAndRedirects() throws Exception {
        when(artworkService.findById(99L)).thenReturn(Optional.of(artwork));
        when(userService.findByEmail("ana")).thenReturn(Optional.of(user));
        when(reviewService.existsByUserAndArtwork(user, artwork)).thenReturn(false);

        mockMvc.perform(post("/review/new")
                .principal(() -> "ana")
                .param("id", "99")
                .param("rating", "4.5")
                .param("comment", "Great artwork!"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/artworks/99"));

        verify(reviewService).save(any(Review.class));
        verify(artworkService).updateAverageRating(artwork);
    }

    @Test
    void editReview_withValidData_editsAndRedirects() throws Exception {
        Review review = new Review();
        review.setId(5L);
        review.setUser(user);
        review.setArtwork(artwork);
        review.setRating(3.0);

        when(artworkService.findById(99L)).thenReturn(Optional.of(artwork));
        when(userService.findByEmail("ana")).thenReturn(Optional.of(user));
        when(reviewService.findByUserAndArtwork(user, artwork)).thenReturn(Optional.of(review));

        mockMvc.perform(post("/review/edit")
                .principal(() -> "ana")
                .param("id", "99")
                .param("rating", "4.0")
                .param("comment", "Updated comment"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/artworks/99"));

        verify(reviewService).save(any(Review.class));
        verify(artworkService).updateAverageRating(artwork);
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