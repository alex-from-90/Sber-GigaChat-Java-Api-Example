package ru.alex.respone;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.alex.config.AppConfig;

import java.util.Base64;
import java.util.UUID;

public class TokenProvider {
    private static final String RQ_UID = UUID.randomUUID().toString(); // Генерация случайного UUID для RqUID
    private static final OkHttpClient client = new OkHttpClient().newBuilder().build(); // Создание клиента HTTP
    private static final Gson gson = new Gson(); // Создание экземпляра Gson для работы с JSON
    private static String accessToken; // Токен доступа
    private static long tokenExpirationTime; // Время истечения токена

    public static String getAccessToken() throws Exception {
        long currentTime = System.currentTimeMillis(); // Текущее время в миллисекундах
        // Если токен еще не получен или истекло его время действия, обновляем токен
        if (accessToken == null || currentTime > tokenExpirationTime) {
            updateToken();
        }
        return accessToken; // Возвращаем токен доступа
    }

    private static void updateToken() throws Exception {
        String authData = Base64.getEncoder().encodeToString((AppConfig.getClientId() + ":" + AppConfig.getClientSecret()).getBytes()); // Кодирование данных авторизации в Base64
        String authHeader = "Basic " + authData; // Формирование заголовка авторизации

        String bodyContent = "scope=GIGACHAT_API_PERS"; // Содержимое тела запроса
        RequestBody body = RequestBody.create(bodyContent, AppConfig.getMediaTypeForm()); // Создание тела запроса

        Request request = new Request.Builder() // Создание запроса
                .url(AppConfig.getOauthUrl())
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .addHeader("RqUID", UUID.randomUUID().toString()) // Генерация идентификатора запроса
                .addHeader("Authorization", authHeader)
                .build();

        try (Response response = client.newCall(request).execute()) { // Выполнение запроса
            if (response.isSuccessful() && response.body() != null) { // Если запрос успешен и тело ответа не пустое
                String responseBody = response.body().string(); // Получение тела ответа
                accessToken = extractAccessToken(responseBody); // Извлечение токена доступа из тела ответа
                // Устанавливаем время истечения токена на 30 минут в будущем
                tokenExpirationTime = System.currentTimeMillis() + 30 * 60 * 1000;
            } else {
                throw new Exception("Не удалось получить токен доступа. Код ответа: " + response.code()); // Если запрос неуспешен, выбрасываем исключение
            }
        }
    }

    private static String extractAccessToken(String responseBody) {
        JsonObject jsonObj = gson.fromJson(responseBody, JsonObject.class); // Преобразование строки ответа в объект JSON
        return jsonObj.get("access_token").getAsString(); // Возвращаем значение токена доступа
    }
}
