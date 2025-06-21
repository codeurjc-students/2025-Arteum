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
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class MuseumApiTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://localhost";
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
            .statusCode(200)
            .body("content", not(empty()))
            .body("content[0].name", notNullValue())
            .body("totalElements", greaterThanOrEqualTo(0));
    }

    @Test
    void testGetMuseumByIdFound() {
        given()
        .when()
            .get("/api/v1/museums/1")
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("name", notNullValue());
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
            .statusCode(200)
            .header("Content-Type", anyOf(equalTo("image/jpeg"), equalTo("image/png")));

        byte[] imageBytes = response.getBody().asByteArray();
        assert(imageBytes.length > 0);
    }
}
