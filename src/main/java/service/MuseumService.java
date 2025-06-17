package service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.experimental.Delegate;
import model.Museum;
import repository.MuseumRepository;

@Service
public class MuseumService {

	@Autowired
	@Delegate
	private MuseumRepository museumRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public Page<Museum> getMuseumsPage(Pageable pageable) {
		return museumRepository.findAll(pageable);
	}

	public Page<Museum> getMuseumsPage(String search, Pageable pageable) {
		if (search != null && !search.trim().isEmpty()) {
			return museumRepository.searchByNameContainingIgnoreCase(search.trim(), pageable);
		}
		return museumRepository.findAll(pageable);
	}

	public Page<Museum> getMuseumsPage(String search, List<String> locations, Pageable pageable) {
		if ((search == null || search.isBlank()) && (locations == null || locations.isEmpty())) {
			return museumRepository.findAll(pageable);
		}

		if (search != null && !search.isBlank() && locations != null && !locations.isEmpty()) {
			return museumRepository.findByNameContainingIgnoreCaseAndLocationIn(search.trim(), locations, pageable);
		}

		if (search != null && !search.isBlank()) {
			return museumRepository.findByNameContainingIgnoreCase(search.trim(), pageable);
		}

		return museumRepository.findByLocationIn(locations, pageable);
	}

	public void save(Museum museum) {
		museumRepository.save(museum);
	}

	public void delete(Museum museum) {
		museumRepository.delete(museum);
	}

}
