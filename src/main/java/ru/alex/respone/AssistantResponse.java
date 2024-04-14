package ru.alex.respone;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.alex.config.AppConfig;
import ru.alex.config.ChatSettings;

public class AssistantResponse {
    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();
    private static final Gson gson = new Gson();

    public static String getAssistantResponse(String accessToken, String userMessage) throws Exception {
        String bodyContent = "{"
                + "  \"model\": \"" + ChatSettings.MODEL + "\","
                + "  \"messages\": ["
                + "    {"
                + "      \"role\": \"user\","
                + "      \"content\": \"" + userMessage + "\""
                + "    }"
                + "  ],"
                + "  \"temperature\": " + ChatSettings.TEMPERATURE + ","
                + "  \"top_p\": " + ChatSettings.TOP_P + ","
                + "  \"n\": " + ChatSettings.N + ","
                + "  \"stream\": false,"
                + "  \"max_tokens\": " + ChatSettings.MAX_TOKENS + ","
                + "  \"repetition_penalty\": " + ChatSettings.REPETITION_PENALTY
                + "}";

        RequestBody body = RequestBody.create(bodyContent, AppConfig.getMediaTypeJson());

        Request request = new Request.Builder()
                .url(AppConfig.getChatUrl())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return extractAssistantResponse(responseBody);
            } else {
                throw new Exception("Не удалось получить ответ помощника. Код ответа: " + response.code());
            }
        }
    }

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

