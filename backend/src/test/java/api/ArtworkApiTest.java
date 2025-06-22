package api;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@SpringBootTest(classes = app.Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArtworkApiTest {
	
	@LocalServerPort
	private int port;
	
	@BeforeEach
    void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = port;
        RestAssured.useRelaxedHTTPSValidation();
    }

	@Test
	@DisplayName("GET /api/v1/artworks - should return status 200 and non-empty list")
	void testGetAllArtworks() {
		Response response = given().accept(ContentType.JSON).when().get("/api/v1/artworks").then().statusCode(200)
				.contentType(ContentType.JSON).extract().response();

		// Extract the 'content' array inside the JSON response
		List<?> artworks = response.jsonPath().getList("content");

		assertNotNull(artworks, "Artworks content list should not be null");
		assertFalse(artworks.isEmpty(), "Artworks content list should not be empty");
	}

	@Test
	@DisplayName("GET /api/v1/artworks/1 - should return specific artwork with title")
	void testGetArtworkById() {
		Response response = given().accept(ContentType.JSON).when().get("/api/v1/artworks/1").then().statusCode(200)
				.contentType(ContentType.JSON).extract().response();

		// Validate response fields
		String title = response.jsonPath().getString("title");
		Integer id = response.jsonPath().getInt("id");

		assertNotNull(id, "Artwork ID should not be null");
		assertEquals(1, id, "Artwork ID should be 1");
		assertNotNull(title, "Artwork title should not be null or empty");
		assertFalse(title.trim().isEmpty(), "Artwork title should not be empty");
	}

	@Test
	@DisplayName("GET /api/v1/artworks/image/1 - should return image or fallback")
	void testGetArtworkImage() {
		Response response = given().when().get("/api/v1/artworks/image/1").then().statusCode(200).extract().response();

		String contentType = response.getContentType();
		assertTrue(contentType.equals("image/jpeg") || contentType.equals("image/png"),
				"Content-Type should be image/jpeg or image/png");
		assertTrue(response.getBody().asByteArray().length > 0, "Image bytes should not be empty");
	}

	@Test
	@DisplayName("GET /api/v1/artworks/top-rated - should return top 7 artworks")
	void testGetTopRatedArtworks() {
		Response response = given().accept(ContentType.JSON).when().get("/api/v1/artworks/top-rated").then()
				.statusCode(200).extract().response();

		List<?> topRated = response.jsonPath().getList("$");
		assertNotNull(topRated, "Top rated list should not be null");
		assertTrue(topRated.size() <= 7, "Should return up to 7 artworks");
	}

	@Test
	@DisplayName("GET /api/v1/artworks/random - should return 7 random artworks")
	void testGetRandomArtworks() {
		Response response = given().accept(ContentType.JSON).when().get("/api/v1/artworks/random").then()
				.statusCode(200).extract().response();

		List<?> random = response.jsonPath().getList("$");
		assertNotNull(random, "Random list should not be null");
		assertEquals(7, random.size(), "Should return exactly 7 random artworks");
	}
}
