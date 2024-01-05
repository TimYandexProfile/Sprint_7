import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Step;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class CreateCourierTest {

    private String login;
    private String courierId; // Поле для хранения id курьера

    @Before
    public void setUp() {
        UUID uuid = UUID.randomUUID();
        login = String.format("ninja%s", uuid);
    }

    @Test
    @Step("Успешное создание курьера")
    public void testCreateCourier_Success() {
        String requestBody = String.format("{ \"login\": \"%s\", \"password\": \"12341\", \"firstName\": \"saske%s\" }", login, login);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(Constants.BASE_COURIER_URL);

        Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatusCode());
        Assert.assertTrue(response.jsonPath().getBoolean("ok"));

        // Получаем id курьера из ответа
        courierId = response.jsonPath().get("id");
    }

    @After
    @Step("Проверяем, что у нас есть id курьера для удаления и отправляем запрос на удаление курьера")
    public void tearDown() {
        // Проверяем, что у нас есть id курьера для удаления
        if (courierId != null) {
            // Отправляем запрос на удаление курьера
            Response deleteResponse = given()
                    .contentType(ContentType.JSON)
                    .body(String.format("{ \"id\": \"%s\" }", courierId))
                    .when()
                    .delete(Constants.BASE_COURIER_URL + "/" + courierId);

            Assert.assertEquals(HttpStatus.SC_OK, deleteResponse.getStatusCode());
            Assert.assertTrue(deleteResponse.jsonPath().getBoolean("ok"));
        }
    }

    @Test
    @Step("Проверяем запрет на дублирование курьеров в системе")
    public void testCreateCourier_DuplicateLogin() {
        // Предполагаем, что логин "ninja" уже существует в системе
        String requestBody = "{ \"login\": \"ninja\", \"password\": \"5678\", \"firstName\": \"itachi\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(Constants.BASE_COURIER_URL);

        Assert.assertEquals(HttpStatus.SC_CONFLICT, response.getStatusCode());
        Assert.assertTrue(response.jsonPath().getString("message").contains("Этот логин уже используется"));
    }

    @Test
    @Step("Проверяем создание курьера без указания всех обязательных полей")
    public void testCreateCourier_MissingFields() {
        // Попытка создания курьера без указания всех обязательных полей
        String requestBody = "{ \"login\": \"sasuke\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(Constants.BASE_COURIER_URL);

        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals("Недостаточно данных для создания учетной записи", response.jsonPath().getString("message"));
    }
}
