
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import io.qameta.allure.Step;


import java.util.UUID;

import static io.restassured.RestAssured.given;


public class CheckLoginCourierTest {

    private static final String LOGIN = String.format("ninja%s", UUID.randomUUID());
    private static final String PASSWORD = UUID.randomUUID().toString();

    @BeforeClass
    public static void setUp() {
        String requestBody = String.format(
                "{ \"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\" }", LOGIN, PASSWORD, LOGIN
        );
        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(Constants.BASE_COURIER_URL);
    }


    @Test
    @Step("Проверка успешной авторизации курьера")
    public void testCourierLogin_Success() {
        String requestBody = String.format("{ \"login\": \"%s\", \"password\": \"%s\" }", LOGIN, PASSWORD);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(Constants.BASE_LOGIN_URL);

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        Assert.assertNotNull(response.jsonPath().get("id"));
    }

    @Test
    @Step("Проверка авторизации с пропущенными полями")
    public void testCourierLogin_MissingFields() {
        // Попытка входа без указания всех обязательных полей
        String requestBody = "{ \"login\": \"ninja\", \"password\": \"\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(Constants.BASE_LOGIN_URL);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals("Недостаточно данных для входа", response.jsonPath().getString("message"));
    }

    @Test
    @Step("Попытка входа с неверными учётными данными")
    public void testCourierLogin_IncorrectCredentials() {
        // Попытка входа с неверными учетными данными
        String requestBody = "{ \"login\": \"ninja\", \"password\": \"wrongPassword\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(Constants.BASE_LOGIN_URL);

        Assert.assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusCode());
        Assert.assertEquals("Учетная запись не найдена", response.jsonPath().getString("message"));
    }
}
