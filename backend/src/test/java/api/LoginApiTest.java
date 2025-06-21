package api;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
class LoginApiTest {

	@LocalServerPort
	private int port;
	
	@BeforeEach
    void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = port;
        RestAssured.useRelaxedHTTPSValidation();
    }

	@Test
	@DisplayName("POST /api/v1/auth/login and logout - should complete successfully")
	void testLoginAndLogoutSuccess() {
	    Response loginResponse = given()
	        .contentType(ContentType.JSON)
	        .body("{ \"username\": \"admin\", \"password\": \"admin\" }")
	    .when()
	        .post("/api/v1/auth/login")
	    .then()
	        .statusCode(200)
	        .body("status", equalTo("SUCCESS"))
	        .body("message", not(emptyOrNullString()))
	        .extract().response();

	    String accessToken = loginResponse.getCookie("accessToken");

	    given()
	        .cookie("accessToken", accessToken)
	    .when()
	        .post("/api/v1/auth/logout")
	    .then()
	        .statusCode(200);
	}

	@Test
	@DisplayName("POST /api/v1/auth/login - should fail with invalid credentials")
	void testLoginFailure() {
		given().contentType(ContentType.JSON).body("{ \"username\": \"fakeuser\", \"password\": \"wrongpass\" }").when()
				.post("/api/v1/auth/login").then().statusCode(200).body("status", equalTo("FAILURE"))
				.body("message", containsString("Invalid credentials"));
	}

	@Test
	@DisplayName("POST /api/v1/auth/refresh - should fail or return refreshed token (mocked token)")
	void testRefreshToken() {
		given().cookie("refreshToken", "fake-refresh-token").when().post("/api/v1/auth/refresh").then()
				.statusCode(anyOf(equalTo(200), equalTo(401)))
				.body("status", anyOf(equalTo("FAILURE"), equalTo("SUCCESS")));
	}

	@Test
	@DisplayName("POST /api/v1/auth/logout - should return failure when not logged in")
	void testLogoutWithoutSession() {
		when().post("/api/v1/auth/logout").then().statusCode(200).body("status", equalTo("FAILURE")).body("message",
				containsString("not logged"));
	}
}
