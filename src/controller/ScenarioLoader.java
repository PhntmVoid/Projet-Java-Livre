package controller;

import model.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ScenarioLoader {

    public static Scenario loadScenario(String resourcePath) {
        try (InputStream is = ScenarioLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            String jsonContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return parseScenarioFromJson(jsonContent);
        } catch (Exception e) {
            System.err.println("Failed to load scenario: " + e.getMessage());
            return createFallbackScenario();
        }
    }

    private static Scenario parseScenarioFromJson(String jsonContent) {
        try {
            // Manual JSON parsing (replacing GSON dependency)
            Map<String, Object> jsonMap = parseJson(jsonContent);

            String title = (String) jsonMap.get("title");
            int startChapterId = ((Number) jsonMap.get("startChapterId")).intValue();

            Scenario scenario = new Scenario(title, startChapterId);

            // Parse chapters
            Object[] chapters = (Object[]) jsonMap.get("chapters");
            for (Object chapterObj : chapters) {
                Map<String, Object> chapterMap = (Map<String, Object>) chapterObj;

                int id = ((Number) chapterMap.get("id")).intValue();
                String text = (String) chapterMap.get("text");

                Chapter chapter = new Chapter(id, text);

                // Parse choices if they exist
                if (chapterMap.containsKey("choices")) {
                    Object[] choices = (Object[]) chapterMap.get("choices");
                    if (choices != null) {
                        for (Object choiceObj : choices) {
                            Map<String, Object> choiceMap = (Map<String, Object>) choiceObj;

                            String choiceText = (String) choiceMap.get("text");
                            int nextChapterId = ((Number) choiceMap.get("nextChapterId")).intValue();

                            // Create choice and add to chapter
                            Choice choice = new Choice(choiceText, nextChapterId);
                            chapter.addChoice(choice);
                        }
                    }
                }

                scenario.addChapter(chapter);
            }

            return scenario;
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            throw e;
        }
    }

    // Simple implementation of a JSON parser to avoid GSON dependency
    // Note: This is a very simplified parser and does not handle all JSON cases
    private static Map<String, Object> parseJson(String json) {
        // For educational purposes, we're providing a simpler alternative
        // In a real application, you would want to use a proper JSON parser library

        // For this demo's purpose only - simulating JSON parsing for the specific expected format
        Map<String, Object> result = new HashMap<>();

        // Parse title
        result.put("title", extractValue(json, "\"title\":", ","));

        // Parse startChapterId
        String startId = extractValue(json, "\"startChapterId\":", ",");
        result.put("startChapterId", Integer.parseInt(startId));

        // For chapters, we'll do a very simplified parsing
        // Warning: This is not a proper JSON parser, just a simple extraction for this specific case
        String chaptersSection = json.substring(json.indexOf("\"chapters\":"));
        chaptersSection = chaptersSection.substring(chaptersSection.indexOf("[") + 1, chaptersSection.lastIndexOf("]"));

        // Split chapters
        String[] chapterStrings = splitChapters(chaptersSection);
        Object[] chapters = new Object[chapterStrings.length];

        for (int i = 0; i < chapterStrings.length; i++) {
            Map<String, Object> chapter = new HashMap<>();

            // Parse id
            String idStr = extractValue(chapterStrings[i], "\"id\":", ",");
            chapter.put("id", Integer.parseInt(idStr));

            // Parse text
            String text = extractValue(chapterStrings[i], "\"text\":", "\"choices\"");
            // Clean up text
            if (text.endsWith(",")) {
                text = text.substring(0, text.length() - 1);
            }
            chapter.put("text", text.replace("\\n", "\n").replace("\\\"", "\""));

            // Parse choices
            if (chapterStrings[i].contains("\"choices\":")) {
                String choicesSection = chapterStrings[i].substring(chapterStrings[i].indexOf("\"choices\":"));
                choicesSection = choicesSection.substring(choicesSection.indexOf("[") + 1);
                int endIndex = choicesSection.lastIndexOf("]");
                if (endIndex > 0) {
                    choicesSection = choicesSection.substring(0, endIndex);
                    String[] choiceStrings = splitChoices(choicesSection);
                    Object[] choices = new Object[choiceStrings.length];

                    for (int j = 0; j < choiceStrings.length; j++) {
                        Map<String, Object> choice = new HashMap<>();

                        // Parse text
                        String choiceText = extractValue(choiceStrings[j], "\"text\":", ",");
                        choice.put("text", choiceText);

                        // Parse nextChapterId
                        String nextId = extractValue(choiceStrings[j], "\"nextChapterId\":", "}");
                        choice.put("nextChapterId", Integer.parseInt(nextId));

                        choices[j] = choice;
                    }

                    chapter.put("choices", choices);
                }
            }

            chapters[i] = chapter;
        }

        result.put("chapters", chapters);
        return result;
    }

    private static String extractValue(String json, String key, String end) {
        int start = json.indexOf(key) + key.length();
        int endPos = json.indexOf(end, start);
        if (endPos < 0) {
            endPos = json.length();
        }
        String value = json.substring(start, endPos).trim();

        // Remove quotes for string values
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        return value;
    }

    private static String[] splitChapters(String chaptersSection) {
        // Very simplified chapter splitting - only works for specific JSON format
        java.util.List<String> chapters = new java.util.ArrayList<>();
        int depth = 0;
        int start = 0;

        for (int i = 0; i < chaptersSection.length(); i++) {
            char c = chaptersSection.charAt(i);
            if (c == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    chapters.add(chaptersSection.substring(start, i + 1));
                }
            }
        }

        return chapters.toArray(new String[0]);
    }

    private static String[] splitChoices(String choicesSection) {
        // Similar to splitChapters but for choices
        java.util.List<String> choices = new java.util.ArrayList<>();
        int depth = 0;
        int start = 0;

        for (int i = 0; i < choicesSection.length(); i++) {
            char c = choicesSection.charAt(i);
            if (c == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    choices.add(choicesSection.substring(start, i + 1));
                }
            }
        }

        return choices.toArray(new String[0]);
    }

    private static Scenario createFallbackScenario() {
        Scenario fallback = new Scenario("Scénario de secours", 1);
        Chapter errorChapter = new Chapter(1, "Erreur: Impossible de charger le scénario principal.");
        fallback.addChapter(errorChapter);
        return fallback;
    }
}