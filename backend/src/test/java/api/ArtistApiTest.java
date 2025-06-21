package api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeAll;
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
            .statusCode(200)
            .body("content", notNullValue())
            .body("page", equalTo(1))
            .body("size", equalTo(5));
    }

    @Test
    @DisplayName("GET /api/v1/artists/{id} - should return artist details")
    void testGetArtistById() {
        given()
        .when()
            .get("/api/v1/artists/1")
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("name", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("GET /api/v1/artists/image/{id} - should return artist image or placeholder")
    void testGetArtistImage() {
        given()
        .when()
            .get("/api/v1/artists/image/1")
        .then()
            .statusCode(200)
            .contentType(anyOf(equalTo("image/jpeg"), equalTo("image/png")))
            .header("Content-Length", notNullValue());
    }

    @Test
    @DisplayName("GET /api/v1/artists/top-rated - should return top 10 artists")
    void testGetTopRatedArtists() {
        given()
        .when()
            .get("/api/v1/artists/top-rated")
        .then()
            .statusCode(200)
            .body("size()", lessThanOrEqualTo(10))
            .body("[0].name", not(emptyOrNullString()));
    }
}
