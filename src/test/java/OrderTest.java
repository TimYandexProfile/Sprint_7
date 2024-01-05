import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;

@RunWith(Parameterized.class)
public class OrderTest {

    private final String firstName;
    private final String lastName;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final int rentTime;
    private final String deliveryDate;
    private final String comment;
    private final String[] color;

    public OrderTest(String firstName, String lastName, String address, String metroStation,
                     String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Naruto", "Uchiha", "Konoha, 142 apt.", "4", "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{"BLACK"}},
                {"Sasuke", "Uzumaki", "Leaf Village, 15 apt.", "2", "+7 800 355 35 36", 3, "2020-07-01", "Naruto, where are you?", new String[]{"GREY"}},
                {"Hinata", "Hyuga", "Hidden Sand Village, 7 apt.", "1", "+7 800 355 35 37", 7, "2020-08-15", "Naruto, dinner at my place?", new String[]{"BLACK", "GREY"}},
                {"Sakura", "Haruno", "Hidden Leaf Village, 5 apt.", "3", "+7 800 355 35 38", 4, "2020-09-10", "Sasuke, let's catch up!", null}
        });
    }

    @Test
    @Step("Проверка создания заказа")
    public void testCreateOrder() {
        String requestBody = createOrderRequestBody();
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(Constants.BASE_ORDERS_URL);

        Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatusCode());
        Assert.assertNotNull(response.jsonPath().get("track"));
    }

    private String createOrderRequestBody() {
        StringBuilder requestBody = new StringBuilder("{");
        requestBody.append("\"firstName\": \"").append(firstName).append("\",");
        requestBody.append("\"lastName\": \"").append(lastName).append("\",");
        requestBody.append("\"address\": \"").append(address).append("\",");
        requestBody.append("\"metroStation\": \"").append(metroStation).append("\",");
        requestBody.append("\"phone\": \"").append(phone).append("\",");
        requestBody.append("\"rentTime\": ").append(rentTime).append(",");
        requestBody.append("\"deliveryDate\": \"").append(deliveryDate).append("\",");
        requestBody.append("\"comment\": \"").append(comment).append("\"");

        if (color != null) {
            String colorString = Arrays.stream(color)
                    .map(c -> "\"" + c + "\"")
                    .collect(Collectors.joining(",", "[", "]"));
            requestBody.append(",\"color\": ").append(colorString);
        }

        requestBody.append("}");
        return requestBody.toString();
    }

}

