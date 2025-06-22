package service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import model.Artist;
import model.Artwork;
import model.Museum;
import model.Review;
import model.User;

@Profile("!test")
@Service
public class DatabaseInitializerService {

	@Autowired
	private UserService userService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private MuseumService museumService;

	@Autowired
	private ArtworkService artworkService;

	@Autowired
	private ArtistService artistService;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${security.user}")
	private String userName;

	@Value("${security.encodedPassword}")
	private String encodedPassword;
    
	
	@Transactional
    @PostConstruct
	public void init() throws IOException, URISyntaxException {
		if (museumService.count() == 0) {
			// =========================
			// Ingresar 25 Museos Famosos
			// =========================

			// 1. Museo del Louvre, París (Francia)
			Museum m1 = new Museum(null, "Museo del Louvre", "París, Francia",
					"El Louvre es el museo de arte más grande del mundo; alberga obras maestras como la Mona Lisa, la Venus de Milo y muchas más.",
					1793, null, new ArrayList<>());
			museumService.save(m1);

			// 2. Museos Vaticanos, Ciudad del Vaticano (Italia)
			Museum m2 = new Museum(null, "Museos Vaticanos", "Ciudad del Vaticano, Italia",
					"Una de las colecciones de arte renacentista más impresionantes, que incluye la Capilla Sixtina y numerosas obras maestras de Michelangelo y Rafael.",
					1506, null, new ArrayList<>());
			museumService.save(m2);

			// 3. The Metropolitan Museum of Art, Nueva York (EE.UU.)
			Museum m3 = new Museum(null, "The Metropolitan Museum of Art", "Nueva York, EE.UU.",
					"El Met posee una de las colecciones de arte más completas del mundo, abarcando desde el arte egipcio hasta el contemporáneo.",
					1870, null, new ArrayList<>());
			museumService.save(m3);

			// 4. British Museum, Londres (Reino Unido)
			Museum m4 = new Museum(null, "British Museum", "Londres, Reino Unido",
					"Famoso por su colección de antigüedades, incluyendo la Piedra de Rosetta y los Mármoles Elgin.",
					1753, null, new ArrayList<>());
			museumService.save(m4);

			// 5. Tate Modern, Londres (Reino Unido)
			Museum m5 = new Museum(null, "Tate Modern", "Londres, Reino Unido",
					"Un importante museo de arte moderno y contemporáneo, ubicado en una antigua central eléctrica.",
					2000, null, new ArrayList<>());
			museumService.save(m5);

			// 6. National Gallery, Londres (Reino Unido)
			Museum m6 = new Museum(null, "National Gallery", "Londres, Reino Unido",
					"Exhibe una rica colección de pintura europea desde el siglo XIII hasta el XIX, con obras de Botticelli, Rembrandt y Van Eyck.",
					1824, null, new ArrayList<>());
			museumService.save(m6);

			// 7. Victoria and Albert Museum, Londres (Reino Unido)
			Museum m7 = new Museum(null, "Victoria and Albert Museum", "Londres, Reino Unido",
					"El V&A es el museo de arte y diseño más grande del mundo, con colecciones que incluyen moda, escultura, y arte decorativo.",
					1852, null, new ArrayList<>());
			museumService.save(m7);

			// 8. Rijksmuseum, Ámsterdam (Países Bajos)
			Museum m8 = new Museum(null, "Rijksmuseum", "Ámsterdam, Países Bajos",
					"Destacado por su colección de arte neerlandés, con obras maestras de Rembrandt, Vermeer y otros artistas del Siglo de Oro holandés.",
					1800, null, new ArrayList<>());
			museumService.save(m8);

			// 9. Van Gogh Museum, Ámsterdam (Países Bajos)
			Museum m9 = new Museum(null, "Van Gogh Museum", "Ámsterdam, Países Bajos",
					"Dedicado al legado de Vincent van Gogh, alberga la mayor colección de sus pinturas y dibujos.",
					1973, null, new ArrayList<>());
			museumService.save(m9);

			// 10. Stedelijk Museum, Ámsterdam (Países Bajos)
			Museum m10 = new Museum(null, "Stedelijk Museum", "Ámsterdam, Países Bajos",
					"Especializado en arte moderno y contemporáneo, con exposiciones de diseño, fotografía y arte experimental.",
					1895, null, new ArrayList<>());
			museumService.save(m10);

			// 11. Hermitage Museum, San Petersburgo (Rusia)
			Museum m11 = new Museum(null, "Hermitage Museum", "San Petersburgo, Rusia",
					"Uno de los museos más grandes y antiguos del mundo, con una vasta colección de arte y antigüedades de todo el orbe.",
					1764, null, new ArrayList<>());
			museumService.save(m11);

			// 12. Tretyakov Gallery, Moscú (Rusia)
			Museum m12 = new Museum(null, "Tretyakov Gallery", "Moscú, Rusia",
					"La principal galería de arte ruso, con obras que van desde el arte medieval hasta el moderno.",
					1856, null, new ArrayList<>());
			museumService.save(m12);

			// 13. Museo del Prado, Madrid (España)
			Museum m13 = new Museum(null, "Museo del Prado", "Madrid, España",
					"Con una de las colecciones más importantes del mundo, el Prado exhibe obras de Velázquez, Goya, El Bosco y otros maestros europeos.",
					1819, null, new ArrayList<>());
			museumService.save(m13);

			// 14. Museo Reina Sofía, Madrid (España)
			Museum m14 = new Museum(null, "Museo Reina Sofía", "Madrid, España",
					"Especializado en arte moderno español, es famoso por albergar el Guernica de Picasso.", 1992, null,
					new ArrayList<>());
			museumService.save(m14);

			// 15. Uffizi Gallery, Florencia (Italia)
			Museum m15 = new Museum(null, "Uffizi Gallery", "Florencia, Italia",
					"Una de las galerías de arte más antiguas y destacadas del mundo, con una extensa colección de obras renacentistas.",
					1581, null, new ArrayList<>());
			museumService.save(m15);

			// 16. Accademia Gallery, Florencia (Italia)
			Museum m16 = new Museum(null, "Accademia Gallery", "Florencia, Italia",
					"Famosa por el David de Miguel Ángel, es uno de los principales museos de escultura renacentista.",
					1784, null, new ArrayList<>());
			museumService.save(m16);

			// 17. Museum of Modern Art (MoMA), Nueva York (EE.UU.)
			Museum m17 = new Museum(null, "Museum of Modern Art (MoMA)", "Nueva York, EE.UU.",
					"Un referente mundial del arte moderno y contemporáneo, conocido por su colección de pintura, escultura y cine.",
					1929, null, new ArrayList<>());
			museumService.save(m17);

			// 18. Solomon R. Guggenheim Museum, Nueva York (EE.UU.)
			Museum m18 = new Museum(null, "Solomon R. Guggenheim Museum", "Nueva York, EE.UU.",
					"Famoso por su arquitectura diseñada por Frank Lloyd Wright y su innovadora colección de arte contemporáneo.",
					1959, null, new ArrayList<>());
			museumService.save(m18);

			// 19. National Gallery of Art, Washington DC (EE.UU.)
			Museum m19 = new Museum(null, "National Gallery of Art", "Washington DC, EE.UU.",
					"Una institución que alberga una impresionante colección de obras maestras europeas y americanas.",
					1941, null, new ArrayList<>());
			museumService.save(m19);

			// 20. Los Angeles County Museum of Art (LACMA), Los Ángeles (EE.UU.)
			Museum m20 = new Museum(null, "Los Angeles County Museum of Art (LACMA)", "Los Ángeles, EE.UU.",
					"El museo de arte más grande del oeste de Estados Unidos, con colecciones que abarcan desde el arte antiguo hasta el contemporáneo.",
					1965, null, new ArrayList<>());
			museumService.save(m20);

			// 21. San Francisco Museum of Modern Art (SFMOMA), San Francisco (EE.UU.)
			Museum m21 = new Museum(null, "San Francisco Museum of Modern Art (SFMOMA)", "San Francisco, EE.UU.",
					"Reconocido por su innovadora exposición de arte moderno y contemporáneo en un edificio emblemático.",
					1935, null, new ArrayList<>());
			museumService.save(m21);

			// 22. Art Institute of Chicago, Chicago (EE.UU.)
			Museum m22 = new Museum(null, "Art Institute of Chicago", "Chicago, EE.UU.",
					"Uno de los museos de arte más prestigiosos de Estados Unidos, con una colección que va desde el arte antiguo hasta el moderno.",
					1879, null, new ArrayList<>());
			museumService.save(m22);

			// 23. National Museum of Korea, Seúl (Corea del Sur)
			Museum m23 = new Museum(null, "National Museum of Korea", "Seúl, Corea del Sur",
					"El museo más grande de Corea, que exhibe una extensa colección de arte y artefactos de la historia coreana.",
					2002, null, new ArrayList<>());
			museumService.save(m23);

			// 24. Palace Museum (Ciudad Prohibida), Beijing (China)
			Museum m24 = new Museum(null, "Palace Museum (Ciudad Prohibida)", "Beijing, China",
					"La Ciudad Prohibida es un palacio imperial convertido en museo, con una de las colecciones de arte chino más amplias del mundo.",
					1420, null, new ArrayList<>());
			museumService.save(m24);

			// 25. Guggenheim Museum Bilbao, Bilbao (España)
			Museum m25 = new Museum(null, "Guggenheim Museum Bilbao", "Bilbao, España",
					"Conocido por su innovador diseño arquitectónico de Frank Gehry, es un icono del arte contemporáneo y la regeneración urbana.",
					1997, null, new ArrayList<>());
			museumService.save(m25);

			// ====================================
			// Ingresar Artistas necesarios
			// ====================================
			Resource resource = resourceLoader.getResource("classpath:assets/img/artist/descarga (14).jpg");
			byte[] imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist daVinci = new Artist(null, "Leonardo da Vinci", "Italiano", parseDate("1452-04-15"),
					parseDate("1519-05-02"), new Date(), imageBytes,
					"Leonardo da Vinci fue un polímata del Renacimiento italiano, reconocido por obras maestras como La Mona Lisa y La Última Cena.",
					new ArrayList<>());
			artistService.save(daVinci);

			Artist alejandro_de_Antioquia = new Artist(null, "Alejandro de Antioquía", null, null, null, new Date(),
					null, "Artista de la antigüedad con información escasa.", new ArrayList<>());
			artistService.save(alejandro_de_Antioquia);

			Artist anonimo = new Artist(null, "Anónimo", null, null, null, new Date(), null,
					"Sin datos precisos; la autoría es desconocida.", new ArrayList<>());
			artistService.save(anonimo);
			resource = resourceLoader
					.getResource("classpath:assets/img/artist/Jacques-Louis_David_-_Self-portrait_-_1748-1825.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist jacques_Louis_David = new Artist(null, "Jacques-Louis David", "Francés", parseDate("1748-08-30"),
					parseDate("1825-12-29"), new Date(), imageBytes,
					"Pintor neoclásico francés, figura clave en la Revolución y en la historia del arte.",
					new ArrayList<>());
			artistService.save(jacques_Louis_David);
			resource = resourceLoader.getResource(
					"classpath:assets/img/artist/Theodore_Gericault_-_Portrait_of_a_Kleptomaniac_-_1791-1824.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist théodore_Géricault = new Artist(null, "Théodore Géricault", "Francés", parseDate("1791-09-26"),
					parseDate("1824-01-26"), new Date(), imageBytes,
					"Pintor francés, autor de 'La balsa de la Medusa' y representante del romanticismo.",
					new ArrayList<>());
			artistService.save(théodore_Géricault);
			resource = resourceLoader.getResource(
					"classpath:assets/img/artist/Eugene_Delacroix_-_Portrait_de_lartiste_ca.1837_-_1798-1863_-_Louvre_-_Paris.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist delacroix = new Artist(null, "Eugène Delacroix", "Francés", parseDate("1798-04-26"),
					parseDate("1863-08-13"), new Date(), imageBytes,
					"Principal exponente del romanticismo francés, conocido por 'La Libertad guiando al pueblo'.",
					new ArrayList<>());
			artistService.save(delacroix);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Paolo-Veronese-800x600.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist veronese = new Artist(null, "Paolo Veronese", "Italiano", parseDate("1528-01-01"),
					parseDate("1588-01-01"), new Date(), imageBytes,
					"Pintor veneciano renombrado por sus escenas religiosas y mitológicas, con un estilo opulento y decorativo.",
					new ArrayList<>());
			artistService.save(veronese);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Michelangelo_Buonarroti_-_1545.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist miguelangel = new Artist(null, "Miguel Ángel", "Italiano", parseDate("1475-03-06"),
					parseDate("1564-02-18"), new Date(), imageBytes,
					"Escultor, pintor y arquitecto del Alto Renacimiento, autor de obras icónicas como la Capilla Sixtina y el David.",
					new ArrayList<>());
			artistService.save(miguelangel);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Raffaello_Sanzio_-_1483-1520.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist rafael = new Artist(null, "Rafael", "Italiano", parseDate("1483-04-06"), parseDate("1520-04-06"),
					new Date(), imageBytes,
					"Pintor y arquitecto del Renacimiento italiano, famoso por su equilibrio y armonía en obras como 'La Escuela de Atenas'.",
					new ArrayList<>());
			artistService.save(rafael);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Fra_Angelico_-_1395-1455.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist fraAngelico = new Artist(null, "Fra Angelico", "Italiano", parseDate("1395-01-01"),
					parseDate("1455-01-18"), new Date(), imageBytes,
					"Monje y pintor del primer Renacimiento, reconocido por la espiritualidad y belleza de sus frescos.",
					new ArrayList<>());
			artistService.save(fraAngelico);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Rogier_van_der_Weyden_-_1399-1464.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist rogier_van_der_Weyden = new Artist(null, "Rogier van der Weyden", "Holandés",
					parseDate("1400-01-01"), parseDate("1464-01-01"), new Date(), imageBytes,
					"Pintor flamenco del siglo XV, conocido por su estilo emotivo y detallado.", new ArrayList<>());
			artistService.save(rogier_van_der_Weyden);
			resource = resourceLoader
					.getResource("classpath:assets/img/artist/Ottavio_Leoni_-_Portrait_of_Caravaggio_small.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist caravaggio = new Artist(null, "Caravaggio", "Italiano", parseDate("1571-09-29"),
					parseDate("1610-07-18"), new Date(), imageBytes,
					"Innovador del claroscuro, revolucionó la pintura baroque con su dramatismo realista.",
					new ArrayList<>());
			artistService.save(caravaggio);
			resource = resourceLoader.getResource(
					"classpath:assets/img/artist/Pieter_Bruegel_the_Elder_-_The_Painter_and_the_Buyer_1565_-_1526-1569.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist pieterBruegelelViejo = new Artist(null, "Pieter Bruegel el Viejo", "Holandés",
					parseDate("1525-01-01"), parseDate("1569-01-01"), new Date(), imageBytes,
					"Pintor neerlandés célebre por sus detalladas escenas campesinas y paisajes.", new ArrayList<>());
			artistService.save(pieterBruegelelViejo);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Lippi_z13.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist fraFilippoLippi = new Artist(null, "Fra Filippo Lippi", "Italiano", parseDate("1406-01-01"),
					parseDate("1469-01-01"), new Date(), imageBytes,
					"Pintor florentino del Renacimiento, conocido por sus delicadas composiciones religiosas.",
					new ArrayList<>());
			artistService.save(fraFilippoLippi);
			resource = resourceLoader.getResource("classpath:assets/img/artist/descarga (12).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist vanGogh = new Artist(null, "Vincent van Gogh", "Holandés", parseDate("1853-03-30"),
					parseDate("1890-07-29"), new Date(), imageBytes,
					"Pintor postimpresionista holandés, cuya obra influyó profundamente en el arte del siglo XX.",
					new ArrayList<>());
			artistService.save(vanGogh);
			resource = resourceLoader.getResource("classpath:assets/img/artist/800px-emanuel-leutze.jpg!Portrait.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist emanuelLeutze = new Artist(null, "Emanuel Leutze", "Alemán", parseDate("1816-06-19"),
					parseDate("1868-11-19"), new Date(), imageBytes,
					"Pintor germano-estadounidense, recordado por obras históricas como 'La embajada de Washington cruzando el Delaware'.",
					new ArrayList<>());
			artistService.save(emanuelLeutze);

			Artist variosautores = new Artist(null, "Varios autores", null, null, null, new Date(), null,
					"Entrada colectiva para obras realizadas por más de un autor.", new ArrayList<>());
			artistService.save(variosautores);
			resource = resourceLoader.getResource("classpath:assets/img/artist/damien-hirst-the-currency.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist damienHirst = new Artist(null, "Damien Hirst", "Británico", parseDate("1965-06-07"), null,
					new Date(), imageBytes,
					"Artista contemporáneo británico, figura central del grupo Young British Artists, conocido por sus instalaciones y obras conceptuales.",
					new ArrayList<>());
			artistService.save(damienHirst);

			Artist traceyEmin = new Artist(null, "Tracey Emin", "Británica", parseDate("1963-07-03"), null, new Date(),
					null, "Artista contemporánea británica, reconocida por su arte confesional y obras provocativas.",
					new ArrayList<>());
			artistService.save(traceyEmin);

			Artist a24 = new Artist(null, "Félix González-Torres", "Cubano", parseDate("1957-01-26"),
					parseDate("1996-03-03"), new Date(), null,
					"Artista cuya obra aborda temas de amor, pérdida y memoria a través de instalaciones minimalistas.",
					new ArrayList<>());
			artistService.save(a24);
			resource = resourceLoader.getResource("classpath:assets/img/artist/licensed-image (14).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a25 = new Artist(null, "Andy Warhol", "Estadounidense", parseDate("1928-08-06"),
					parseDate("1987-02-22"), new Date(), imageBytes,
					"Figura representativa del pop art, reconocido por sus serigrafías y su mirada iconográfica de la cultura de masas.",
					new ArrayList<>());
			artistService.save(a25);

			Artist a26 = new Artist(null, "Olafur Eliasson", "Danés", parseDate("1967-02-05"), null,
					new Date(), null,
					"Instalacionista y escultor contemporáneo, crea obras que exploran la percepción, la luz y la naturaleza.",
					new ArrayList<>());
			artistService.save(a26);
			resource = resourceLoader.getResource("classpath:assets/img/artist/duchamp.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a27 = new Artist(null, "Marcel Duchamp", "Francés", parseDate("1887-07-28"),
					parseDate("1968-10-02"), new Date(), imageBytes,
					"Revolucionó el arte del siglo XX desafiando las convenciones establecidas, con obras emblemáticas como 'La fuente'.",
					new ArrayList<>());
			artistService.save(a27);

			Artist a28 = new Artist(null, "David Smith", "Estadounidense", parseDate("1906-03-09"),
					parseDate("1965-05-23"), new Date(), null,
					"Escultor estadounidense conocido por sus obras en metal y su exploración del volumen en el espacio.",
					new ArrayList<>());
			artistService.save(a28);

			Artist a29 = new Artist(null, "Artista Contemporáneo", null, null, null, new Date(), null,
					"Registro genérico de un artista actual sin datos específicos disponibles.", new ArrayList<>());
			artistService.save(a29);
			resource = resourceLoader.getResource("classpath:assets/img/artist/William_Turner_-_selfportrait.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a30 = new Artist(null, "J.M.W. Turner", "Británico", parseDate("1775-04-23"),
					parseDate("1851-12-19"), new Date(), imageBytes,
					"Pintor inglés, reconocido por sus paisajes atmosféricos e innovadoras representaciones de la luz.",
					new ArrayList<>());
			artistService.save(a30);
			resource = resourceLoader
					.getResource("classpath:assets/img/artist/Rembrandt_van_Rijn_-_Self-Portrait_-_1659.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a31 = new Artist(null, "Rembrandt", "Holandés", parseDate("1606-07-15"), parseDate("1669-10-04"),
					new Date(), imageBytes,
					"Uno de los mayores maestros de la pintura barroca, famoso por su uso del claroscuro y autorretratos.",
					new ArrayList<>());
			artistService.save(a31);

			Artist a32 = new Artist(null, "George Stubbs", "Británico", parseDate("1724-04-04"),
					parseDate("1806-08-25"), new Date(), null,
					"Pintor británico, célebre por sus representaciones ecuestres y de animales.", new ArrayList<>());
			artistService.save(a32);

			Artist a33 = new Artist(null, "Georges Seurat", "Francés", parseDate("1859-12-02"), parseDate("1891-03-29"),
					new Date(), null, "Pintor postimpresionista, pionero de la técnica del puntillismo.",
					new ArrayList<>());
			artistService.save(a33);
			resource = resourceLoader.getResource(
					"classpath:assets/img/artist/Hans_Holbein_the_Younger_-_Self-portrait_-_1497-1543.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a34 = new Artist(null, "Hans Holbein the Younger", "Alemán-suizo", parseDate("1497-07-01"),
					parseDate("1543-07-29"), new Date(), imageBytes,
					"Pintor renombrado por sus retratos precisos y elegantes, activo en la corte de Enrique VIII.",
					new ArrayList<>());
			artistService.save(a34);
			resource = resourceLoader.getResource(
					"classpath:assets/img/artist/James_Abbot_McNeill_Whistler_-_Selbstportrat_-_1834-1903.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a35 = new Artist(null, "James McNeill Whistler", "Estadounidense", parseDate("1834-07-10"),
					parseDate("1903-12-17"), new Date(), imageBytes,
					"Conocido como James Abbott McNeill Whistler, fue un pintor y grabador que ejerció influencia en el arte moderno.",
					new ArrayList<>());
			artistService.save(a35);

			Artist a36 = new Artist(null, "John Everett Millais", "Británico", parseDate("1829-06-08"),
					parseDate("1896-08-18"), new Date(), null,
					"Uno de los fundadores del movimiento prerrafaelita, famoso por sus detallados cuadros históricos y naturales.",
					new ArrayList<>());
			artistService.save(a36);

			Artist a37 = new Artist(null, "John William Waterhouse", "Británico", parseDate("1849-04-28"),
					parseDate("1917-09-16"), new Date(), null,
					"Pintor británico conocido por sus escenas mitológicas y literarias, con un estilo cargado de romanticismo.",
					new ArrayList<>());
			artistService.save(a37);
			resource = resourceLoader.getResource(
					"classpath:assets/img/artist/Johannes_Vermeer_-_De_koppelaarster_-_1656_-_Oil_on_canvas_-_143_x_130_cm.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a38 = new Artist(null, "Johannes Vermeer", "Holandés", parseDate("1632-10-31"),
					parseDate("1675-12-15"), new Date(), imageBytes,
					"Maestro del claroscuro y de la representación íntima de escenas domésticas, famoso por 'La joven de la perla'.",
					new ArrayList<>());
			artistService.save(a38);

			Artist a39 = new Artist(null, "Adriaen van Ostade", "Holandés", parseDate("1610-01-01"),
					parseDate("1685-12-01"), new Date(), null,
					"Pintor neerlandés reconocido por sus representaciones de escenas campesinas y de la vida cotidiana.",
					new ArrayList<>());
			artistService.save(a39);

			Artist a40 = new Artist(null, "Jacob van Ruisdael", "Holandés", parseDate("1628-01-01"),
					parseDate("1682-01-01"), new Date(), null,
					"Uno de los más grandes paisajistas de la pintura holandesa del siglo XVII.", new ArrayList<>());
			artistService.save(a40);

			Artist a41 = new Artist(null, "Jan Steen", "Holandés", parseDate("1626-01-01"), parseDate("1679-01-01"),
					new Date(), null,
					"Pintor del siglo XVII conocido por sus composiciones humorísticas y caóticas de escenas domésticas.",
					new ArrayList<>());
			artistService.save(a41);

			Artist a42 = new Artist(null, "Rachel Ruysch", "Holandés", parseDate("1664-01-01"), parseDate("1750-01-01"),
					new Date(), null,
					"Renombrada pintora de flores y naturaleza muerta, sobresaliente en el ámbito del arte neerlandés.",
					new ArrayList<>());
			artistService.save(a42);
			resource = resourceLoader
					.getResource("classpath:assets/img/artist/Piet_Mondrian_-_Self-portrait_-_1900.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a43 = new Artist(null, "Piet Mondrian", "Holandés", parseDate("1872-03-07"), parseDate("1944-02-01"),
					new Date(), imageBytes,
					"Pionero del arte abstracto, conocido por su reducción de formas a líneas y colores primarios.",
					new ArrayList<>());
			artistService.save(a43);

			Artist a44 = new Artist(null, "Josef Albers", "Alemán", parseDate("1888-03-19"),
					parseDate("1976-03-25"), new Date(), null,
					"Artista y teórico del color, que influyó en la educación del diseño y el arte moderno.",
					new ArrayList<>());
			artistService.save(a44);

			Artist a45 = new Artist(null, "Jean Arp", "Franco-alemán", parseDate("1886-09-16"),
					parseDate("1966-03-07"), new Date(), null,
					"Escultor y pintor, miembro influyente del movimiento dadaísta y surrealista.", new ArrayList<>());
			artistService.save(a45);

			Artist a46 = new Artist(null, "Correggio", "Italiano", parseDate("1489-01-01"), parseDate("1534-01-01"),
					new Date(), null,
					"Pintor del Alto Renacimiento, reconocido por su innovador uso del claroscuro y la composición.",
					new ArrayList<>());
			artistService.save(a46);
			resource = resourceLoader.getResource(
					"classpath:assets/img/artist/Titian_-_Self-portrait_-_1546-1547_-_Oil_on_canvas_-_96_x_75_cm_-_Gemaldegalerie_-_Berlin.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a47 = new Artist(null, "Tiziano", "Italiano", parseDate("1488-01-01"), parseDate("1576-08-27"),
					new Date(), imageBytes,
					"Pintor veneciano, influyente durante el Renacimiento, conocido por sus vibrantes colores y retratos magistrales.",
					new ArrayList<>());
			artistService.save(a47);

			Artist a48 = new Artist(null, "Giovanni Battista Tiepolo", "Italiano", parseDate("1696-03-17"),
					parseDate("1770-03-27"), new Date(), null,
					"Pintor y dibujante, destacado por sus frescos y composiciones monumentales, especialmente en iglesias y palacios.",
					new ArrayList<>());
			artistService.save(a48);

			Artist a49 = new Artist(null, "Alexander Ivanov", "Ruso", parseDate("1806-12-09"), parseDate("1858-11-20"),
					new Date(), null,
					"Pintor ruso, reconocido por sus obras de temática histórica y religiosa con gran realismo.",
					new ArrayList<>());
			artistService.save(a49);

			Artist a50 = new Artist(null, "Vasily Tropinin", "Ruso", parseDate("1776-01-01"), parseDate("1857-01-01"),
					new Date(), null,
					"Pintor ruso cuyos retratos y escenas cotidianas reflejan la sensibilidad del siglo XIX.",
					new ArrayList<>());
			artistService.save(a50);

			Artist a51 = new Artist(null, "Ilya Repin", "Ruso", parseDate("1844-08-05"), parseDate("1930-09-29"),
					new Date(), null,
					"Uno de los más importantes pintores realistas rusos, notable por sus representaciones históricas y sociales.",
					new ArrayList<>());
			artistService.save(a51);

			Artist a52 = new Artist(null, "Khariton Platonov", "Ruso", null, null, new Date(), null,
					"Escasa información disponible; reconocido en su época pero con datos biográficos poco precisos.",
					new ArrayList<>());
			artistService.save(a52);

			Artist a53 = new Artist(null, "Alexey Savrasov", "Ruso", parseDate("1830-01-01"), parseDate("1897-03-26"),
					new Date(), null,
					"Pintor ruso, recordado por sus paisajes líricos y por capturar la esencia del campo ruso.",
					new ArrayList<>());
			artistService.save(a53);

			Artist a54 = new Artist(null, "Ivan Aivazovsky", "Ruso", parseDate("1817-07-29"),
					parseDate("1900-05-05"), new Date(), null,
					"Famoso por sus majestuosos paisajes marinos, es uno de los más grandes pintores de la temática naval.",
					new ArrayList<>());
			artistService.save(a54);

			Artist a55 = new Artist(null, "Isaac Levitan", "Ruso", parseDate("1860-07-10"), parseDate("1900-07-23"),
					new Date(), null,
					"Pintor ruso cuyos paisajes expresan la melancolía y la belleza del entorno natural.",
					new ArrayList<>());
			artistService.save(a55);
			resource = resourceLoader
					.getResource("classpath:assets/img/artist/Diego_Velazquez_-_Self-portrait_-_1645.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a57 = new Artist(null, "Diego Velázquez", "Español", parseDate("1599-06-06"),
					parseDate("1660-08-06"), new Date(), imageBytes,
					"Pintor del Siglo de Oro español, reconocido por sus retratos y su maestría en la luz y la composición.",
					new ArrayList<>());
			artistService.save(a57);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Hieronymus_Bosch_-_1533-1578.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a58 = new Artist(null, "El Bosco", "Holandés", parseDate("1450-01-01"), parseDate("1516-08-09"),
					new Date(), imageBytes,
					"Pintor enigmático, conocido por sus complejas y fantásticas visiones religiosas y morales.",
					new ArrayList<>());
			artistService.save(a58);
			resource = resourceLoader
					.getResource("classpath:assets/img/artist/Domenikos_Theotokopoulos_-_El_Greco_-_1541-1614.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a59 = new Artist(null, "El Greco", "Español", parseDate("1541-10-01"), parseDate("1614-04-06"),
					new Date(), imageBytes,
					"Pintor único por su estilo alargado y expresivo, que fusionó influencias bizantinas, italianas y españolas.",
					new ArrayList<>());
			artistService.save(a59);
			resource = resourceLoader.getResource(
					"classpath:assets/img/artist/Francisco_de_Goya_por-Vicente_Lopez_Portana-_1826_-_94_x_78_cm_-_Prado_Museum_-_Madrid.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a60 = new Artist(null, "Francisco de Goya", "Español", parseDate("1746-03-30"),
					parseDate("1828-04-16"), new Date(), imageBytes,
					"Uno de los grandes maestros españoles, cuya obra abarca desde retratos de la nobleza hasta oscuras visiones de la guerra.",
					new ArrayList<>());
			artistService.save(a60);
			resource = resourceLoader.getResource("classpath:assets/img/artist/descarga (13).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a62 = new Artist(null, "Pablo Picasso", "Español", parseDate("1881-10-25"), parseDate("1973-04-08"),
					new Date(), imageBytes,
					"Uno de los artistas más influyentes del siglo XX, cofundador del cubismo y renovador de la pintura y la escultura.",
					new ArrayList<>());
			artistService.save(a62);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Salvador_Dalí_1939.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a63 = new Artist(null, "Salvador Dalí", "Español", parseDate("1904-05-11"), parseDate("1989-01-23"),
					new Date(), imageBytes,
					"Pintor surrealista, famoso por sus imágenes oníricas y técnicas innovadoras que exploraron el subconsciente.",
					new ArrayList<>());
			artistService.save(a63);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Sandro_Botticelli_-_1445-1510.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a64 = new Artist(null, "Sandro Botticelli", "Italiano", parseDate("1445-01-01"),
					parseDate("1510-04-17"), new Date(), imageBytes,
					"Pintor del Renacimiento italiano, célebre por obras como 'El nacimiento de Venus' que encarnan la belleza clásica.",
					new ArrayList<>());
			artistService.save(a64);

			Artist a65 = new Artist(null, "Jacopo Ligozzi", "Italiano", parseDate("1547-01-01"),
					parseDate("1627-01-01"), new Date(), null,
					"Pintor y dibujante renombrado en la corte de Mantua, conocido por sus estudios de la naturaleza y figuras mitológicas.",
					new ArrayList<>());
			artistService.save(a65);

			Artist a66 = new Artist(null, "Jacopo Pontormo", "Italiano", parseDate("1494-01-01"),
					parseDate("1557-01-01"), new Date(), null,
					"Pintor del manierismo italiano, reconocido por su estilo emocional y composiciones complejas.",
					new ArrayList<>());
			artistService.save(a66);

			Artist a67 = new Artist(null, "Agnolo Bronzino", "Italiano", parseDate("1503-01-01"),
					parseDate("1572-01-01"), new Date(), null,
					"Pintor de la corte medicea, destacado por sus retratos nítidos y elegantes del Renacimiento.",
					new ArrayList<>());
			artistService.save(a67);

			Artist a68 = new Artist(null, "Rosso Fiorentino", "Italiano", parseDate("1494-01-01"),
					parseDate("1540-01-01"), new Date(), null,
					"Pintor manierista, conocido por sus composiciones dramáticas y colores vibrantes.",
					new ArrayList<>());
			artistService.save(a68);
			resource = resourceLoader.getResource("classpath:assets/img/artist/166681.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a71 = new Artist(null, "Jackson Pollock", "Estadounidense", parseDate("1912-01-28"),
					parseDate("1956-08-11"), new Date(), imageBytes,
					"Pintor estadounidense, pionero del expresionismo abstracto, célebre por su técnica de dripping.",
					new ArrayList<>());
			artistService.save(a71);
			resource = resourceLoader.getResource("classpath:assets/img/artist/licensed-image (10).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a72 = new Artist(null, "Wassily Kandinsky", "Ruso", parseDate("1866-12-16"), parseDate("1944-12-13"),
					new Date(), imageBytes,
					"Pionero del arte abstracto, sus composiciones influyeron notablemente en la dirección del arte moderno.",
					new ArrayList<>());
			artistService.save(a72);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Albrecht_Durer_-_Self-portrait.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a73 = new Artist(null, "Albrecht Dürer", "Alemán", parseDate("1471-05-21"), parseDate("1528-04-06"),
					new Date(), imageBytes,
					"Grabador y pintor del Renacimiento alemán, famoso por la precisión de sus obras y autorretratos.",
					new ArrayList<>());
			artistService.save(a73);

			Artist a74 = new Artist(null, "Jean-Honoré Fragonard", "Francés", parseDate("1732-04-07"),
					parseDate("1806-08-22"), new Date(), null,
					"Pintor rococó francés, reconocido por sus obras llenas de fantasía y sensualidad.",
					new ArrayList<>());
			artistService.save(a74);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Winslow_Homer_-_1836-1910.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a75 = new Artist(null, "Winslow Homer", "Estadounidense", parseDate("1836-02-24"),
					parseDate("1910-09-29"), new Date(), imageBytes,
					"Pintor estadounidense, célebre por sus escenas del mundo natural y la vida marítima.",
					new ArrayList<>());
			artistService.save(a75);

			Artist a76 = new Artist(null, "Michael Heizer", "Estadounidense", parseDate("1944-01-01"), null, new Date(),
					null,
					"Artista contemporáneo reconocido por sus monumentales obras escultóricas y de instalación en grandes espacios.",
					new ArrayList<>());
			artistService.save(a76);

			Artist a77 = new Artist(null, "Henry Moore", "Británico", parseDate("1898-07-30"), parseDate("1986-08-31"),
					new Date(), null,
					"Escultor británico, conocido por sus formas orgánicas y abstractas que redefinieron la escultura moderna.",
					new ArrayList<>());
			artistService.save(a77);
			resource = resourceLoader.getResource("classpath:assets/img/artist/Henri_Matisse_-_1869-1954.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			Artist a78 = new Artist(null, "Henri Matisse", "Francés", parseDate("1869-12-31"), parseDate("1954-11-03"),
					new Date(), imageBytes,
					"Figura central en el fauvismo, reconocido por su uso innovador del color y la forma.",
					new ArrayList<>());
			artistService.save(a78);

			Artist a79 = new Artist(null, "Grant Wood", "Estadounidense", parseDate("1891-10-13"),
					parseDate("1942-12-12"), new Date(), null,
					"Pintor estadounidense, recordado por obras que retratan la vida rural en Estados Unidos, como 'American Gothic'.",
					new ArrayList<>());
			artistService.save(a79);

			Artist a80 = new Artist(null, "Edward Hopper", "Estadounidense", parseDate("1882-07-22"),
					parseDate("1967-05-15"), new Date(), null,
					"Pintor estadounidense, célebre por sus representaciones de la soledad y el aislamiento en escenarios urbanos.",
					new ArrayList<>());
			artistService.save(a80);

			Artist a81 = new Artist(null, "Mary Cassatt", "Estadounidense", parseDate("1844-05-22"),
					parseDate("1926-06-14"), new Date(), null,
					"Pintora y grabadora, miembro clave del movimiento impresionista, reconocida por sus retratos íntimos y sensibles de mujeres y niños.",
					new ArrayList<>());
			artistService.save(a81);

			Artist a83 = new Artist(null, "Wang Xizhi", "Chino", parseDate("303-01-01"), parseDate("361-01-01"),
					new Date(), null,
					"Famoso calígrafo chino, venerado por la elegancia y fluidez de su escritura en la antigüedad.",
					new ArrayList<>());
			artistService.save(a83);

			Artist a84 = new Artist(null, "Guo Xi", "Chino", parseDate("1020-01-01"), parseDate("1090-01-01"),
					new Date(), null,
					"Pintor de paisajes de la dinastía Song, reconocido por sus composiciones que capturan la atmósfera y la naturaleza.",
					new ArrayList<>());
			artistService.save(a84);

			// ====================================
			// Ingresar 10 Obras para cada Museo
			// ====================================

			// --- Museo 1: Louvre ---
			resource = resourceLoader.getResource("classpath:assets/img/artwork/Mona-Lisa-by-Leonardo-d-Vinci (1).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Mona Lisa", daVinci, 1503,
					"Una de las obras más famosas y enigmáticas del mundo, célebre por la misteriosa sonrisa de su sujeto.",
					imageBytes, m1));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/venus_milo.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Venus de Milo", alejandro_de_Antioquia, -130,
					"Escultura clásica que representa a la diosa Venus, reconocida por su belleza y la pérdida de sus brazos.",
					imageBytes, m1));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/Samothrace.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Nike de Samothrace", anonimo, -190,
					"La Victoria alada de Samotracia, que simboliza la victoria y se destaca por su dinamismo.",
					imageBytes, m1));
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/The_Coronation_of_Napoleon_(1805-1807).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Coronación de Napoleón", jacques_Louis_David, 1807,
					"Una obra monumental que representa la coronación de Napoleón Bonaparte, símbolo del poder revolucionario.",
					imageBytes, m1));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/001-the-raft-of-the-medusa-1819_1.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Balsa de la Medusa", théodore_Géricault, 1818,
					"Pintura impactante que representa el naufragio de la fragata Medusa y la lucha por la supervivencia.",
					imageBytes, m1));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/The-Liberty-Leading-the-People-0.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Libertad guiando al pueblo", delacroix, 1830,
					"Obra emblemática que simboliza la revolución y la lucha por la libertad.", imageBytes, m1));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/La_belle_ferronnière.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Belle Ferronnière", daVinci, 1490,
					"Retrato renacentista que destaca por su refinada composición y misterio en la expresión de la figura.",
					imageBytes, m1));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/001-the-marriage-at-cana (1).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "El Matrimonio en Cana", veronese, 1563,
					"Una pintura monumental que narra el primer milagro de Jesús, en la que transforma el agua en vino.",
					imageBytes, m1));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/davidjl002 (1).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "El Juramento de los Horacios", jacques_Louis_David, 1784,
					"Una obra que encarna los valores de honor y sacrificio, representada en una composición clásica.",
					imageBytes, m1));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/Vergine_delle_Rocce_(Louvre).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Virgen de las Rocas", daVinci, 1483,
					"Una enigmática representación de la Virgen y el Niño, con una composición que destaca por su luminosidad.",
					imageBytes, m1));

			// --- Museo 2: Museos Vaticanos ---
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/001-michelangelo-creation-of-adam-cropped.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Creación de Adán", miguelangel, 1512,
					"Fresco icónico del techo de la Capilla Sixtina, que simboliza el acto divino de dar vida al hombre.",
					imageBytes, m2));
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/Michelangelo-Buonarroti-Last-Judgment-S.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "El Juicio Final", miguelangel, 1541,
					"Una imponente representación del juicio final que ocupa la pared del altar de la Capilla Sixtina.",
					imageBytes, m2));
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/800px-Michelangelo's_Pieta_5450_cropncleaned.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Piedad", miguelangel, 1498,
					"Escultura que muestra a la Virgen María sosteniendo a Jesús, destacada por su emotividad y detalle anatómico.",
					imageBytes, m2));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/Laocoonte_y_sus_hijos.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Laocoonte y sus Hijos", anonimo, 1,
					"Escultura helenística que representa la trágica lucha de Laocoonte y sus hijos contra serpientes marinas.",
					imageBytes, m2));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/Laocoonte_y_sus_hijos.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "El Torso de Belvedere", anonimo, -100,
					"Fragmento escultórico clásico que ha influido en el estudio de la anatomía ideal.", imageBytes,
					m2));
			artworkService.save(new Artwork(null, "Transfiguración", rafael, 1516,
					"Fresco que narra la transfiguración de Cristo, fusionando lo divino y lo humano en una composición equilibrada.",
					null, m2));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/Fra-Angelico-The-Annunciation-S.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Anunciación", fraAngelico, 1440,
					"Una obra serena que representa el anuncio del ángel Gabriel a la Virgen María.", imageBytes, m2));
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/Rogier-Van-Der-Weyden-Deposition-S.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "El Descendimiento de la Cruz", rogier_van_der_Weyden, 1435,
					"Pintura que ilustra la bajada de Cristo de la cruz, destacada por su emotividad y composición.",
					imageBytes, m2));
			resource = resourceLoader.getResource(
					"classpath:assets/img/artwork/800px-The_Conversion_of_Saint_Paul-Caravaggio_(c._1600-1).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Conversión de San Pablo", caravaggio, 1601,
					"Una obra dramática que captura el instante de la conversión de San Pablo, con un fuerte manejo del claroscuro.",
					imageBytes, m2));
			artworkService.save(new Artwork(null, "Retrato de un Santo", anonimo, 1500,
					"Un ejemplo representativo de la escultura renacentista dedicada a figuras sagradas.", null, m2));

			// --- Museo 3: The Metropolitan Museum of Art ---
			resource = resourceLoader.getResource(
					"classpath:assets/img/artwork/Pieter_Bruegel_the_Elder-_The_Harvesters_-_Google_Art_Project.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Los Cosechadores", pieterBruegelelViejo, 1565,
					"Pintura que muestra campesinos en plena labor, destacada por su detallada representación de la vida rural europea.",
					imageBytes, m3));
			resource = resourceLoader.getResource(
					"classpath:assets/img/artwork/800px-Filippo_Lippi_-_Madonna_col_Bambino_e_due_angeli_-_Google_Art_Project.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Madonna and Child with Two Angels", fraFilippoLippi, 1450,
					"Una obra delicada del Renacimiento que retrata la Virgen con el Niño rodeada de ángeles.",
					imageBytes, m3));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/013-self-portrait.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Self-Portrait", vanGogh, 1889,
					"Autorretrato que destaca por la expresividad y uso característico del color por parte de Van Gogh.",
					imageBytes, m3));
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/1280px-The_Temple_of_Dendur_MET_DT563.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Temple of Dendur", anonimo, -15,
					"Una estructura arquitectónica egipcia trasladada al Met, que data del siglo I a.C.", imageBytes,
					m3));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/001-WashingtonCrossingtheDelaware.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Washington Crossing the Delaware", emanuelLeutze, 1851,
					"Obra emblemática que representa un momento decisivo durante la guerra revolucionaria de Estados Unidos.",
					imageBytes, m3));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/001-musicians.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "The Musicians", caravaggio, 1600,
					"Pintura que ilustra la maestría de Caravaggio en el uso del claroscuro y la composición dinámica.",
					imageBytes, m3));
			artworkService.save(new Artwork(null, "Sarcófago Egipcio", anonimo, -100,
					"Una pieza de la colección egipcia del Met, representativa de la cultura del Antiguo Egipto.", null,
					m3));
			artworkService.save(new Artwork(null, "Arms and Armor", anonimo, 17,
					"Objeto representativo de la colección de armaduras históricas del museo.", null, m3));

			// --- Museo 4: British Museum ---
			artworkService.save(new Artwork(null, "La Piedra de Rosetta", anonimo, -196,
					"La clave para descifrar los jeroglíficos egipcios, fundamental en el estudio de la civilización antigua.",
					null, m4));
			artworkService.save(new Artwork(null, "Los Mármoles de Elgin", variosautores, 5,
					"Una colección de esculturas clásicas provenientes de la antigua Grecia.", null, m4));
			artworkService.save(new Artwork(null, "Friso de Persépolis", anonimo, -500,
					"Relieves que ilustran las grandezas del Imperio Aqueménida, extraídos de la antigua Persépolis.",
					null, m4));
			artworkService.save(new Artwork(null, "Busto de Ramsés II", anonimo, 1279,
					"Una representación escultórica del faraón Ramsés II, destacada por su imponente presencia.", null,
					m4));
			artworkService.save(new Artwork(null, "Sello de Ur-Nammu", anonimo, 2100,
					"Uno de los sellos cuneiformes más antiguos, fundamental para la historia mesopotámica.", null,
					m4));
			artworkService.save(new Artwork(null, "Frescos de Pompéi", variosautores, 79,
					"Piezas conservadas de la antigua ciudad de Pompeya que revelan la vida cotidiana en la antigua Roma.",
					null, m4));
			artworkService.save(new Artwork(null, "Cerámica Griega Clásica", variosautores, -450,
					"Ejemplos de la exquisita cerámica de la Antigua Grecia, reconocida por su decoración y forma.",
					null, m4));
			artworkService.save(new Artwork(null, "Colección Numismática", anonimo, 1,
					"Un compendio de monedas antiguas que ilustran la evolución económica y cultural.", null, m4));
			artworkService.save(new Artwork(null, "Manuscrito Iluminado", anonimo, 800,
					"Ejemplo notable de la producción de manuscritos medievales, ricamente decorados.", null, m4));
			artworkService.save(new Artwork(null, "Artefacto Romano", anonimo, 50,
					"Un objeto representativo del arte y la ingeniería del Imperio Romano.", null, m4));

			// --- Museo 5: Tate Modern ---
			artworkService.save(new Artwork(null, "The Physical Impossibility of Death in the Mind of Someone Living",
					damienHirst, 1991,
					"Instalación que utiliza un tiburón ahumado para explorar la relación entre la vida y la muerte.",
					null, m5));
			artworkService.save(new Artwork(null, "My Bed", traceyEmin, 1998,
					"Una instalación que expone la intimidad personal del artista, mezclando vida privada y arte conceptual.",
					null, m5));
			artworkService.save(new Artwork(null, "Untitled (Portrait of Ross in L.A.)", a24, 1991,
					"Obra minimalista que invita a reflexionar sobre la fragilidad y la memoria mediante objetos cotidianos.",
					null, m5));
			artworkService.save(new Artwork(null, "Marilyn Diptych", a25, 1962,
					"Obra icónica del arte pop, que reinterpreta la imagen de Marilyn Monroe en múltiples formatos.",
					null, m5));
			artworkService.save(new Artwork(null, "The Weather Project", a26, 2003,
					"Instalación que recrea la atmósfera del sol y la niebla, transformando el espacio expositivo.",
					null, m5));
			artworkService.save(new Artwork(null, "Fountain", a27, 1917,
					"Una obra que desafía las convenciones del arte al presentar un urinario como pieza artística.",
					null, m5));
			artworkService.save(new Artwork(null, "Cubi", a28, 1961,
					"Esculturas abstractas que exploran el equilibrio entre forma y espacio en el arte moderno.", null,
					m5));
			artworkService.save(new Artwork(null, "Obra Famosa Tate Modern #9", a29, 2000,
					"Una instalación representativa del arte contemporáneo actual, que desafía el espectador a reinterpretar el espacio.",
					null, m5));
			artworkService.save(new Artwork(null, "Obra Famosa Tate Modern #10", a29, 2005,
					"Una pieza que encapsula el espíritu experimental y vanguardista del Tate Modern.", null, m5));
			artworkService.save(new Artwork(null, "Obra Famosa Tate Modern #11", a29, 2010,
					"Una obra que marca una tendencia en el arte digital y multimedia.", null, m5));

			// --- Museo 6: National Gallery, Londres ---
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/Vincent-Van-Gogh-Twelve-Sunflowers-in-a-Vase-S.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Sunflowers", vanGogh, 1888,
					"Una de las series de pinturas más conocidas de Van Gogh, que celebra el color y la vida.",
					imageBytes, m6));
			resource = resourceLoader.getResource(
					"classpath:assets/img/artwork/William-Turner-The-Fighting-Temeraire-Tugged-to-Her-Last-Berth-to-Be-Broken-up-S.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "The Fighting Temeraire", a30, 1839,
					"Una pintura que muestra el ocaso de una era naval, imborrable por su tratamiento del color y la luz.",
					imageBytes, m6));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/self-portrait_1937.1.72.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Self-Portrait", a31, 1660,
					"Uno de los autorretratos más intensos y personales del maestro holandés Rembrandt.", imageBytes,
					m6));
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/800px-Whistlejacket_by_George_Stubbs_edit.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Whistlejacket", a32, 1762,
					"Famosa pintura ecuestre que captura la majestuosidad y fuerza del caballo en acción.", imageBytes,
					m6));
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/Georges-Pierre-Seurat-Bathers-at-Asnieres-S.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Bathers at Asnières", a33, 1884,
					"Una obra innovadora en técnica pointillista, que muestra la vida cotidiana de la clase trabajadora a las afueras de París.",
					imageBytes, m6));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/001-the-ambassadors-1533 (1).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "The Ambassadors", a34, 1533,
					"Un retrato doble que es célebre por su detallado simbolismo y el enigmático anamorfosis.",
					imageBytes, m6));
			resource = resourceLoader.getResource(
					"classpath:assets/img/artwork/James-Abbott-Mcneill-Whistler-Arrangement-in-Grey-and-Black.-Portrait-of-the-Pai...ainter_s-Mother-S.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Arrangement in Grey and Black No.1", a35, 1871,
					"Conocida popularmente como 'El retrato de madre', es un ejemplo del arte tonal de Whistler.",
					imageBytes, m6));
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/John-Everett-Millais-Ophelia-Cropped--S.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Ophelia", a36, 1852,
					"Una obra romántica que representa la trágica figura de Ofelia, con una detallada representación del entorno natural.",
					imageBytes, m6));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/001-the-lady-of-shalott-1888.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "The Lady of Shalott", a37, 1888,
					"Un retrato onírico basado en la leyenda artúrica, lleno de simbolismo y belleza pictórica.",
					imageBytes, m6));

			// --- Museo 7: Victoria and Albert Museum, Londres ---
			artworkService.save(new Artwork(null, "The Unicorn Tapestries", anonimo, 1500,
					"Conjunto de tapices que narran la cacería del unicornio, símbolo de la pureza y el misticismo en la Edad Media.",
					null, m7));
			artworkService.save(new Artwork(null, "Napoleonic Sobriquet", anonimo, 1805,
					"Una exquisita pieza de joyería y orfebrería representativa del refinamiento del periodo napoleónico.",
					null, m7));
			artworkService.save(new Artwork(null, "Oriental Carpets", anonimo, 1700,
					"Una colección representativa de alfombras orientales con complejos patrones geométricos y colores vibrantes.",
					null, m7));
			artworkService.save(new Artwork(null, "Medieval Metalwork", anonimo, 1300,
					"Ejemplo de orfebrería medieval, con detalles ornamentales típicos de la época.", null, m7));
			artworkService.save(new Artwork(null, "Sarcophagus", anonimo, 150,
					"Un sarcófago decorado con relieves que ilustran escenas mitológicas, típico del arte funerario antiguo.",
					null, m7));
			artworkService.save(new Artwork(null, "Ceramic Vase", anonimo, 1600,
					"Un jarrón decorativo que muestra la maestría en la cerámica de la era isabelina.", null, m7));
			artworkService.save(new Artwork(null, "Textile Embroidery", anonimo, 1750,
					"Un ejemplo impresionante de bordado, evidenciando técnicas artesanales tradicionales.", null, m7));
			artworkService.save(new Artwork(null, "Fashion Illustrations", anonimo, 1900,
					"Ilustraciones de moda que destacan el estilo y la sofisticación de principios del siglo XX.", null,
					m7));
			artworkService.save(new Artwork(null, "Sculpture in Bronze", anonimo, 1800,
					"Una escultura en bronce que fusiona tradición y modernidad en su acabado artístico.", null, m7));
			artworkService.save(new Artwork(null, "Exquisite Furniture", anonimo, 1850,
					"Una pieza de mobiliario decorativo que ejemplifica el lujo y la elegancia de la época victoriana.",
					null, m7));

			// --- Museo 8: Rijksmuseum, Ámsterdam ---
			resource = resourceLoader.getResource("classpath:assets/img/artwork/002-The_Night_Watch_-_HD.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "The Night Watch", a31, 1642,
					"Una de las pinturas más famosas de Rembrandt, famosa por su uso dramático de la luz y composición dinámica.",
					imageBytes, m8));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/Jan-Vermeer-The-Milkmaid-S.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "The Milkmaid", a38, 1658,
					"Una obra maestra que representa la vida cotidiana en la Holanda del siglo XVII con gran detalle y realismo.",
					imageBytes, m8));
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/Self-portrait_(1628-1629),_by_Rembrandt.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Self-Portrait", a31, 1660,
					"Un autorretrato que destaca la maestría de Rembrandt en la representación del alma humana.",
					imageBytes, m8));
			artworkService.save(new Artwork(null, "The Weaver", a39, 1665,
					"Pintura que retrata la vida diaria y la labor en una aldea holandesa del siglo XVII.", null, m8));
			artworkService.save(new Artwork(null, "Landscape with Cattle", a40, 1660,
					"Un paisaje majestuoso que muestra la naturaleza de los Países Bajos en el Siglo de Oro.", null,
					m8));
			artworkService.save(new Artwork(null, "Portrait of a Family", a41, 1665,
					"Una vívida representación de la vida familiar en la Holanda del siglo XVII, con humor y detalle.",
					null, m8));
			artworkService.save(new Artwork(null, "Still Life with Flowers", a42, 1700,
					"Una composición floral que resalta la delicadeza y el detalle característico de esta pintora holandesa.",
					null, m8));
			artworkService.save(new Artwork(null, "The Battle of the Zuiderzee", anonimo, 17,
					"Una representación histórica de una batalla naval que marcó la historia de los Países Bajos.",
					null, m8));
			artworkService.save(new Artwork(null, "The Dutch Kitchen", anonimo, 1660,
					"Una escena doméstica que captura la esencia de la vida cotidiana en la Holanda del Siglo de Oro.",
					null, m8));
			artworkService.save(new Artwork(null, "The Flower Market", anonimo, 1650,
					"Una pintura que muestra la vibrante atmósfera de un mercado de flores en Ámsterdam.", null, m8));

			// --- Museo 9: Van Gogh Museum, Ámsterdam ---
			artworkService.save(new Artwork(null, "Sunflowers", vanGogh, 1888,
					"Una de las series más famosas de Van Gogh, que celebra la belleza y vitalidad de los girasoles.",
					null, m9));
			artworkService.save(new Artwork(null, "The Potato Eaters", vanGogh, 1885,
					"Una representación cruda de la vida rural holandesa, con un fuerte realismo social.", null, m9));
			artworkService.save(new Artwork(null, "Irises", vanGogh, 1889,
					"Pintura que destaca la intensidad y el vibrante colorido de las flores, realizada durante su estancia en el asilo.",
					null, m9));
			artworkService.save(new Artwork(null, "Wheatfield with Crows", vanGogh, 1890,
					"Una de las obras finales de Van Gogh, llena de emoción y simbolismo en un campo de trigo tormentoso.",
					null, m9));
			artworkService.save(new Artwork(null, "Self-Portrait", vanGogh, 1887,
					"Uno de los numerosos autorretratos del pintor, que refleja su lucha interna y pasión por el arte.",
					null, m9));
			artworkService.save(new Artwork(null, "Bedroom in Arles", vanGogh, 1888,
					"Una representación íntima del espacio personal del artista en Arles, con una composición singular.",
					null, m9));
			artworkService.save(new Artwork(null, "Café Terrace at Night", vanGogh, 1888,
					"Pintura que captura la atmósfera nocturna de un café en Arles, con un vibrante uso del color y la luz.",
					null, m9));
			artworkService.save(new Artwork(null, "Starry Night Over the Rhone", vanGogh, 1888,
					"Una obra que muestra la escena nocturna sobre el río Ródano, destacada por sus destellos de luz en el agua.",
					null, m9));
			artworkService.save(new Artwork(null, "Olive Trees", vanGogh, 1889,
					"Una pintura que enfatiza el paisaje mediterráneo y la fuerza de la naturaleza a través de robustos olivos.",
					null, m9));
			artworkService.save(new Artwork(null, "Sower", vanGogh, 1888,
					"Una obra que representa la figura del sembrador en un campo, símbolo de la conexión del hombre con la tierra.",
					null, m9));

			// --- Museo 10: Stedelijk Museum, Ámsterdam ---
			artworkService.save(new Artwork(null, "Composition No. III", a43, 1929,
					"Una obra abstracta que utiliza líneas y bloques de color primario para crear equilibrio y armonía.",
					null, m10));
			artworkService.save(new Artwork(null, "Broadway Boogie Woogie", a43, 1943,
					"Pintura icónica que refleja el dinamismo urbano a través de un sistema de colores y retículas geométricas.",
					null, m10));
			artworkService.save(new Artwork(null, "Homage to the Square", a44, 1950,
					"Una serie de composiciones que exploran las relaciones de color y la percepción a través de cuadrados concéntricos.",
					null, m10));
			artworkService.save(new Artwork(null, "Automatic Drawing", a45, 1942,
					"Obra que representa la abstracción y el azar, elementos fundamentales del arte experimental.",
					null, m10));
			artworkService.save(new Artwork(null, "Abstract Composition", anonimo, 1930,
					"Un ejemplo representativo de la experimentación en el arte abstracto del siglo XX.", null, m10));
			artworkService.save(new Artwork(null, "Geometric Forms", anonimo, 1925,
					"Una composición basada en formas geométricas simples que explora la relación entre el espacio y el color.",
					null, m10));
			artworkService.save(new Artwork(null, "Modern Landscape", anonimo, 1935,
					"Una interpretación abstracta de un paisaje urbano, donde el color y la forma se conjugan para crear atmósfera.",
					null, m10));
			artworkService.save(new Artwork(null, "Dynamic Lines", anonimo, 1940,
					"Una obra que captura el movimiento y la energía a través de líneas y contrastes fuertes.", null,
					m10));
			artworkService.save(new Artwork(null, "Color Field", anonimo, 1955,
					"Ejemplo del movimiento color field, donde grandes bloques de color transmiten emociones puras.",
					null, m10));
			artworkService.save(new Artwork(null, "Urban Abstraction", anonimo, 1960,
					"Una obra que fusiona la abstracción con elementos urbanos, reflejando la complejidad de la vida moderna.",
					null, m10));

			// --- Museo 11: Hermitage Museum, San Petersburgo ---
			artworkService.save(new Artwork(null, "Madonna Litta", daVinci, 1480,
					"Un retrato de la Virgen y el Niño, famoso por su delicadeza y ejecución renacentista.", null,
					m11));
			artworkService.save(new Artwork(null, "Danaë", a46, 1531,
					"Una pintura que representa la mitológica historia de Danaë, destacada por su sensualidad y uso de la luz.",
					null, m11));
			artworkService.save(new Artwork(null, "Rape of Europa", a47, 1562,
					"Obra que narra el rapto de Europa, con un vívido uso del color y la composición.", null, m11));
			artworkService.save(new Artwork(null, "Venus Anadyomene", a47, 1555,
					"Una interpretación renacentista de la diosa Venus emergiendo del mar.", null, m11));
			artworkService.save(new Artwork(null, "The Return of the Prodigal Son", a31, 1669,
					"Una representación conmovedora del perdón y la redención, con un manejo magistral de la luz y la sombra.",
					null, m11));
			artworkService.save(new Artwork(null, "The Five Senses", a48, 1752,
					"Una serie de frescos que simbolizan los sentidos humanos, característicos del estilo rococó.",
					null, m11));
			artworkService.save(new Artwork(null, "Apollo Belvedere", anonimo, 120,
					"Una escultura clásica que encarna la perfección del cuerpo humano en la antigua Grecia.", null,
					m11));
			artworkService.save(new Artwork(null, "Sphinx", anonimo, -250,
					"Una representación emblemática de la enigmática esfinge egipcia, pieza central de la colección de antigüedades.",
					null, m11));
			artworkService.save(new Artwork(null, "Alexander Mosaic", anonimo, 100,
					"Un impresionante mosaico romano que retrata la victoria de Alejandro Magno.", null, m11));
			artworkService.save(new Artwork(null, "Portrait of a Lady", a31, 1660,
					"Un retrato que destaca la maestría del artista en capturar la personalidad y la emoción.", null,
					m11));

			// --- Museo 12: Tretyakov Gallery, Moscú ---
			artworkService.save(new Artwork(null, "The Appearance of Christ Before the People", a49, 1837,
					"Una obra monumental del romanticismo ruso que muestra la aparición de Cristo ante el pueblo.",
					null, m12));
			artworkService.save(new Artwork(null, "Bogatyr", a50, 1810,
					"Una pintura que evoca la épica tradición de los héroes rusos, los bogatyrs.", null, m12));
			artworkService.save(new Artwork(null, "Ivan the Terrible and His Son Ivan", a51, 1885,
					"Una obra profundamente emotiva que retrata la trágica figura de Iván el Terrible y el dolor de la familia real.",
					null, m12));
			artworkService.save(new Artwork(null, "Portrait of a Peasant", a52, 1870,
					"Un retrato que refleja la dignidad y la dureza de la vida rural en Rusia.", null, m12));
			artworkService.save(new Artwork(null, "Russian Field", a53, 1871,
					"Una pintura melancólica que capta la vasta extensión de la campiña rusa.", null, m12));
			artworkService.save(new Artwork(null, "The Rusalka", a54, 1886,
					"Una representación lírica de una ninfa del agua, destacada por su atmósfera ensoñadora.", null,
					m12));
			artworkService.save(new Artwork(null, "Dark Night", a55, 1896,
					"Una obra emblemática que muestra la melancolía y la belleza de la naturaleza rusa en un crepúsculo sombrío.",
					null, m12));
			artworkService.save(new Artwork(null, "Morning in a Pine Forest", anonimo, 1889,
					"Una representación idílica del amanecer en un bosque de pinos, muy popular en la pintura rusa.",
					null, m12));
			artworkService.save(new Artwork(null, "The Icon of the Virgin", anonimo, 1500,
					"Un icono religioso de gran veneración en la tradición ortodoxa rusa.", null, m12));
			artworkService.save(new Artwork(null, "Landscape with Stream", anonimo, 1865,
					"Una tranquila escena natural que evoca la esencia del paisaje ruso.", null, m12));

			// --- Museo 13: Museo del Prado, Madrid ---
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/Las_Meninas_Die_Hoffraulein-by-Diego_Velazquez (1).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Las Meninas", a57, 1656,
					"Una de las obras más enigmáticas del Barroco español, que juega con la perspectiva y la presencia del propio pintor.",
					imageBytes, m13));
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/El_jardín_de_las_Delicias,_de_El_Bosco.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "El jardín de las delicias", a58, 1500,
					"Un tríptico surrealista que representa el Edén, la vida terrenal y el infierno en una compleja narrativa visual.",
					imageBytes, m13));
			resource = resourceLoader.getResource(
					"classpath:assets/img/artwork/800px-El_caballero_de_la_mano_en_el_pecho,_by_El_Greco,_from_Prado_in_Google_Earth.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "El caballero de la mano en el pecho", a59, 1580,
					"Retrato emblemático de la nobleza española, reconocido por su expresividad y uso dramático del color.",
					imageBytes, m13));
			resource = resourceLoader.getResource(
					"classpath:assets/img/artwork/003-francisco-de-goya-saturno-devorando-a-su-hijo-1819-1823.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Saturno devorando a su hijo", a60, 1819,
					"Una de las pinturas más impactantes de Goya, que simboliza la brutalidad y la desesperación.",
					imageBytes, m13));
			resource = resourceLoader.getResource(
					"classpath:assets/img/artwork/Carlos_V_en_Mühlberg,_by_Titian,_from_Prado_in_Google_Earth.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "El emperador Carlos V a caballo", a47, 1548,
					"Pintura que celebra el poder imperial mediante una majestuosa representación ecuestre.",
					imageBytes, m13));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/El triunfo de la Muerte.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "El triunfo de la Muerte", pieterBruegelelViejo, 1630,
					"Obra moral que muestra el triunfo de la Muerte sobre las cosas mundanas, simbolizado a través de un gran ejército de esqueletos arrasando la Tierra.",
					imageBytes, m13));
			resource = resourceLoader.getResource(
					"classpath:assets/img/artwork/Diego-Rodriguez-De-Silva-Y-Velazquez-The-Surrender-of-Breda-Las-Lanzas-2--S.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La rendición de Breda", a57, 1634,
					"Una obra histórica que simboliza la nobleza en la derrota, mediante una composición magistral.",
					imageBytes, m13));
			resource = resourceLoader
					.getResource("classpath:assets/img/artwork/Pablo-Picasso-The-Young-Ladies-of-Avignon-S.jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "Las señoritas de Avignon", a62, 1907,
					"Una obra revolucionaria que marcó el inicio del cubismo y transformó la manera de ver la figura humana.",
					imageBytes, m13));
			resource = resourceLoader.getResource("classpath:assets/img/artwork/goya003 (1).jpg");
			imageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
			artworkService.save(new Artwork(null, "La Maja Desnuda", a60, 1800,
					"Un retrato polémico y audaz que desafió las convenciones artísticas de su época.", imageBytes,
					m13));

			// --- Museo 14: Museo Reina Sofía, Madrid ---
			artworkService.save(new Artwork(null, "Guernica", a62, 1937,
					"Una obra monumental que expresa el horror de la guerra y el sufrimiento humano, símbolo universal de la protesta.",
					null, m14));
			artworkService.save(new Artwork(null, "El Gran Masturbador", a63, 1929,
					"Una pieza surrealista que refleja la dualidad y complejidad del subconsciente humano.", null,
					m14));
			artworkService.save(new Artwork(null, "La Casa de Papel", anonimo, 1930,
					"Obra representativa del arte español del siglo XX, evocadora de las tensiones de la época.", null,
					m14));
			artworkService.save(new Artwork(null, "El Mural del Guernica", anonimo, 1937,
					"Una interpretación muralista del icónico Guernica, reforzando su mensaje anti-bélico.", null,
					m14));
			artworkService.save(new Artwork(null, "Retrato de Gertrudis Gómez de Avellaneda", anonimo, 1863,
					"Un retrato emblemático de la escritora cubana, inmortalizado en un estilo realista y apasionado.",
					null, m14));
			artworkService.save(new Artwork(null, "Algunas Damas", anonimo, 1940,
					"Obra representativa de la evolución del arte moderno en España.", null, m14));
			artworkService.save(new Artwork(null, "El Cubismo en Rojo", a62, 1912,
					"Una muestra del cubismo analítico que caracteriza parte de la obra de Picasso.", null, m14));
			artworkService.save(new Artwork(null, "La Espera", anonimo, 1935,
					"Obra que captura la melancolía y la esperanza del periodo de posguerra en España.", null, m14));
			artworkService.save(new Artwork(null, "La Mujer en Azul", anonimo, 1945,
					"Un retrato que transmite la fuerza y sensibilidad de la mujer española.", null, m14));
			artworkService.save(new Artwork(null, "El Abstraccionismo en el Siglo XX", anonimo, 1950,
					"Obra que ejemplifica la transición al arte abstracto en España.", null, m14));

			// --- Museo 15: Uffizi Gallery, Florencia ---
			artworkService.save(new Artwork(null, "El Nacimiento de Venus", a64, 1486,
					"Una obra emblemática del Renacimiento que representa el nacimiento de la diosa Venus emergiendo del mar.",
					null, m15));
			artworkService.save(new Artwork(null, "La Primavera", a64, 1482,
					"Una pintura alegórica que simboliza el renacer de la naturaleza y la fertilidad.", null, m15));
			artworkService.save(new Artwork(null, "El Mendigo", caravaggio, 1590,
					"Una obra que captura el realismo crudo y dramático de la vida en el Renacimiento italiano.", null,
					m15));
			artworkService.save(new Artwork(null, "Dafne", a65, 1560,
					"Una representación mitológica de la transformación de Dafne en laurel para escapar del acoso de Apolo.",
					null, m15));
			artworkService.save(new Artwork(null, "El Juramento de los Horacios", a66, 1530,
					"Un ejemplo del manierismo florentino que reinterpreta temas clásicos de manera novedosa.", null,
					m15));
			artworkService.save(new Artwork(null, "Retrato de Cosimo de' Medici", a67, 1540,
					"Un retrato renacentista que refleja el poder y la influencia de la familia Medici.", null, m15));
			artworkService.save(new Artwork(null, "El Descendimiento", a68, 1520,
					"Obra llena de emoción y dinamismo que retrata el descenso de Cristo de la cruz.", null, m15));
			artworkService.save(new Artwork(null, "La Anunciación", fraAngelico, 1440,
					"Una representación sublime y espiritual del anuncio del arcángel a la Virgen.", null, m15));
			artworkService.save(new Artwork(null, "El Retrato de Eleonora di Toledo", a67, 1540,
					"Un retrato exquisito de la nobleza renacentista, reconocible por su elegancia y detalle.", null,
					m15));
			artworkService.save(new Artwork(null, "Obra Famosa Uffizi #10", anonimo, 1550,
					"Una obra representativa que cierra la colección de este museo, destacada por su innovación artística.",
					null, m15));

			// --- Museo 16: Accademia Gallery, Florencia ---
			artworkService.save(new Artwork(null, "David", miguelangel, 1504,
					"La escultura renacentista por excelencia, que simboliza el ideal de belleza y proporción.", null,
					m16));
			artworkService.save(new Artwork(null, "La Piedad", miguelangel, 1499,
					"Una emotiva escultura que muestra a la Virgen María con el cuerpo de Jesús, destacada por su ternura y detalle.",
					null, m16));
			artworkService.save(new Artwork(null, "El Nacimiento de Adán", miguelangel, 1512,
					"Una de las obras más célebres del Renacimiento, representada en el techo de la Capilla Sixtina.",
					null, m16));
			artworkService.save(new Artwork(null, "El Juicio Final", miguelangel, 1541,
					"Una imponente obra que representa el destino final de la humanidad, cargada de emoción y dramatismo.",
					null, m16));
			artworkService.save(new Artwork(null, "El Grabado de David", anonimo, 1505,
					"Una representación en relieve que complementa la fama de la escultura de David.", null, m16));
			artworkService.save(new Artwork(null, "Estudio de Anatomía", daVinci, 1510,
					"Un dibujo detallado que muestra la investigación de la anatomía humana, fundamental para el Renacimiento.",
					null, m16));
			artworkService.save(new Artwork(null, "Retrato del Guelfo", anonimo, 1500,
					"Un retrato representativo de la nobleza florentina, con gran realismo y detalle.", null, m16));
			artworkService.save(new Artwork(null, "Bodegón Renacentista", anonimo, 1500,
					"Una composición que destaca los elementos cotidianos en el arte renacentista.", null, m16));
			artworkService.save(new Artwork(null, "Paisaje Toscano", anonimo, 1510,
					"Una visión idealizada de la campiña toscana, llena de luz y serenidad.", null, m16));
			artworkService.save(new Artwork(null, "Obra Famosa Accademia #10", anonimo, 1520,
					"Una pieza destacada que ejemplifica el virtuosismo del periodo.", null, m16));

			// --- Museo 17: Museum of Modern Art (MoMA), Nueva York ---
			artworkService.save(new Artwork(null, "La Noche Estrellada", vanGogh, 1889,
					"Pintura icónica que muestra un vibrante cielo nocturno repleto de remolinos y estrellas brillantes.",
					null, m17));
			artworkService.save(new Artwork(null, "Campbell's Soup Cans", a25, 1962,
					"Obra emblemática del arte pop que representa la cultura de consumo en Estados Unidos.", null,
					m17));
			artworkService.save(new Artwork(null, "Broadway Boogie Woogie", a43, 1943,
					"Una composición abstracta que captura la energía de la ciudad de Nueva York a través de colores primarios y líneas rectas.",
					null, m17));
			artworkService.save(new Artwork(null, "Fountain", a27, 1917,
					"Una provocadora recontextualización de un objeto cotidiano como obra de arte.", null, m17));
			artworkService.save(new Artwork(null, "The Persistence of Memory", a63, 1931,
					"Famosa por sus relojes blandos, esta pintura surrealista explora la relatividad del tiempo.", null,
					m17));
			artworkService.save(new Artwork(null, "No. 5, 1948", a71, 1948,
					"Ejemplo paradigmático del expresionismo abstracto, con su estilo de 'dripping' que revolucionó la pintura.",
					null, m17));
			artworkService.save(new Artwork(null, "Campbell's Soup Cans (Variación)", a25, 1962,
					"Otra mirada al icónico tema del arte pop, enfatizando la repetición y el consumo masivo.", null,
					m17));
			artworkService.save(new Artwork(null, "Composition with Red, Blue, and Yellow", a43, 1930,
					"Una obra que define la estética neoplástica y el equilibrio a través de formas geométricas.", null,
					m17));
			artworkService.save(new Artwork(null, "Number 31", a71, 1950,
					"Una de las expresiones más intensas de la acción painting de Pollock.", null, m17));
			artworkService.save(new Artwork(null, "Obra Famosa MoMA #10", a29, 2000,
					"Una pieza que refleja las tendencias actuales en el arte moderno.", null, m17));

			// --- Museo 18: Guggenheim Museum, Nueva York ---
			artworkService.save(new Artwork(null, "Composition", a43, 1930,
					"Obra pionera del arte abstracto, que utiliza líneas y colores primarios para crear un equilibrio visual.",
					null, m18));
			artworkService.save(new Artwork(null, "Abstract Forms", a72, 1923,
					"Una representación abstracta que destaca la espiritualidad a través del color y la forma.", null,
					m18));
			artworkService.save(new Artwork(null, "The Spiral", anonimo, 1950,
					"Una obra abstracta que simboliza el dinamismo y el movimiento continuo.", null, m18));
			artworkService.save(new Artwork(null, "Organic Shapes", anonimo, 1945,
					"Exploración de formas orgánicas a través de una composición fluida.", null, m18));
			artworkService.save(new Artwork(null, "Waves of Color", anonimo, 1960,
					"Obra que fusiona la abstracción con un vibrante uso del color, típica del arte contemporáneo.",
					null, m18));
			artworkService.save(new Artwork(null, "Construction in Red", anonimo, 1932,
					"Una pieza que muestra la intersección entre el arte y la arquitectura abstracta.", null, m18));
			artworkService.save(new Artwork(null, "Cubic Abstraction", anonimo, 1948,
					"Obra que utiliza formas cúbicas para explorar la profundidad y la perspectiva.", null, m18));
			artworkService.save(new Artwork(null, "Dynamic Composition", anonimo, 1955,
					"Una composición dinámica que juega con la organización espacial y el contraste de colores.", null,
					m18));
			artworkService.save(new Artwork(null, "Red, Blue, and Yellow Revisited", a43, 1943,
					"Una reinterpretación de la iconografía neoplástica, con un enfoque en la precisión geométrica.",
					null, m18));
			artworkService.save(new Artwork(null, "Obra Famosa Guggenheim #10", a29, 2005,
					"Una obra representativa de la evolución del arte en el siglo XXI.", null, m18));

			// --- Museo 19: National Gallery of Art, Washington DC ---
			artworkService.save(new Artwork(null, "Ginevra de' Benci", daVinci, 1474,
					"Un retrato enigmático de una joven florentina, famosa por su mirada misteriosa.", null, m19));
			artworkService.save(new Artwork(null, "Self-Portrait", a73, 1500,
					"Uno de los autorretratos más reconocidos del Renacimiento del Norte, con un intenso realismo.",
					null, m19));
			artworkService.save(new Artwork(null, "A Young Woman", a38, 1665,
					"Obra íntima y delicada que muestra la maestría del claroscuro vermeeriano.", null, m19));
			artworkService.save(new Artwork(null, "The Swing", a74, 1767,
					"Una pintura rococó emblemática que refleja la diversión y la ligereza de la sociedad del siglo XVIII.",
					null, m19));
			artworkService.save(new Artwork(null, "The Gulf Stream", a75, 1899,
					"Una poderosa representación de la lucha del hombre contra la naturaleza en alta mar.", null, m19));
			artworkService.save(new Artwork(null, "The Art of Painting", a38, 1666,
					"Una representación metafórica del acto creativo, que destaca por su composición y detalle.", null,
					m19));
			artworkService.save(new Artwork(null, "The Connoisseur", anonimo, 1850,
					"Obra representativa de la habilidad en el retrato y la observación, propia del arte estadounidense.",
					null, m19));
			artworkService.save(new Artwork(null, "Still Life with Flowers", anonimo, 1700,
					"Una composición serena y armónica que celebra la belleza efímera de la naturaleza.", null, m19));
			artworkService.save(new Artwork(null, "Landscape with River", anonimo, 1800,
					"Pintura que captura la inmensidad del paisaje americano a través del uso del color y la luz.",
					null, m19));
			artworkService.save(new Artwork(null, "Obra Famosa NGA #10", anonimo, 1900,
					"Una pieza que refleja la diversidad de la colección de la National Gallery of Art.", null, m19));

			// --- Museo 20: LACMA, Los Ángeles ---
			artworkService.save(new Artwork(null, "Levitated Mass", a76, 2012,
					"Una monumental escultura que desafía la gravedad, emblemática del LACMA.", null, m20));
			artworkService.save(new Artwork(null, "Reclining Figure", a77, 1969,
					"Una icónica escultura abstracta que representa la forma humana en reposo.", null, m20));
			artworkService.save(new Artwork(null, "Urban Landscape", anonimo, 1970,
					"Obra que captura la esencia cambiante del paisaje urbano de Los Ángeles.", null, m20));
			artworkService.save(new Artwork(null, "Dream in Blue", anonimo, 1985,
					"Una pintura contemporánea que evoca la atmósfera vibrante de la ciudad.", null, m20));
			artworkService.save(new Artwork(null, "The Arrival", anonimo, 1990,
					"Una obra que representa la diversidad cultural y la esperanza en la metrópoli de Los Ángeles.",
					null, m20));
			artworkService.save(new Artwork(null, "Abstract Motion", anonimo, 2000,
					"Pieza que explora la abstracción y el movimiento en el arte moderno.", null, m20));
			artworkService.save(new Artwork(null, "City Lights", anonimo, 1995,
					"Una representación vibrante de la nocturnidad urbana y el brillo de la ciudad.", null, m20));
			artworkService.save(new Artwork(null, "Sunset Over LA", anonimo, 1980,
					"Obra que capta el esplendor del atardecer sobre la ciudad de Los Ángeles.", null, m20));
			artworkService.save(new Artwork(null, "Modern Faces", anonimo, 2005,
					"Una serie de retratos que reflejan la diversidad y la modernidad de la sociedad californiana.",
					null, m20));
			artworkService.save(new Artwork(null, "Obra Famosa LACMA #10", a29, 2010,
					"Una pieza representativa del arte contemporáneo en la costa oeste de EE.UU.", null, m20));

			// --- Museo 21: SFMOMA, San Francisco ---
			artworkService.save(new Artwork(null, "Blue Nude", a78, 1952,
					"Una obra que destaca la maestría de Matisse en la representación de la figura humana mediante el uso del color.",
					null, m21));
			artworkService.save(new Artwork(null, "Abstract Landscape", anonimo, 1965,
					"Una interpretación abstracta de la naturaleza que fusiona formas y colores vibrantes.", null,
					m21));
			artworkService.save(new Artwork(null, "Silhouettes", anonimo, 1970,
					"Obra minimalista que explora la interacción entre luz y sombra en un entorno urbano.", null, m21));
			artworkService.save(new Artwork(null, "Modern Expression", anonimo, 1980,
					"Una pintura que captura la energía y el caos controlado del arte moderno en San Francisco.", null,
					m21));
			artworkService.save(new Artwork(null, "Digital Dreams", anonimo, 1995,
					"Una obra que combina elementos digitales y tradicionales para crear una visión futurista.", null,
					m21));
			artworkService.save(new Artwork(null, "Cityscape", anonimo, 2000,
					"Una representación abstracta de la silueta de la ciudad de San Francisco al atardecer.", null,
					m21));
			artworkService.save(new Artwork(null, "Neon Nights", anonimo, 2005,
					"Una pintura que evoca la atmósfera luminosa y vibrante de la ciudad de noche.", null, m21));
			artworkService.save(new Artwork(null, "Urban Pulse", anonimo, 2010,
					"Obra que simboliza el latido y la energía constante de la metrópoli.", null, m21));
			artworkService.save(new Artwork(null, "Fractured Reality", anonimo, 2015,
					"Una obra contemporánea que juega con la fragmentación y la reconstrucción de la realidad.", null,
					m21));
			artworkService.save(new Artwork(null, "Obra Famosa SFMOMA #10", a29, 2020,
					"Una pieza icónica del arte moderno de la costa oeste, representativa de las tendencias actuales.",
					null, m21));

			// --- Museo 22: Art Institute of Chicago ---
			artworkService.save(new Artwork(null, "American Gothic", a79, 1930,
					"Una de las pinturas estadounidenses más reconocidas, que muestra un granero y la figura de una pareja campesina.",
					null, m22));
			artworkService.save(new Artwork(null, "A Sunday on La Grande Jatte", a33, 1886,
					"Una obra icónica del pointillismo, capturando una escena de ocio en las orillas del río.", null,
					m22));
			artworkService.save(new Artwork(null, "Nighthawks", a80, 1942,
					"Una pintura que evoca la soledad y el aislamiento en la vida urbana estadounidense.", null, m22));
			artworkService.save(new Artwork(null, "The Child's Bath", a81, 1893,
					"Una obra que muestra la intimidad y el afecto en la vida cotidiana, destacada por su sensibilidad.",
					null, m22));
			artworkService.save(new Artwork(null, "Chicago Skyline", anonimo, 1900,
					"Una representación del horizonte de Chicago que captura la transformación industrial de la ciudad.",
					null, m22));
			artworkService.save(new Artwork(null, "Modern Abstraction", anonimo, 1955,
					"Una obra representativa del movimiento abstracto en la pintura moderna.", null, m22));
			artworkService.save(new Artwork(null, "Light and Shadow", anonimo, 1960,
					"Pieza que juega con contrastes de luz y sombra para evocar emociones.", null, m22));
			artworkService.save(new Artwork(null, "City Reflections", anonimo, 1970,
					"Una visión abstracta de los reflejos urbanos sobre el agua.", null, m22));
			artworkService.save(new Artwork(null, "Rhythm of the City", anonimo, 1980,
					"Obra que sintetiza el pulso y la vitalidad de Chicago a través de formas geométricas.", null,
					m22));
			artworkService.save(new Artwork(null, "Obra Famosa AIC #10", a29, 1990,
					"Una pieza icónica que cierra la colección del Art Institute con una mirada moderna.", null, m22));

			// --- Museo 23: National Museum of Korea, Seúl ---
			artworkService.save(new Artwork(null, "Cheongja Sangsang", anonimo, 1500,
					"Una antigua vasija coreana representativa de la cerámica tradicional de Corea.", null, m23));
			artworkService.save(new Artwork(null, "Celadon Bowl", anonimo, 1400,
					"Ejemplo destacado de la porcelana celadón coreana, conocida por su acabado esmerilado.", null,
					m23));
			artworkService.save(new Artwork(null, "Royal Crown", anonimo, 1300,
					"Una pieza ornamental utilizada en ceremonias reales, símbolo del poder dinástico en Corea.", null,
					m23));
			artworkService.save(new Artwork(null, "Buddhist Sculpture", anonimo, 1100,
					"Escultura budista que refleja la influencia del budismo en la cultura coreana.", null, m23));
			artworkService.save(new Artwork(null, "Calligraphy Scroll", anonimo, 1200,
					"Un pergamino con caligrafía tradicional coreana, que destaca por su elegancia y precisión.", null,
					m23));
			artworkService.save(new Artwork(null, "Celadon Jar", anonimo, 1450,
					"Una jarra de porcelana celadón, símbolo de la refinada artesanía coreana.", null, m23));
			artworkService.save(new Artwork(null, "Traditional Hanbok Painting", anonimo, 1600,
					"Una pintura que muestra la vestimenta tradicional coreana, el hanbok, en una escena cotidiana.",
					null, m23));
			artworkService.save(new Artwork(null, "Royal Emblem", anonimo, 1550,
					"Inscripción y emblema real utilizado en objetos ceremoniales, representativo del linaje dinástico.",
					null, m23));
			artworkService.save(new Artwork(null, "Confucian Tablet", anonimo, 1700,
					"Un objeto que refleja la influencia del confucianismo en la educación y la vida moral coreana.",
					null, m23));
			artworkService.save(new Artwork(null, "Obra Famosa NMK #10", anonimo, 1750,
					"Una pieza representativa del arte tradicional coreano, que cierra la colección del museo.", null,
					m23));

			// --- Museo 24: Palace Museum (Ciudad Prohibida), Beijing ---
			artworkService.save(new Artwork(null, "El Nacimiento de Jade", anonimo, 1420,
					"Una antigua obra de arte chino representativa de la nobleza imperial, con jade de gran valor simbólico.",
					null, m24));
			artworkService.save(new Artwork(null, "La Dinastía Ming", anonimo, 1500,
					"Una pintura que celebra la magnificencia y la historia de la dinastía Ming en China.", null, m24));
			artworkService.save(new Artwork(null, "La Emperatriz en Reposo", anonimo, 1450,
					"Un retrato imperial que refleja la elegancia y el poder de la corte china.", null, m24));
			artworkService.save(new Artwork(null, "Caligrafía Imperial", a83, 353,
					"Una obra maestra de la caligrafía china, atribuida a Wang Xizhi, considerada la 'Santidad de la Caligrafía'.",
					null, m24));
			artworkService.save(new Artwork(null, "Cerámica de la Dinastía Tang", anonimo, 700,
					"Pieza representativa de la sofisticada cerámica china, famosa por sus formas y glaseados únicos.",
					null, m24));
			artworkService.save(new Artwork(null, "Jade Imperial", anonimo, 1400,
					"Una escultura y objeto ceremonial de jade, utilizado en la corte imperial para simbolizar poder y pureza.",
					null, m24));
			artworkService.save(new Artwork(null, "Paisaje Chino", a84, 1072,
					"Una pintura de paisaje clásica que representa la filosofía y la estética de la pintura china tradicional.",
					null, m24));
			artworkService.save(new Artwork(null, "El Emperador y los Sabios", anonimo, 1500,
					"Una obra que ilustra la interacción entre el emperador y sus consejeros sabios, fundamental en la cultura imperial.",
					null, m24));
			artworkService.save(new Artwork(null, "Retrato de un Noble", anonimo, 1480,
					"Un retrato que capta la dignidad y la autoridad de un noble de la antigua China.", null, m24));
			artworkService.save(new Artwork(null, "Obra Famosa del Forbidden City #10", anonimo, 1550,
					"Una pieza emblemática que cierra la colección de obras imperiales del Palace Museum.", null, m24));

			// --- Museo 25: Guggenheim Museum Bilbao ---
			artworkService.save(new Artwork(null, "Obra Contemporánea Bilbao 1", anonimo, 2000,
					"Una obra representativa del dinamismo del arte contemporáneo en Bilbao.", null, m25));
			artworkService.save(new Artwork(null, "Obra Contemporánea Bilbao 2", anonimo, 2002,
					"Pieza emblemática que fusiona tradición y modernidad en el contexto vasco.", null, m25));
			artworkService.save(new Artwork(null, "Obra Contemporánea Bilbao 3", anonimo, 2005,
					"Una pieza innovadora que destaca por su uso experimental de materiales y formas.", null, m25));
			artworkService.save(new Artwork(null, "Obra Contemporánea Bilbao 4", anonimo, 2007,
					"Obra que refleja la transformación cultural y urbana de Bilbao en el siglo XXI.", null, m25));
			artworkService.save(new Artwork(null, "Obra Contemporánea Bilbao 5", anonimo, 2008,
					"Una instalación impactante que desafía las convenciones del arte tradicional.", null, m25));
			artworkService.save(new Artwork(null, "Obra Contemporánea Bilbao 6", anonimo, 2010,
					"Una pintura abstracta que enfatiza el color y la forma en un estilo único.", null, m25));
			artworkService.save(new Artwork(null, "Obra Contemporánea Bilbao 7", anonimo, 2012,
					"Una escultura moderna que juega con la luz y el espacio, representativa del espíritu innovador de Bilbao.",
					null, m25));
			artworkService.save(new Artwork(null, "Obra Contemporánea Bilbao 8", anonimo, 2014,
					"Una obra multimedia que integra video, sonido y escultura para explorar nuevas narrativas visuales.",
					null, m25));
			artworkService.save(new Artwork(null, "Obra Contemporánea Bilbao 9", anonimo, 2016,
					"Una pieza que sintetiza las tendencias más actuales del arte contemporáneo en un formato disruptivo.",
					null, m25));
			artworkService.save(new Artwork(null, "Obra Contemporánea Bilbao #10", anonimo, 2018,
					"Una obra icónica que cierra la colección con una mirada crítica y vanguardista sobre la sociedad actual.",
					null, m25));

			// ==============================
			// Ingresar 20 Usuarios y 1 admin
			// ==============================
			User u1 = new User("ColorTheory", "colortheory@arteum.com", passwordEncoder.encode("art12345"));
			u1.setRoles(List.of("USER"));
			userService.save(u1);

			User u2 = new User("CanvasKing", "canvasking@arteum.com", passwordEncoder.encode("paintme"));
			u2.setRoles(List.of("USER"));
			userService.save(u2);

			User u3 = new User("PastelDreams", "pasteldreams@arteum.com", passwordEncoder.encode("softtones"));
			u3.setRoles(List.of("USER"));
			userService.save(u3);

			User u4 = new User("Brushstroke", "brushstroke@arteum.com", passwordEncoder.encode("brsh789"));
			u4.setRoles(List.of("USER"));
			userService.save(u4);

			User u5 = new User("InkMuse", "inkmuse@arteum.com", passwordEncoder.encode("inkedup"));
			u5.setRoles(List.of("USER"));
			userService.save(u5);

			User u6 = new User("PalettePoet", "palettepoet@arteum.com", passwordEncoder.encode("colors123"));
			u6.setRoles(List.of("USER"));
			userService.save(u6);

			User u7 = new User("GalleryGhost", "galleryghost@arteum.com", passwordEncoder.encode("hauntme"));
			u7.setRoles(List.of("USER"));
			userService.save(u7);

			User u8 = new User("Artoholic", "artoholic@arteum.com", passwordEncoder.encode("obsessed"));
			u8.setRoles(List.of("USER"));
			userService.save(u8);

			User u9 = new User("GoldenRatio", "goldenratio@arteum.com", passwordEncoder.encode("phi314"));
			u9.setRoles(List.of("USER"));
			userService.save(u9);

			User u10 = new User("NeoRenaissance", "neorenaissance@arteum.com", passwordEncoder.encode("rebirth"));
			u10.setRoles(List.of("USER"));
			userService.save(u10);

			User u11 = new User("SurrealQueen", "surrealqueen@arteum.com", passwordEncoder.encode("dreamy22"));
			u11.setRoles(List.of("USER"));
			userService.save(u11);

			User u12 = new User("PixelMonk", "pixelmonk@arteum.com", passwordEncoder.encode("pxl420"));
			u12.setRoles(List.of("USER"));
			userService.save(u12);

			User u13 = new User("SketchLord", "sketchlord@arteum.com", passwordEncoder.encode("scribble"));
			u13.setRoles(List.of("USER"));
			userService.save(u13);

			User u14 = new User("Aesthetician", "aesthetician@arteum.com", passwordEncoder.encode("beauty42"));
			u14.setRoles(List.of("USER"));
			userService.save(u14);

			User u15 = new User("ArtChaser", "artchaser@arteum.com", passwordEncoder.encode("run4art"));
			u15.setRoles(List.of("USER"));
			userService.save(u15);

			User u16 = new User("GraffitiGuru", "graffitiguru@arteum.com", passwordEncoder.encode("spraycan"));
			u16.setRoles(List.of("USER"));
			userService.save(u16);

			User u17 = new User("DoodleWitch", "doodlewitch@arteum.com", passwordEncoder.encode("hexart"));
			u17.setRoles(List.of("USER"));
			userService.save(u17);

			User u18 = new User("VisualVibes", "visualvibes@arteum.com", passwordEncoder.encode("vibes456"));
			u18.setRoles(List.of("USER"));
			userService.save(u18);

			User u19 = new User("MinimalMuse", "minimalmuse@arteum.com", passwordEncoder.encode("simplicity"));
			u19.setRoles(List.of("USER"));
			userService.save(u19);

			User u20 = new User("CeramicSoul", "ceramicsoul@arteum.com", passwordEncoder.encode("claylife"));
			u20.setRoles(List.of("USER"));
			userService.save(u20);

			User admin = new User(userName, userName + ".com", encodedPassword);
			admin.setRoles(List.of("USER", "ADMIN"));
			userService.save(admin);

			// ==============================
			// Ingresar 70 reviews
			// ==============================

			Review review1 = new Review(null, 5, "Impresionante obra maestra, la técnica es sublime.", new Date(),
					userService.findById(1L).orElse(null), artworkService.findById(1L).orElse(null));
			reviewService.save(review1);

			Review review2 = new Review(null, 4, "Me encanta cómo captura la emoción humana.", new Date(),
					userService.findById(1L).orElse(null), artworkService.findById(2L).orElse(null));
			reviewService.save(review2);

			Review review3 = new Review(null, 3, "Interesante, pero me parece un poco oscuro en su simbolismo.",
					new Date(), userService.findById(1L).orElse(null), artworkService.findById(3L).orElse(null));
			reviewService.save(review3);

			Review review4 = new Review(null, 5, "Una obra fascinante que realmente te hace pensar.", new Date(),
					userService.findById(2L).orElse(null), artworkService.findById(4L).orElse(null));
			reviewService.save(review4);

			Review review5 = new Review(null, 4,
					"El detalle en la pintura es impresionante, pero algo sobre el tema me desconcierta.", new Date(),
					userService.findById(2L).orElse(null), artworkService.findById(5L).orElse(null));
			reviewService.save(review5);

			Review review6 = new Review(null, 5, "Una de las mejores esculturas clásicas que he visto. Es perfecta.",
					new Date(), userService.findById(2L).orElse(null), artworkService.findById(6L).orElse(null));
			reviewService.save(review6);

			Review review7 = new Review(null, 4,
					"Una pintura que refleja la angustia humana muy bien, aunque algo surrealista para mi gusto.",
					new Date(), userService.findById(3L).orElse(null), artworkService.findById(7L).orElse(null));
			reviewService.save(review7);

			Review review8 = new Review(null, 3,
					"Creo que el estilo de la obra podría haber sido más fluido, aunque es única en su tipo.",
					new Date(), userService.findById(3L).orElse(null), artworkService.findById(8L).orElse(null));
			reviewService.save(review8);

			Review review9 = new Review(null, 4, "Me transmite mucha paz, pero siento que le falta algo de vida.",
					new Date(), userService.findById(3L).orElse(null), artworkService.findById(9L).orElse(null));
			reviewService.save(review9);

			Review review10 = new Review(null, 5,
					"El uso de los colores en esta obra es brillante. La energía es asombrosa.", new Date(),
					userService.findById(4L).orElse(null), artworkService.findById(10L).orElse(null));
			reviewService.save(review10);

			Review review11 = new Review(null, 4,
					"Es difícil no admirar la visión de esta pintura, pero la historia detrás me impactó aún más.",
					new Date(), userService.findById(4L).orElse(null), artworkService.findById(11L).orElse(null));
			reviewService.save(review11);

			Review review12 = new Review(null, 5,
					"La complejidad del tema y la ejecución son inigualables. Me encantó.", new Date(),
					userService.findById(4L).orElse(null), artworkService.findById(12L).orElse(null));
			reviewService.save(review12);

			Review review13 = new Review(null, 3,
					"Quizás no es mi tipo de arte, pero es impresionante cómo se retrata la tragedia.", new Date(),
					userService.findById(5L).orElse(null), artworkService.findById(13L).orElse(null));
			reviewService.save(review13);

			Review review14 = new Review(null, 5, "El detalle en los rostros es simplemente impresionante. Gran obra.",
					new Date(), userService.findById(5L).orElse(null), artworkService.findById(14L).orElse(null));
			reviewService.save(review14);

			Review review15 = new Review(null, 4,
					"El enfoque clásico y la representación de la luz son magníficos. Aún así, la historia es algo que no me convence del todo.",
					new Date(), userService.findById(5L).orElse(null), artworkService.findById(15L).orElse(null));
			reviewService.save(review15);

			Review review16 = new Review(null, 5, "Simplemente hermoso. El trabajo de sombras y luces es perfecto.",
					new Date(), userService.findById(6L).orElse(null), artworkService.findById(16L).orElse(null));
			reviewService.save(review16);

			Review review17 = new Review(null, 4,
					"Una representación sublime del dolor humano. Es una obra impresionante.", new Date(),
					userService.findById(6L).orElse(null), artworkService.findById(17L).orElse(null));
			reviewService.save(review17);

			Review review18 = new Review(null, 5, "Un clásico absoluto, la obra de arte que definió toda una época.",
					new Date(), userService.findById(6L).orElse(null), artworkService.findById(18L).orElse(null));
			reviewService.save(review18);

			Review review19 = new Review(null, 4,
					"Los colores son excepcionales, pero el mensaje de la obra es lo que realmente me tocó.",
					new Date(), userService.findById(7L).orElse(null), artworkService.findById(19L).orElse(null));
			reviewService.save(review19);

			Review review20 = new Review(null, 5,
					"Esta pintura refleja la soledad de una forma tan profunda. Me encanta.", new Date(),
					userService.findById(7L).orElse(null), artworkService.findById(20L).orElse(null));
			reviewService.save(review20);

			Review review21 = new Review(null, 3,
					"Aunque la obra es famosa, no pude conectarme emocionalmente con ella.", new Date(),
					userService.findById(7L).orElse(null), artworkService.findById(21L).orElse(null));
			reviewService.save(review21);

			Review review22 = new Review(null, 5,
					"Un retrato conmovedor, la luz y sombra transmiten una sensación de esperanza.", new Date(),
					userService.findById(8L).orElse(null), artworkService.findById(22L).orElse(null));
			reviewService.save(review22);

			Review review23 = new Review(null, 4, "La obra tiene una narrativa visual asombrosa. Me gustó mucho.",
					new Date(), userService.findById(8L).orElse(null), artworkService.findById(23L).orElse(null));
			reviewService.save(review23);

			Review review24 = new Review(null, 5,
					"Una obra maestra que sigue siendo tan relevante hoy como cuando fue creada.", new Date(),
					userService.findById(8L).orElse(null), artworkService.findById(24L).orElse(null));
			reviewService.save(review24);

			Review review25 = new Review(null, 5,
					"Una pintura profundamente emotiva. El uso del espacio y la luz es magistral.", new Date(),
					userService.findById(9L).orElse(null), artworkService.findById(25L).orElse(null));
			reviewService.save(review25);

			Review review26 = new Review(null, 4,
					"La obra es intrigante, pero su simbolismo me deja un poco pensativo.", new Date(),
					userService.findById(9L).orElse(null), artworkService.findById(26L).orElse(null));
			reviewService.save(review26);

			Review review27 = new Review(null, 5,
					"El contraste entre lo antiguo y lo moderno en esta obra es impresionante.", new Date(),
					userService.findById(9L).orElse(null), artworkService.findById(27L).orElse(null));
			reviewService.save(review27);

			Review review28 = new Review(null, 4, "Una obra con mucha historia y un mensaje poderoso.", new Date(),
					userService.findById(10L).orElse(null), artworkService.findById(28L).orElse(null));
			reviewService.save(review28);

			Review review29 = new Review(null, 5,
					"La manera en que representa la historia es impresionante. Realmente conmovedora.", new Date(),
					userService.findById(10L).orElse(null), artworkService.findById(29L).orElse(null));
			reviewService.save(review29);

			Review review30 = new Review(null, 4,
					"Es una obra difícil de entender, pero una vez que lo haces, se vuelve tan poderosa.", new Date(),
					userService.findById(10L).orElse(null), artworkService.findById(30L).orElse(null));
			reviewService.save(review30);

			Review review31 = new Review(null, 5,
					"El arte detrás de esta obra es simplemente épico. Su historia me ha tocado profundamente.",
					new Date(), userService.findById(11L).orElse(null), artworkService.findById(31L).orElse(null));
			reviewService.save(review31);

			Review review32 = new Review(null, 4,
					"Es una obra fascinante, aunque algunas veces me siento un poco distante de ella.", new Date(),
					userService.findById(11L).orElse(null), artworkService.findById(32L).orElse(null));
			reviewService.save(review32);

			Review review33 = new Review(null, 5, "Una obra increíblemente poderosa, la mezcla de colores me atrapa.",
					new Date(), userService.findById(11L).orElse(null), artworkService.findById(33L).orElse(null));
			reviewService.save(review33);

			Review review34 = new Review(null, 4,
					"El simbolismo aquí es muy interesante, aunque hay elementos que podrían haberse tratado de otra manera.",
					new Date(), userService.findById(12L).orElse(null), artworkService.findById(34L).orElse(null));
			reviewService.save(review34);

			Review review35 = new Review(null, 5,
					"Definitivamente una de las piezas más hermosas que he visto. La luz en esta obra es perfecta.",
					new Date(), userService.findById(12L).orElse(null), artworkService.findById(35L).orElse(null));
			reviewService.save(review35);

			Review review36 = new Review(null, 3,
					"Creo que la obra tiene mucho simbolismo que no logro comprender del todo.", new Date(),
					userService.findById(12L).orElse(null), artworkService.findById(36L).orElse(null));
			reviewService.save(review36);

			Review review37 = new Review(null, 4,
					"El contraste entre el fondo y el sujeto es excelente, pero su interpretación es algo ambigua.",
					new Date(), userService.findById(13L).orElse(null), artworkService.findById(37L).orElse(null));
			reviewService.save(review37);

			Review review38 = new Review(null, 5, "La belleza de esta pieza es atemporal. Nunca dejará de asombrarme.",
					new Date(), userService.findById(13L).orElse(null), artworkService.findById(38L).orElse(null));
			reviewService.save(review38);

			Review review39 = new Review(null, 4,
					"Excelente representación de la historia, aunque creo que podría haber más interacción en la escena.",
					new Date(), userService.findById(13L).orElse(null), artworkService.findById(39L).orElse(null));
			reviewService.save(review39);

			Review review40 = new Review(null, 5,
					"Lo que más me impresiona es la atmósfera que logra crear con el uso de los colores.", new Date(),
					userService.findById(14L).orElse(null), artworkService.findById(40L).orElse(null));
			reviewService.save(review40);

			Review review41 = new Review(null, 4,
					"Esta obra me deja reflexionando sobre el paso del tiempo, la técnica utilizada es brillante.",
					new Date(), userService.findById(14L).orElse(null), artworkService.findById(41L).orElse(null));
			reviewService.save(review41);

			Review review42 = new Review(null, 5,
					"Una obra absolutamente conmovedora. Cada vez que la veo encuentro nuevos detalles.", new Date(),
					userService.findById(14L).orElse(null), artworkService.findById(42L).orElse(null));
			reviewService.save(review42);

			Review review43 = new Review(null, 3,
					"A pesar de ser una pieza muy famosa, no me siento tan conectado con la obra.", new Date(),
					userService.findById(15L).orElse(null), artworkService.findById(43L).orElse(null));
			reviewService.save(review43);

			Review review44 = new Review(null, 5, "Impresionante, realmente transmite una sensación de grandeza.",
					new Date(), userService.findById(15L).orElse(null), artworkService.findById(44L).orElse(null));
			reviewService.save(review44);

			Review review45 = new Review(null, 4,
					"La obra es bellísima, pero su mensaje es tan profundo que puede ser difícil de captar al principio.",
					new Date(), userService.findById(15L).orElse(null), artworkService.findById(45L).orElse(null));
			reviewService.save(review45);

			Review review46 = new Review(null, 5,
					"No puedo dejar de admirar esta pintura. Cada pincelada parece estar llena de significado.",
					new Date(), userService.findById(16L).orElse(null), artworkService.findById(46L).orElse(null));
			reviewService.save(review46);

			Review review47 = new Review(null, 4,
					"La obra es intrigante, y su historia detrás la hace aún más fascinante.", new Date(),
					userService.findById(16L).orElse(null), artworkService.findById(47L).orElse(null));
			reviewService.save(review47);

			Review review48 = new Review(null, 5,
					"Una obra profundamente emocional, logra capturar la esencia de la fragilidad humana.", new Date(),
					userService.findById(16L).orElse(null), artworkService.findById(48L).orElse(null));
			reviewService.save(review48);

			Review review49 = new Review(null, 3,
					"Aunque es una obra famosa, no logro encontrarle todo el sentido que muchos le atribuyen.",
					new Date(), userService.findById(17L).orElse(null), artworkService.findById(49L).orElse(null));
			reviewService.save(review49);

			Review review50 = new Review(null, 5,
					"¡Simplemente magnífico! La combinación de colores y la forma en que se presenta la emoción es brillante.",
					new Date(), userService.findById(17L).orElse(null), artworkService.findById(50L).orElse(null));
			reviewService.save(review50);

			Review review51 = new Review(null, 4,
					"Una pintura que nunca deja de sorprenderme. Su profundidad y significado son realmente extraordinarios.",
					new Date(), userService.findById(17L).orElse(null), artworkService.findById(51L).orElse(null));
			reviewService.save(review51);

			Review review52 = new Review(null, 5,
					"Una obra que destila paz, belleza y complejidad. Me hace pensar en la eternidad.", new Date(),
					userService.findById(18L).orElse(null), artworkService.findById(52L).orElse(null));
			reviewService.save(review52);

			Review review53 = new Review(null, 3,
					"No la entendí completamente, pero es un trabajo visualmente fascinante.", new Date(),
					userService.findById(18L).orElse(null), artworkService.findById(53L).orElse(null));
			reviewService.save(review53);

			Review review54 = new Review(null, 5,
					"Es fascinante cómo el autor logra plasmar tantas emociones con tan pocos elementos.", new Date(),
					userService.findById(18L).orElse(null), artworkService.findById(54L).orElse(null));
			reviewService.save(review54);

			Review review55 = new Review(null, 4,
					"El uso de la luz en esta pintura es perfecto, pero siento que la obra podría ser más dinámica.",
					new Date(), userService.findById(19L).orElse(null), artworkService.findById(55L).orElse(null));
			reviewService.save(review55);

			Review review56 = new Review(null, 5,
					"La forma en que esta pintura captura el alma humana es impresionante.", new Date(),
					userService.findById(19L).orElse(null), artworkService.findById(56L).orElse(null));
			reviewService.save(review56);

			Review review57 = new Review(null, 4,
					"Una obra que va más allá de lo visual. Una vez que entiendes su mensaje, se vuelve inolvidable.",
					new Date(), userService.findById(19L).orElse(null), artworkService.findById(57L).orElse(null));
			reviewService.save(review57);

			Review review58 = new Review(null, 5,
					"La narrativa visual aquí es muy potente, te conecta de inmediato con la obra.", new Date(),
					userService.findById(20L).orElse(null), artworkService.findById(58L).orElse(null));
			reviewService.save(review58);

			Review review59 = new Review(null, 3,
					"No fue fácil para mí entender la obra, aunque reconozco su grandeza técnica.", new Date(),
					userService.findById(20L).orElse(null), artworkService.findById(59L).orElse(null));
			reviewService.save(review59);

			Review review60 = new Review(null, 4, "Una obra que siempre me hace pensar en lo efímero de la vida.",
					new Date(), userService.findById(20L).orElse(null), artworkService.findById(60L).orElse(null));
			reviewService.save(review60);

			Review review61 = new Review(null, 5,
					"Una de las obras más impactantes que he visto. La técnica empleada es increíble.", new Date(),
					userService.findById(1L).orElse(null), artworkService.findById(61L).orElse(null));
			reviewService.save(review61);

			Review review62 = new Review(null, 4,
					"Me encanta la complejidad de la obra, pero a veces resulta difícil interpretar sus significados.",
					new Date(), userService.findById(1L).orElse(null), artworkService.findById(62L).orElse(null));
			reviewService.save(review62);

			Review review63 = new Review(null, 5,
					"La armonía de los colores es algo fuera de lo común. Una obra maestra sin duda.", new Date(),
					userService.findById(1L).orElse(null), artworkService.findById(63L).orElse(null));
			reviewService.save(review63);

			Review review64 = new Review(null, 4,
					"Una pintura cautivadora, pero creo que algunas de las ideas subyacentes podrían explicarse mejor.",
					new Date(), userService.findById(2L).orElse(null), artworkService.findById(64L).orElse(null));
			reviewService.save(review64);

			Review review65 = new Review(null, 5,
					"Es una obra que te deja sin palabras. Todo en ella transmite fuerza y serenidad al mismo tiempo.",
					new Date(), userService.findById(2L).orElse(null), artworkService.findById(65L).orElse(null));
			reviewService.save(review65);

			Review review66 = new Review(null, 3,
					"Aunque la obra tiene un gran valor histórico, no logré sentir una conexión profunda con ella.",
					new Date(), userService.findById(2L).orElse(null), artworkService.findById(66L).orElse(null));
			reviewService.save(review66);

			Review review67 = new Review(null, 5,
					"¡Una de mis obras favoritas! La manera en que está pintada muestra una emoción cruda y real.",
					new Date(), userService.findById(3L).orElse(null), artworkService.findById(67L).orElse(null));
			reviewService.save(review67);

			Review review68 = new Review(null, 4,
					"Una obra llena de significado, aunque su estilo puede no ser del gusto de todos.", new Date(),
					userService.findById(3L).orElse(null), artworkService.findById(68L).orElse(null));
			reviewService.save(review68);

			Review review69 = new Review(null, 5,
					"Cada vez que la miro, descubro algo nuevo. Es como un enigma visual.", new Date(),
					userService.findById(3L).orElse(null), artworkService.findById(69L).orElse(null));
			reviewService.save(review69);

			Review review70 = new Review(null, 4,
					"El estilo visual es impresionante, pero creo que la historia detrás podría ser más clara.",
					new Date(), userService.findById(4L).orElse(null), artworkService.findById(70L).orElse(null));
			reviewService.save(review70);

			Review review71 = new Review(null, 5,
					"Increíble, la manera en que se utiliza la luz es pura magia. La obra realmente cuenta una historia.",
					new Date(), userService.findById(4L).orElse(null), artworkService.findById(71L).orElse(null));
			reviewService.save(review71);

			Review review72 = new Review(null, 3,
					"Aunque la obra tiene una gran técnica, no me conecta emocionalmente como otras.", new Date(),
					userService.findById(4L).orElse(null), artworkService.findById(72L).orElse(null));
			reviewService.save(review72);

			Review review73 = new Review(null, 5,
					"Esta obra transmite una profundidad increíble. La forma en que el autor transmite sentimientos es única.",
					new Date(), userService.findById(5L).orElse(null), artworkService.findById(73L).orElse(null));
			reviewService.save(review73);

			Review review74 = new Review(null, 4,
					"Una obra impactante, aunque algunas partes me parecen algo forzadas. Aun así, me impresiona mucho.",
					new Date(), userService.findById(5L).orElse(null), artworkService.findById(74L).orElse(null));
			reviewService.save(review74);

			Review review75 = new Review(null, 5,
					"Una de las obras más poderosas de todos los tiempos. La complejidad y la emoción que transmite son indescriptibles.",
					new Date(), userService.findById(5L).orElse(null), artworkService.findById(75L).orElse(null));
			reviewService.save(review75);

			// ==============================
			// Flush
			// ==============================
			userService.flush();
			artworkService.flush();
			reviewService.flush();
			museumService.flush();

			// ====================================
			// Asignar seguidores y obras favoritas
			// ====================================
			assignFollowersAndFavorites();

			// ====================================
			// Asignar reviews automaticas
			// ====================================
			assignReviewsForFirst20Users();

			// ====================================
			// Actualizar ratings de obras
			// ====================================
			updateAllArtworkAverageRatings();
			
			// ==============================
			// Flush
			// ==============================
			userService.flush();
			artworkService.flush();
			reviewService.flush();
			museumService.flush();
			reviewService.flush();
		}
	}
	
	@Transactional
	public void assignFollowersAndFavorites() {
	    List<User> users = userService.findAll();
	    users.removeIf(user -> user.getRoles() != null && user.getRoles().contains("ADMIN"));

	    // Paso 1: guardar usuarios sin relaciones
	    userService.saveAll(users); // asegura que están gestionados

	    List<Artwork> artworks = artworkService.findAll();
	    Random random = new Random();

	    // Paso 2: agregar relaciones
	    for (User user : users) {
	        List<User> candidates = new ArrayList<>(users);
	        candidates.remove(user);
	        Collections.shuffle(candidates, random);

	        int numFollowers = candidates.isEmpty() ? 0 : random.nextInt(candidates.size()) + 1;
	        List<User> chosen = candidates.subList(0, numFollowers);

	        user.getFollowing().addAll(chosen);
	        for (User follower : chosen) {
	            follower.getFollowers().add(user);
	        }

	        // También favoritos
	        List<Artwork> shuffled = new ArrayList<>(artworks);
	        Collections.shuffle(shuffled, random);
	        int numFavorites = Math.min(random.nextInt(5) + 3, artworks.size());
	        user.getFavoriteArtworks().addAll(shuffled.subList(0, numFavorites));
	    }

	    // Paso 3: guardar relaciones
	    userService.saveAll(users);
	}

	
	@Transactional
	public void updateAllArtworkAverageRatings() {
		// Obtener todas las obras de la base de datos
		List<Artwork> artworks = artworkService.findAll();

		// Actualizar el promedio de cada obra
		for (Artwork artwork : artworks) {
			artworkService.updateAverageRating(artwork);
		}
	}
	
	@Transactional
	public void assignReviewsForFirst20Users() {
		// Obtiene la lista de todos los usuarios y obras
		List<User> users = userService.findAll();
		List<Artwork> artworks = artworkService.findAll();

		// Selecciona los primeros 20 usuarios (o todos si hay menos de 20)
		List<User> first20Users = users.size() > 20 ? users.subList(0, 20) : new ArrayList<>(users);

		// Lista para acumular todas las reviews
		List<Review> reviews = new ArrayList<>();

		Random random = new Random();

		// Genera una review para cada obra de cada uno de los primeros 20 usuarios
		for (User user : first20Users) {
			for (Artwork artwork : artworks) {
				// Genera un rating aleatorio entre 0.5 y 5.0 en incrementos de 0.5
				double rating = (random.nextInt(5) + 6) * 0.5;

				// Genera un comentario simple
				String comment = "";

				// Crear la review
				Review review = new Review();
				review.setRating(rating);
				review.setComment(comment);
				review.setCreatedAt(new Date());
				review.setUser(user);
				review.setArtwork(artwork);

				reviews.add(review);
			}
		}

		// Guardar todas las reviews de una sola vez
		reviewService.saveAll(reviews);
	}
	
	private static Date parseDate(String dateStr) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

}