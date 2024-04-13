package ru.alex;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AssistantResponse {
    private static final OkHttpClient client = new OkHttpClient().newBuilder().build(); // Создание клиента HTTP
    private static final Gson gson = new Gson(); // Создание экземпляра Gson для работы с JSON

    public static String getAssistantResponse(String accessToken, String userMessage) throws Exception {
        // Формирование тела запроса
        String bodyContent = "{"
                + "  \"model\": \"GigaChat\","
                + "  \"messages\": ["
                + "    {"
                + "      \"role\": \"user\","
                + "      \"content\": \"" + userMessage + "\""
                + "    }"
                + "  ],"
                + "  \"temperature\": 1,"
                + "  \"top_p\": 0.1,"
                + "  \"n\": 1,"
                + "  \"stream\": false,"
                + "  \"max_tokens\": 512,"
                + "  \"repetition_penalty\": 1"
                + "}";

        // Создание тела запроса
        RequestBody body = RequestBody.create(bodyContent, AppConfig.getMediaTypeJson());

        // Создание запроса
        Request request = new Request.Builder()
                .url(AppConfig.getChatUrl())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        // Выполнение запроса и обработка ответа
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return extractAssistantResponse(responseBody);
            } else {
                throw new Exception("Не удалось получить ответ помощника. Код ответа: " + response.code());
            }
        }
    }

    // Извлечение ответа ассистента из тела ответа
    private static String extractAssistantResponse(String responseBody) {
        JsonObject jsonObj = gson.fromJson(responseBody, JsonObject.class);
        JsonArray choicesArray = jsonObj.getAsJsonArray("choices");

        if (choicesArray != null && !choicesArray.isEmpty()) {
            JsonObject choiceObj = choicesArray.get(0).getAsJsonObject();
            JsonObject messageObj = choiceObj.getAsJsonObject("message");
            return messageObj.has("content") ? messageObj.get("content").getAsString() : "";
        } else {
            throw new IllegalStateException("Массив 'choices' имеет значение null или пустой");
        }
    }
}
