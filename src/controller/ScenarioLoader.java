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
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            String jsonContent = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
            return parseJsonManually(jsonContent);
        } catch (Exception e) {
            System.err.println("Failed to load scenario from " + resourcePath + ": " + e.getMessage());
            e.printStackTrace();
            // Return a minimal fallback scenario instead of throwing exception
            return createFallbackScenario();
        }
    }

    private static Scenario createFallbackScenario() {
        Scenario fallback = new Scenario("Scénario de secours", 0);
        Chapter errorChapter = new Chapter(0, "Impossible de charger le scénario. Veuillez vérifier que le fichier JSON est correctement formaté.");
        fallback.addChapter(errorChapter);
        return fallback;
    }

    private static Scenario parseJsonManually(String json) {
        try {
            String title = extractValue(json, "title");
            int startChapterId = Integer.parseInt(extractValue(json, "startChapterId"));

            Scenario scenario = new Scenario(title, startChapterId);
            String chaptersSection = extractSection(json, "chapters");
            List<String> chapters = splitArray(chaptersSection);

            for (String chapterJson : chapters) {
                try {
                    int id = Integer.parseInt(extractValue(chapterJson, "id"));
                    String text = extractValue(chapterJson, "text");

                    Chapter chapter = new Chapter(id, text);

                    if (chapterJson.contains("\"choices\"")) {
                        String choicesSection = extractSection(chapterJson, "choices");
                        List<String> choices = splitArray(choicesSection);

                        for (String choiceJson : choices) {
                            try {
                                String choiceText = extractValue(choiceJson, "text");
                                int nextChapterId = Integer.parseInt(extractValue(choiceJson, "nextChapterId"));
                                chapter.addChoice(new Choice(choiceText, nextChapterId));
                            } catch (Exception e) {
                                System.err.println("Error parsing choice: " + e.getMessage());
                            }
                        }
                    }

                    scenario.addChapter(chapter);
                } catch (Exception e) {
                    System.err.println("Error parsing chapter: " + e.getMessage());
                }
            }

            // Make sure start chapter exists
            if (scenario.getChapter(startChapterId) == null) {
                throw new IllegalStateException("Start chapter ID " + startChapterId + " not found in loaded chapters");
            }

            return scenario;
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return createFallbackScenario();
        }
    }

    // Méthodes utilitaires pour le parsing manuel
    private static String extractValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\":");
        if (keyIndex == -1) {
            throw new IllegalArgumentException("Key not found: " + key);
        }

        int start = keyIndex + key.length() + 3;
        int end = -1;

        // Handle string values (with quotes)
        if (json.charAt(start) == '"') {
            start++; // Skip opening quote
            end = json.indexOf('"', start);
            if (end == -1) {
                throw new IllegalArgumentException("Unterminated string value for key: " + key);
            }
            return json.substring(start, end);
        }
        // Handle numeric or boolean values
        else {
            end = json.indexOf(',', start);
            if (end == -1) end = json.indexOf('}', start);
            if (end == -1) {
                throw new IllegalArgumentException("Unterminated value for key: " + key);
            }
            return json.substring(start, end).trim();
        }
    }

    private static String extractSection(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\":");
        if (keyIndex == -1) {
            throw new IllegalArgumentException("Section not found: " + key);
        }

        int start = keyIndex + key.length() + 2;
        while (start < json.length() && (json.charAt(start) == ':' || Character.isWhitespace(json.charAt(start)))) {
            start++;
        }

        boolean inArray = json.charAt(start) == '[';
        if (inArray) {
            return extractBalancedSection(json, start, '[', ']');
        } else if (json.charAt(start) == '{') {
            return extractBalancedSection(json, start, '{', '}');
        } else {
            throw new IllegalArgumentException("Invalid section format for key: " + key);
        }
    }

    private static String extractBalancedSection(String json, int start, char openChar, char closeChar) {
        int count = 0;
        int contentStart = start + 1; // Skip the opening bracket/brace

        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == openChar) count++;
            if (c == closeChar) count--;

            if (count == 0) {
                return json.substring(contentStart, i);
            }
        }

        throw new IllegalArgumentException("Unbalanced brackets/braces in JSON");
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
                items.add(arrayJson.substring(start, i).trim());
                start = i + 1;
            }
        }

        if (start < arrayJson.length()) {
            items.add(arrayJson.substring(start).trim());
        }

        return items;
    }
}