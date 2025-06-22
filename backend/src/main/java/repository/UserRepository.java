package repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);

	Optional<User> findByName(String username);

	Page<User> searchByNameContainingIgnoreCase(String trim, Pageable pageable);
	
	Page<User> findByRolesNotContaining(String role, Pageable pageable);

	Page<User> searchByNameContainingIgnoreCaseAndRolesNotContaining(String name, String role, Pageable pageable);

}
