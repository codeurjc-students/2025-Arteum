package api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = { app.Application.class, app.config.TestDataInitializer.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class ReviewApiTest {

    private String accessToken;
    private String username;
    private Integer artworkId = 1;
    private Long reviewId;

    @LocalServerPort
	private int port;
	
	@BeforeEach
    void setup() {
		RestAssured.baseURI = "https://localhost";
        RestAssured.port = port;
        RestAssured.useRelaxedHTTPSValidation();
        
        username = "testUser" + System.currentTimeMillis();
        String password = "TestPass123!";
        String email = username + "@example.com";

        if (accessToken == null) {
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
        }
    }

    @Test
    @Order(1)
    @DisplayName("1) Create review")
    void testCreateReview() {
        String reviewBody = """
            {
                "artworkId": %d,
                "rating": 5.0,
                "comment": "Amazing artwork!"
            }
        """.formatted(artworkId);

        Response createResp = given()
            .cookie("AuthToken", accessToken)
            .contentType(ContentType.JSON)
            .body(reviewBody)
        .when()
            .post("/api/v1/reviews");

        createResp.then()
            .statusCode(201);

        String location = createResp.getHeader("Location");
        reviewId = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        Assertions.assertNotNull(reviewId, "Review ID should be extracted from Location header");
    }

    @Test
    @Order(2)
    @DisplayName("2) Edit review")
    void testEditReview() {
        Assertions.assertNotNull(reviewId, "Review ID must not be null before editing");

        String updateBody = """
            {
                "artworkId": %d,
                "rating": 4.0,
                "comment": "Updated comment"
            }
        """.formatted(artworkId);

        given()
            .cookie("AuthToken", accessToken)
            .contentType(ContentType.JSON)
            .body(updateBody)
        .when()
            .put("/api/v1/reviews/" + reviewId)
        .then()
            .statusCode(200)
            .body(equalTo("Review actualizada"));
    }

    @Test
    @Order(3)
    @DisplayName("3) Get review by user and artwork")
    void testGetReviewByUserAndArtwork() {
        Assertions.assertNotNull(reviewId, "Review ID must not be null before fetching");

        given()
            .cookie("AuthToken", accessToken)
        .when()
            .get("/api/v1/reviews/" + reviewId)
        .then()
            .statusCode(200)
            .body("rating", equalTo(4.0F))
            .body("comment", equalTo("Updated comment"));
    }

    @Test
    @Order(4)
    @DisplayName("4) Delete review")
    void testDeleteReview() {
        Assertions.assertNotNull(reviewId, "Review ID must not be null before deletion");

        given()
            .cookie("AuthToken", accessToken)
        .when()
            .delete("/api/v1/reviews/" + reviewId)
        .then()
            .statusCode(204);
    }
}
