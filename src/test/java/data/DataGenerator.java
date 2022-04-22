package data;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.Value;
import org.junit.jupiter.api.BeforeAll;

import java.util.Locale;

import static io.restassured.RestAssured.given;

@Data
public class DataGenerator {
    private static final Faker faker = new Faker(new Locale("en"));
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()// спецификация нужна для того, чтобы переиспользовать настройки в разных запросах
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();


    private DataGenerator() {//Конструктор
    }


    private static void sendRequest(RegistrationDto user) {//отправить запрос на указанный в требованиях path, передав в body запроса объект user
        Gson gson = new Gson();
        String jsonUser = gson.toJson(user);
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(jsonUser)
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK

    }


    public static String getRandomLogin() {//Создаем рандомный логин
        String login = faker.name().username();
        return login;
    }

    public static String getRandomPassword() {
        String password = faker.internet().password();//Создаем рандомный пароль
        return password;
    }

    public static class Registration {//Регистрация

        private Registration() {//Конструктор
        }


        public static RegistrationDto getUser(String status) {//создать user используя методы getRandomLogin(), getRandomPassword() и параметр status
            return new RegistrationDto(getRandomLogin(), getRandomPassword(), status);
        }

        public static RegistrationDto getRegisteredUser(String status) {//Зарегистриров юзер
            RegistrationDto registeredUser = getUser(status);
            sendRequest(registeredUser);
            return registeredUser;
        }

    }

    @Value
    public static class RegistrationDto {//Шаблон для юзера в виде дата- класса
        String login;
        String password;
        String status;
    }
}