package controller;

import model.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ScenarioLoader {
    public static Scenario loadScenario(String resourcePath) {
        try (InputStream is = ScenarioLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Resource not found: " + resourcePath);
            }

            String jsonContent = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
            return parseJsonManually(jsonContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load scenario", e);
        }
    }

    private static Scenario parseJsonManually(String json) {
        // Implémentation simplifiée - pour un vrai projet, utilisez une bibliothèque JSON
        String title = extractValue(json, "title");
        int startChapterId = Integer.parseInt(extractValue(json, "startChapterId"));

        Scenario scenario = new Scenario(title, startChapterId);

        String chaptersSection = extractSection(json, "chapters");
        List<String> chapters = splitArray(chaptersSection);

        for (String chapterJson : chapters) {
            int id = Integer.parseInt(extractValue(chapterJson, "id"));
            String text = extractValue(chapterJson, "text");

            Chapter chapter = new Chapter(id, text);

            if (chapterJson.contains("choices")) {
                String choicesSection = extractSection(chapterJson, "choices");
                List<String> choices = splitArray(choicesSection);

                for (String choiceJson : choices) {
                    String choiceText = extractValue(choiceJson, "text");
                    int nextChapterId = Integer.parseInt(extractValue(choiceJson, "nextChapterId"));
                    chapter.addChoice(new Choice(choiceText, nextChapterId));
                }
            }

            scenario.addChapter(chapter);
        }

        return scenario;
    }

    // Méthodes utilitaires pour le parsing manuel
    private static String extractValue(String json, String key) {
        int start = json.indexOf("\"" + key + "\":") + key.length() + 3;
        int end = json.indexOf(',', start);
        if (end == -1) end = json.indexOf('}', start);

        String value = json.substring(start, end).trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static String extractSection(String json, String key) {
        int start = json.indexOf("\"" + key + "\":") + key.length() + 3;
        int braceCount = 0;
        int bracketCount = 0;
        boolean inArray = json.charAt(start) == '[';
        if (inArray) {
            start++;
            bracketCount++;
        }

        int end = start;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (c == '{') braceCount++;
            if (c == '}') braceCount--;
            if (c == '[') bracketCount++;
            if (c == ']') bracketCount--;

            if ((inArray && bracketCount == 0) || (!inArray && braceCount == 0)) {
                break;
            }
            end++;
        }

        return json.substring(start, end);
    }

    private static List<String> splitArray(String arrayJson) {
        List<String> items = new ArrayList<>();
        int braceCount = 0;
        int start = 0;

        for (int i = 0; i < arrayJson.length(); i++) {
            char c = arrayJson.charAt(i);
            if (c == '{') braceCount++;
            if (c == '}') braceCount--;

            if (braceCount == 0 && c == ',') {
                items.add(arrayJson.substring(start, i));
                start = i + 1;
            }
        }

        if (start < arrayJson.length()) {
            items.add(arrayJson.substring(start));
        }

        return items;
    }
}