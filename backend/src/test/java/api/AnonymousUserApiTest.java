package api;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class AnonymousUserApiTest {

	@LocalServerPort
	private int port;
	
	@BeforeEach
    void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = port;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    @DisplayName("POST /api/v1/register - should register user successfully")
    void testRegisterSuccess() {
    	String username = "testUser" + System.currentTimeMillis();
        String password = "TestPass123!";
        String email = username + "@example.com";
        String accessToken;
        
        // 1) Register
        String registerBody = """
            {
                "username": "%s",
                "email": "%s",
                "password": "%s",
                "confirmPassword": "%s"
            }
        """.formatted(username, email, password, password);

        given()
            .contentType(ContentType.JSON)
            .body(registerBody)
        .when()
            .post("/api/v1/register")
        .then()
            .statusCode(201);

        // 2) Login
        String loginBody = """
            {
                "username": "%s",
                "password": "%s"
            }
        """.formatted(username, password);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(loginBody)
        .when()
            .post("/api/v1/auth/login");

        response.then().statusCode(200);
        accessToken = response.getCookie("AuthToken");
        Assertions.assertNotNull(accessToken, "AuthToken cookie should not be null");
        
        if (accessToken != null) {
            given()
                .cookie("AuthToken", accessToken)
            .when()
                .delete("/api/v1/users/me")
            .then()
                .statusCode(204);
        }
    }

    @Test
    @DisplayName("POST /api/v1/register - should fail when passwords do not match")
    void testRegisterPasswordMismatch() {
        given()
            .contentType(ContentType.JSON)
            .body("{"
                + "\"username\": \"user1\","
                + "\"email\": \"user1@example.com\","
                + "\"password\": \"abc123\","
                + "\"confirmPassword\": \"def456\""
                + "}")
        .when()
            .post("/api/v1/register")
        .then()
            .statusCode(400)
            .body(containsString("Las contraseñas no coinciden"));
    }

    @Test
    @DisplayName("POST /api/v1/register - should fail when required fields are blank")
    void testRegisterMissingFields() {
        given()
            .contentType(ContentType.JSON)
            .body("{"
                + "\"username\": \"\","
                + "\"email\": \"\","
                + "\"password\": \"\","
                + "\"confirmPassword\": \"\""
                + "}")
        .when()
            .post("/api/v1/register")
        .then()
            .statusCode(400)
            .body(containsString("Faltan campos obligatorios"));
    }
}
