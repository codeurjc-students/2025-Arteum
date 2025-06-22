package repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import model.Artwork;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

	Page<Artwork> findAll(Pageable pageable);

	Page<Artwork> findByArtistId(Long artistId, Pageable pageable);

	List<Artwork> findByArtistId(Long artistId);

	Page<Artwork> findByMuseumId(Long museumId, Pageable pageable);

	Page<Artwork> findByArtistIdAndTitleContainingIgnoreCase(Long artistId, String title, Pageable pageable);

	List<Artwork> findByArtistIdAndTitleContainingIgnoreCase(Long artistId, String title);

	Page<Artwork> findByMuseumIdAndTitleContainingIgnoreCase(Long museumId, String title, Pageable pageable);

	Page<Artwork> findByTitleContainingIgnoreCaseAndArtistIdIn(String title, List<Long> artistIds, Pageable pageable);

	Page<Artwork> findByArtistIdIn(List<Long> artistIds, Pageable pageable);

	@Query("SELECT a FROM Artwork a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<Artwork> searchByTitleContainingIgnoreCase(@Param("search") String search, Pageable pageable);

	@Query("SELECT a FROM User u JOIN u.favoriteArtworks a WHERE u.id = :userId")
	Page<Artwork> findFavoritesByUserId(@Param("userId") Long userId, Pageable pageable);
	
	Page<Artwork> findByUsersWhoFavorited_Id(Long userId, Pageable pageable);

	List<Artwork> findTop7ByOrderByAverageRatingDesc();

	List<Artwork> findTop12ByOrderByAverageRatingDesc();

	@Query(value = "SELECT * FROM artwork WHERE image IS NOT NULL ORDER BY RAND() LIMIT 7", nativeQuery = true)
	List<Artwork> find7RandomArtworks();

	List<Artwork> findByTitleContainingIgnoreCaseAndArtistIdIn(String title, List<Long> artistIds);

	@Query("SELECT a FROM Artwork a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%'))")
	List<Artwork> searchByTitleContainingIgnoreCase(@Param("search") String search);

	List<Artwork> findByArtistIdIn(List<Long> artistIds);

	List<Artwork> findAll();

	List<Artwork> findByArtistIdAndMuseumIdIn(Long artistId, List<Long> museumIds);
	
	Page<Artwork> findByArtistIdAndMuseumIdIn(Long artistId, List<Long> museumIds, Pageable pageable);

	Page<Artwork> findByArtistIdAndTitleContainingIgnoreCaseAndMuseumIdIn(Long artistId, String search,
			List<Long> museumIds, Pageable pageable);
	
	List<Artwork> findByArtistIdAndTitleContainingIgnoreCaseAndMuseumIdIn(Long artistId, String search,
			List<Long> museumIds);

	List<Artwork> findByMuseumId(Long museumId);

	List<Artwork> findByUsersWhoFavorited_Id(Long id);

	List<Artwork> findByUsersWhoFavorited_IdAndTitleContainingIgnoreCase(Long id, String search);

	Page<Artwork> findByUsersWhoFavorited_IdAndTitleContainingIgnoreCase(Long id, String search, Pageable pageable);

	Page<Artwork> findByUsersWhoFavorited_IdAndTitleContainingIgnoreCaseAndArtistIdIn(Long id, String search,
			List<Long> artistIds, Pageable pageable);

	Page<Artwork> findByUsersWhoFavorited_IdAndArtistIdIn(Long id, List<Long> artistIds, Pageable pageable);

	List<Artwork> findByUsersWhoFavorited_IdAndTitleContainingIgnoreCaseAndArtistIdIn(Long id, String search,
			List<Long> artistIds);

	List<Artwork> findByUsersWhoFavorited_IdAndArtistIdIn(Long id, List<Long> artistIds);
}
