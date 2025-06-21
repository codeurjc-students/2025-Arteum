package service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.experimental.Delegate;
import model.Artwork;
import model.Review;
import model.User;
import repository.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	@Delegate
	private ReviewRepository repository;

	public Page<Review> getReviewsPage(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public Page<Review> getReviewsByArtworkPage(Long artworkId, Pageable pageable) {
		if (artworkId != null) {
			return repository.findByArtworkId(artworkId, pageable);
		} else {
			return null;
		}
	}
	
	public Map<Integer, Long> countStarsGivenByUser(Long userId) {
	    List<Review> reviews = repository.findByUserId(userId);
	    return reviews.stream()
	        .collect(Collectors.groupingBy(
	            r -> (int) Math.floor(r.getRating()),
	            Collectors.counting()
	        ));
	}
	
	public Map<Integer, Long> countStarsGivenByEveryone() {
	    List<Review> reviews = repository.findAll();
	    return reviews.stream()
	        .collect(Collectors.groupingBy(
	            r -> (int) Math.floor(r.getRating()),
	            Collectors.counting()
	        ));
	}


	public Optional<Review> findByUserAndArtwork(User user, Artwork artwork) {
		return repository.findByUserAndArtwork(user, artwork);
	}

	public Page<Review> getReviewsByUser(Long userId, Pageable pageable) {
		return repository.findByUserId(userId, pageable);
	}

	public List<Review> getReviewsByUser(Long userId) {
		return repository.findByUserId(userId);
	}

	public void save(Review review) {
		repository.save(review);
	}

	public void delete(Review review) {
		repository.delete(review);
	}

	public List<Review> findByUserIdIn(List<Long> similars) {
		return repository.findByUserIdIn(similars);
	}

}
