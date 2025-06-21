package api;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;

@SpringBootTest(classes = app.Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArtistApiTest {

	@LocalServerPort
	private int port;
	
	@BeforeEach
    void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = port;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    @DisplayName("GET /api/v1/artists - should return paginated list of artists")
    void testGetArtistsList() {
        given()
            .queryParam("page", 1)
            .queryParam("size", 5)
        .when()
            .get("/api/v1/artists")
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("GET /api/v1/artists/{id} - should return artist details")
    void testGetArtistById() {
        given()
        .when()
            .get("/api/v1/artists/1")
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("GET /api/v1/artists/top-rated - should return top 10 artists")
    void testGetTopRatedArtists() {
        given()
        .when()
            .get("/api/v1/artists/top-rated")
        .then()
            .statusCode(200);
    }
}
