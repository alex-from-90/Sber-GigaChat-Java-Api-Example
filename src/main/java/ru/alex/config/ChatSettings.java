package ru.alex.config;

public class ChatSettings {
    // Модель для общения
    public static final String MODEL = "GigaChat";

    // Температура для генерации ответа
    public static final double TEMPERATURE = 1;

    // Top-p параметр
    public static final double TOP_P = 0.1;

    // Количество вариантов ответа
    public static final int N = 1;

    // Максимальное количество токенов в ответе
    public static final int MAX_TOKENS = 1024;

    // Penalty для предотвращения повторений
    public static final double REPETITION_PENALTY = 1;
}
