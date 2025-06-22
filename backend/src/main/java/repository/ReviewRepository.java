package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import model.Artwork;
import model.Review;
import model.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findByArtwork(Artwork artwork);

	Page<Review> findByArtworkId(Long artworkId, Pageable pageable);

	List<Review> findByUserId(Long userId);

	Page<Review> findByUserId(Long userId, Pageable pageable);

	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.artwork = :artwork")
	Double findAverageRatingByArtwork(@Param("artwork") Artwork artwork);

	boolean existsByUserAndArtwork(User user, Artwork artwork);

	Optional<Review> findByUserAndArtwork(User user, Artwork artwork);

	List<Review> findByUserIdIn(List<Long> similars);

}
