package ru.alex.config;

import lombok.Getter;
import okhttp3.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class AppConfig {
    @Getter
    private static String clientId; // ID клиента
    @Getter
    private static String clientSecret; // Ключ клиента
    @Getter
    private static String oauthUrl; // URL для OAuth
    @Getter
    private static String chatUrl; // URL для чата
    @Getter
    private static MediaType mediaTypeForm; // Тип медиа для формы
    @Getter
    private static MediaType mediaTypeJson; // Тип медиа для JSON

    static {
        try {
            loadConfig(); // Загрузка конфигурации
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadConfig() throws IOException {
        Properties properties = new Properties(); // Создание объекта свойств
        try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream); // Загрузка свойств из файла
                clientId = properties.getProperty("CLIENT_ID"); // Получение ID клиента
                clientSecret = properties.getProperty("CLIENT_SECRET"); // Получение ключа клиента
                mediaTypeForm = MediaType.parse(properties.getProperty("MEDIA_TYPE_FORM")); // Получение типа медиа для формы
                mediaTypeJson = MediaType.parse(properties.getProperty("MEDIA_TYPE_JSON")); // Получение типа медиа для JSON
                oauthUrl = properties.getProperty("OAUTH_URL"); // Получение URL для OAuth
                chatUrl = properties.getProperty("CHAT_URL"); // Получение URL для чата
            } else {
                throw new IOException("config.properties не найден"); // Если файл не найден, выбрасывается исключение
            }
        }
    }
}
