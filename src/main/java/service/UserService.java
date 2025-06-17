package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.experimental.Delegate;
import model.User;
import repository.UserRepository;

@Service
public class UserService {

	@Autowired
	@Delegate
	private UserRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	public void save(User user) {
		repository.save(user);
	}

	public void delete(User user) {
		repository.delete(user);
	}

	public Optional<User> findByEmail(String email) {
		return repository.findByEmail(email);
	}

	public Optional<User> findByName(String email) {
		return repository.findByName(email);
	}

	public Page<User> getUsersPage(String search, Pageable pageable) {
		if (search != null && !search.trim().isEmpty()) {
			return repository.searchByNameContainingIgnoreCaseAndRolesNotContaining(search.trim(), "ADMIN", pageable);
		}
		return repository.findByRolesNotContaining("ADMIN", pageable);
	}

	public boolean artworkIsFavorite(String name, Long id) {
		User user = this.findByEmail(name).orElseGet(
				() -> this.findByName(name).orElseThrow(() -> new UsernameNotFoundException("User not found")));
		return user.getFavoriteArtworks().stream().anyMatch(artwork -> artwork.getId().equals(id));
	}

	public Page<User> getFollowersPage(Long userId, Pageable pageable) {
		User user = repository.findById(userId).orElseThrow();
		List<User> followersList = new ArrayList<>(user.getFollowers());
		followersList.removeIf(userR -> userR.getRoles() != null && userR.getRoles().contains("ADMIN"));
		if (pageable.getSort().isSorted()) {
			for (Sort.Order order : pageable.getSort()) {
				Comparator<User> comparator = switch (order.getProperty()) {
				case "createdAt" -> Comparator.comparing(User::getCreatedAt);
				default -> Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER);
				};

				if (comparator != null) {
					if (order.getDirection() == Sort.Direction.DESC) {
						comparator = comparator.reversed();
					}
					followersList.sort(comparator);
				}
			}
		}
		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), followersList.size());
		List<User> pageContent = followersList.subList(start, end);
		return new PageImpl<>(pageContent, pageable, followersList.size());
	}

	public Page<User> getFollowingPage(Long userId, Pageable pageable) {
		User user = repository.findById(userId).orElseThrow();
		List<User> followingList = new ArrayList<>(user.getFollowing());
		followingList.removeIf(userR -> userR.getRoles() != null && userR.getRoles().contains("ADMIN"));
		if (pageable.getSort().isSorted()) {
			for (Sort.Order order : pageable.getSort()) {
				Comparator<User> comparator = switch (order.getProperty()) {
				case "createdAt" -> Comparator.comparing(User::getCreatedAt);
				default -> Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER);
				};

				if (comparator != null) {
					if (order.getDirection() == Sort.Direction.DESC) {
						comparator = comparator.reversed();
					}
					followingList.sort(comparator);
				}
			}
		}
		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), followingList.size());
		List<User> pageContent = followingList.subList(start, end);
		return new PageImpl<>(pageContent, pageable, followingList.size());
	}

	public void followUser(Long followerId, Long followedId) {
		User follower = repository.findById(followerId).orElseThrow();
		User followed = repository.findById(followedId).orElseThrow();

		if (follower.getRoles() != null && follower.getRoles().contains("ADMIN")
				|| followed.getRoles() != null && followed.getRoles().contains("ADMIN")) {
			throw new IllegalStateException("Error");
		}
		if (!follower.getFollowing().contains(followed)) {
			follower.getFollowing().add(followed);
			repository.save(follower);
		}
	}

	public void unfollowUser(Long followerId, Long followedId) {
		User follower = repository.findById(followerId).orElseThrow();
		User followed = repository.findById(followedId).orElseThrow();
		if (follower.getRoles() != null && follower.getRoles().contains("ADMIN")
				|| followed.getRoles() != null && followed.getRoles().contains("ADMIN")) {
			throw new IllegalStateException("Error");
		}
		if (follower.getFollowing().contains(followed)) {
			follower.getFollowing().remove(followed);
			repository.save(follower);
		}
	}

	public boolean isFollowing(Long followerId, Long followedId) {
		User follower = repository.findById(followerId).orElseThrow();
		User followed = repository.findById(followedId).orElseThrow();
		return follower.getFollowing().contains(followed);
	}

}
