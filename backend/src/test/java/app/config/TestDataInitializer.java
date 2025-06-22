package app.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import model.Artist;
import model.Artwork;
import model.Museum;
import model.User;
import service.ArtistService;
import service.ArtworkService;
import service.MuseumService;
import service.UserService;

import org.springframework.transaction.annotation.Transactional;

@TestConfiguration
@Profile("test")
@RequiredArgsConstructor
public class TestDataInitializer {

	@Autowired
	private UserService userService;

	@Autowired
	private MuseumService museumService;

	@Autowired
	private ArtworkService artworkService;

	@Autowired
	private ArtistService artistService;

	@Value("${security.user}")
	private String userName;

	@Value("${security.encodedPassword}")
	private String encodedPassword;

	@PostConstruct
	@Transactional
	public void init() {
		Museum m1 = new Museum((long) 1, "Museo del Louvre", "París, Francia",
				"El Louvre es el museo de arte más grande del mundo; alberga obras maestras como la Mona Lisa, la Venus de Milo y muchas más.",
				1793, null, new ArrayList<>());
		museumService.save(m1);

		Artist daVinci = new Artist((long) 1, "Leonardo da Vinci", "Italiano", null, null, new Date(), null,
				"Leonardo da Vinci fue un polímata del Renacimiento italiano, reconocido por obras maestras como La Mona Lisa y La Última Cena.",
				new ArrayList<>());
		artistService.save(daVinci);

		artworkService.save(new Artwork((long) 1, "La Mona Lisa", daVinci, 1503,
				"Una de las obras más famosas y enigmáticas del mundo, célebre por la misteriosa sonrisa de su sujeto.",
				null, m1));

		User admin = new User(userName, userName + ".com", encodedPassword);
		admin.setRoles(List.of("USER", "ADMIN"));
		userService.save(admin);
		
		User user = new User("test", "test@arteum.com", encodedPassword);
		user.setRoles(List.of("USER"));
		userService.save(user);
	}
}
