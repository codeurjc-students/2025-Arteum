package api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

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
public class UserApiTest {

    private String accessToken;
    private String username;
    private String testPassword;
    private String secondUsername = "PastelDreams";

    @LocalServerPort
	private int port;
	
	@BeforeEach
    void setupRA() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = port;
        RestAssured.useRelaxedHTTPSValidation();

        if (accessToken == null) {
	        username = "testUser" + System.currentTimeMillis();
	        testPassword = "TestPass123!";
	        String email = username + "@example.com";
	
	        // 1) Register
	        String registerBody = """
	            {
	                "username": "%s",
	                "email": "%s",
	                "password": "%s",
	                "confirmPassword": "%s"
	            }
	        """.formatted(username, email, testPassword, testPassword);
	
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
	        """.formatted(username, testPassword);
	
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
    @DisplayName("1) GET /users – paginated list with authentication")
    void testGetUsersList() {
        given()
        	.cookie("AuthToken", accessToken)
            .queryParam("page", 1)
            .queryParam("size", 5)
            .queryParam("search", username)
            .queryParam("sort", "createdAt")
        .when()
            .get("/api/v1/users")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(2)
    @DisplayName("2) GET /users/public-profile/{username} – own profile should return 404 for admin, 200 for user")
    void testGetPublicProfile() {
        // As this user has no ADMIN role, we expect 200
        given()
        	.cookie("AuthToken", accessToken)
        .when()
            .get("/api/v1/users/public-profile/" + username)
        .then()
            .statusCode(200);
    }

    @Test
    @Order(3)
    @DisplayName("3) GET /users/recommended-artworks – should return OK")
    void testGetRecommendedArtworks() {
        given()
        	.cookie("AuthToken", accessToken)
        .when()
            .get("/api/v1/users/recommended-artworks")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", instanceOf(java.util.List.class));
    }

    @Test
    @Order(4)
    @DisplayName("4) GET /users/pdf – should return PDF")
    void testDownloadUserPdf() {
        given()
        	.cookie("AuthToken", accessToken)
            .accept("application/pdf")
        .when()
            .get("/api/v1/users/pdf")
        .then()
            .statusCode(200)
            .contentType("application/pdf")
            .header("Content-Disposition", containsString("attachment; filename=usuario_" + username));
    }
    
    @Test
    @Order(5)
    @DisplayName("5) GET /users/stats – authenticated user statistics")
    void testGetUserStats() {
        given()
            .cookie("AuthToken", accessToken)
        .when()
            .get("/api/v1/users/stats")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(6)
    @DisplayName("6) GET /users/reviews – authenticated user reviews paginated")
    void testGetUserReviews() {
        given()
            .cookie("AuthToken", accessToken)
            .queryParam("page", 1)
            .queryParam("size", 5)
            .queryParam("sort", "createdAt")
        .when()
            .get("/api/v1/users/reviews")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(7)
    @DisplayName("7) GET /users/following – authenticated user following list paginated")
    void testGetUserFollowing() {
        given()
            .cookie("AuthToken", accessToken)
            .queryParam("page", 1)
            .queryParam("size", 5)
            .queryParam("sort", "nameAsc")
        .when()
            .get("/api/v1/users/following")
        .then()
            .statusCode(200);
    }

    @Test
    @Order(8)
    @DisplayName("8) GET /users/followers – authenticated user followers list paginated")
    void testGetUserFollowers() {
        given()
            .cookie("AuthToken", accessToken)
            .queryParam("page", 1)
            .queryParam("size", 5)
            .queryParam("sort", "nameDesc")
        .when()
            .get("/api/v1/users/followers")
        .then()
            .statusCode(200);
    }
    
    @Test
    @Order(9)
    @DisplayName("9) POST /users/follow/{username} – authenticated follows another user")
    void testFollowUser() {
        given()
            .cookie("AuthToken", accessToken)
        .when()
            .post("/api/v1/users/follow/" + secondUsername)
        .then()
            .statusCode(200);
    }

    @Test
    @Order(10)
    @DisplayName("10) POST /users/unfollow/{username} – authenticated unfollows the user")
    void testUnfollowUser() {
        given()
            .cookie("AuthToken", accessToken)
        .when()
            .post("/api/v1/users/unfollow/" + secondUsername)
        .then()
            .statusCode(200)
            .body("message", equalTo("Usuario dejado de seguir exitosamente"));
    }

    @Test
    @Order(11)
    @DisplayName("11) GET /users/favourites-artworks – paginated favorites list")
    void testGetFavouritesArtworks() {
        given()
            .cookie("AuthToken", accessToken)
            .queryParam("page", 1)
            .queryParam("size", 5)
            .queryParam("sort", "title")
        .when()
            .get("/api/v1/users/favourites-artworks")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("content", instanceOf(java.util.List.class))
            .body("pageable.pageNumber", equalTo(0))
            .body("pageable.pageSize", equalTo(5));
    }

    @Test
    @Order(12)
    @DisplayName("12) POST /users/image – upload and delete profile image")
    void testUploadAndDeleteUserImage() {
        // 14a) upload image
        given()
            .cookie("AuthToken", accessToken)
            .multiPart("image", new java.io.File("src/test/resources/user1.jpg"))
        .when()
            .post("/api/v1/users/image")
        .then()
            .statusCode(200);

        // 14b) delete image
        given()
            .cookie("AuthToken", accessToken)
        .when()
            .delete("/api/v1/users/image")
        .then()
            .statusCode(200);
    }

}
