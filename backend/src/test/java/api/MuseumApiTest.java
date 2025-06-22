package api;

import static io.restassured.RestAssured.given;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SpringBootTest(classes = app.Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = { OAuth2ClientWebSecurityAutoConfiguration.class,
		Saml2RelyingPartyAutoConfiguration.class })
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
