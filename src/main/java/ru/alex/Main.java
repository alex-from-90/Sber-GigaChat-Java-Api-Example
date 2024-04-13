package ru.alex;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            String accessToken = TokenProvider.getAccessToken();
            // System.out.println("Access Token: " + accessToken);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Введите ваш вопрос (exit для выхода): ");
                String userMessage = scanner.nextLine();

                if ("exit".equalsIgnoreCase(userMessage)) {
                    System.out.println("Выход из программы...");
                    break;
                }

                String assistantResponse = AssistantResponse.getAssistantResponse(accessToken, userMessage);
                System.out.println("Ответ ассистента: " + assistantResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
