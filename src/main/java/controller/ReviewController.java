package controller;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import model.Artwork;
import model.Review;
import model.User;
import service.ArtworkService;
import service.ReviewService;
import service.UserService;

@Controller
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ArtworkService artworkService;

	@Autowired
	private UserService userService;

	@PostMapping("/review/new")
	public String newReview(HttpServletRequest request, RedirectAttributes redirectAttrs, Long id, Double rating,
			String comment) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			boolean id_exists = id != null;
			boolean rating_exists = rating > 0 && rating <= 5 && !rating.isInfinite() && !rating.isNaN();
			boolean comment_exists = comment != null && !comment.isEmpty() && !comment.isBlank();
			if (id_exists && rating_exists) {
				Optional<Artwork> optionalArtwork = artworkService.findById(id);
				if (optionalArtwork.isPresent()) {
					Artwork artwork = optionalArtwork.get();
					User user = userService.findByEmail(principal.getName())
							.orElseGet(() -> userService.findByName(principal.getName())
									.orElseThrow(() -> new UsernameNotFoundException("User not found")));
					boolean alreadyReviewed = reviewService.existsByUserAndArtwork(user, artwork);
					if (alreadyReviewed) {
						redirectAttrs.addFlashAttribute("error", "Ya has valorado esta obra.");
						return "redirect:/artworks/" + id;
					}
					Review review = new Review();
					review.setRating(rating);
					if (comment_exists) {
						review.setComment(comment);
					}
					review.setCreatedAt(new Date());
					review.setArtwork(artwork);
					review.setUser(user);
					reviewService.save(review);
					artworkService.updateAverageRating(artwork);
				} else {
					redirectAttrs.addFlashAttribute("error", "error");
				}
			} else {
				redirectAttrs.addFlashAttribute("error", "error");
			}
			return "redirect:/artworks/" + id;
		} else {
			return "redirect:/";
		}
	}

	@PostMapping("/review/edit")
	public String editReview(HttpServletRequest request, RedirectAttributes redirectAttrs, Long id, Double rating,
			String comment) {
		Principal principal = request.getUserPrincipal();

		if (principal == null) {
			return "redirect:/";
		}

		boolean idExists = id != null;
		boolean ratingIsValid = rating != null && rating > 0 && rating <= 5 && !rating.isNaN() && !rating.isInfinite();
		boolean commentExists = comment != null && !comment.isBlank();

		if (!idExists || !ratingIsValid) {
			redirectAttrs.addFlashAttribute("error", "Datos inválidos para editar la review.");
			return "redirect:/artworks/" + id;
		}

		Optional<Artwork> optionalArtwork = artworkService.findById(id);
		if (optionalArtwork.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "Obra no encontrada.");
			return "redirect:/artworks";
		}

		Artwork artwork = optionalArtwork.get();

		User user = userService.findByEmail(principal.getName())
				.orElseGet(() -> userService.findByName(principal.getName())
						.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado")));

		Optional<Review> optionalReview = reviewService.findByUserAndArtwork(user, artwork);
		if (optionalReview.isEmpty()) {
			redirectAttrs.addFlashAttribute("error", "No tienes una review para esta obra.");
			return "redirect:/artworks/" + id;
		}

		Review review = optionalReview.get();
		review.setRating(rating);
		review.setComment(commentExists ? comment : null);
		review.setCreatedAt(new Date());
		reviewService.save(review);

		artworkService.updateAverageRating(artwork);

		return "redirect:/artworks/" + id;
	}

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
		} else {
			model.addAttribute("logged", false);
		}
	}
}
