package api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(classes = app.Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MuseumApiTest {

	@LocalServerPort
	private int port;
	
	@BeforeEach
    void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = port;
        RestAssured.useRelaxedHTTPSValidation();
    }
	
    @Test
    void testGetMuseumsPage() {
        given()
            .queryParam("page", 1)
            .queryParam("size", 5)
            .queryParam("sort", "name")
            .queryParam("search", "art")
        .when()
            .get("/api/v1/museums")
        .then()
            .statusCode(200);
    }

    @Test
    void testGetMuseumByIdFound() {
        given()
        .when()
            .get("/api/v1/museums/1")
        .then()
            .statusCode(200);
    }

    @Test
    void testGetMuseumByIdNotFound() {
        given()
        .when()
            .get("/api/v1/museums/999999")
        .then()
            .statusCode(404);
    }

    @Test
    void testGetMuseumImageFoundOrDefault() throws IOException {
        Response response = given()
        .when()
            .get("/api/v1/museums/image/1");

        response.then()
            .statusCode(200);
    }
}
