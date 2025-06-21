package repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import model.Museum;

public interface MuseumRepository extends JpaRepository<Museum, Long> {
	@Query("SELECT a FROM Museum a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<Museum> searchByNameContainingIgnoreCase(@Param("search") String search, Pageable pageable);

	Page<Museum> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Museum> findByLocationIn(List<String> locations, Pageable pageable);

	Page<Museum> findByNameContainingIgnoreCaseAndLocationIn(String name, List<String> locations, Pageable pageable);

}
