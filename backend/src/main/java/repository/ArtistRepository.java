package repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import model.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
	@Query("SELECT a, AVG(w.averageRating) AS avgRating FROM Artwork w " + "JOIN w.artist a "
			+ "WHERE a.image IS NOT NULL " + "GROUP BY a.id ORDER BY avgRating DESC LIMIT 10")
	List<Artist> findTop10ArtistsByAverageArtworkRating();

	@Query("SELECT a FROM Artist a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<Artist> searchByNameContainingIgnoreCase(@Param("search") String search, Pageable pageable);

	List<Artist> findByNameContainingIgnoreCase(String name);

	List<Artist> findByNationalityIn(List<String> nationalities);

	List<Artist> findByNameContainingIgnoreCaseAndNationalityIn(String name, List<String> nationalities);

	Page<Artist> findByNameContainingIgnoreCaseAndNationalityIn(String search, List<String> nationalities,
			Pageable pageable);

	Page<Artist> findByNameContainingIgnoreCase(String search, Pageable pageable);

	Page<Artist> findByNationalityIn(List<String> nationalities, Pageable pageable);

}
