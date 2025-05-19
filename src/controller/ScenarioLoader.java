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
            e.printStackTrace();
            return createFallbackScenario();
        }
    }

    private static Scenario parseScenarioFromJson(String jsonContent) {
        try {
            Map<String, Object> jsonMap = parseJson(jsonContent);

            String title = (String) jsonMap.get("title");
            int startChapterId = ((Number) jsonMap.get("startChapterId")).intValue();

            Scenario scenario = new Scenario(title, startChapterId);

            Object[] chapters = (Object[]) jsonMap.get("chapters");
            for (Object chapterObj : chapters) {
                Map<String, Object> chapterMap = (Map<String, Object>) chapterObj;

                int id = ((Number) chapterMap.get("id")).intValue();
                String text = (String) chapterMap.get("text");

                Chapter chapter = new Chapter(id, text);

                // Parse endurance and fear modifiers
                if (chapterMap.containsKey("enduranceModifier")) {
                    String modStr = extractValue(chapterMap.toString(), "\"enduranceModifier\":", ",}");
                    if (modStr != null && !modStr.isEmpty()) {
                        chapter.setEnduranceModifier(Integer.parseInt(modStr.trim()));
                    }
                }

                if (chapterMap.containsKey("fearModifier")) {
                    String modStr = extractValue(chapterMap.toString(), "\"fearModifier\":", ",}");
                    if (modStr != null && !modStr.isEmpty()) {
                        chapter.setFearModifier(Integer.parseInt(modStr.trim()));
                    }
                }

                // Parse luck test
                if (chapterMap.containsKey("luckTest")) {
                    Map<String, Object> luckTestMap = (Map<String, Object>) chapterMap.get("luckTest");
                    LuckTestOutcome success = null;
                    LuckTestOutcome failure = null;

                    if (luckTestMap.containsKey("success")) {
                        Map<String, Object> successMap = (Map<String, Object>) luckTestMap.get("success");
                        String successText = (String) successMap.get("text");
                        int successMod = ((Number) successMap.get("enduranceModifier")).intValue();
                        success = new LuckTestOutcome(successText, successMod);
                    }

                    if (luckTestMap.containsKey("failure")) {
                        Map<String, Object> failureMap = (Map<String, Object>) luckTestMap.get("failure");
                        String failureText = (String) failureMap.get("text");
                        int failureMod = ((Number) failureMap.get("enduranceModifier")).intValue();
                        failure = new LuckTestOutcome(failureText, failureMod);
                    }

                    chapter.setLuckTest(new LuckTest(success, failure));
                }

                // Parse choices
                if (chapterMap.containsKey("choices")) {
                    String choicesStr = chapterMap.get("choices").toString();
                    String[] choiceStrings = splitChoices(choicesStr);

                    for (String choiceString : choiceStrings) {
                        String choiceText = extractValue(choiceString, "\"text\":", ",");
                        String nextIdStr = extractValue(choiceString, "\"nextChapterId\":", ",}");

                        if (choiceText != null && nextIdStr != null) {
                            int nextChapterId = Integer.parseInt(nextIdStr.trim());
                            Choice choice = new Choice(choiceText, nextChapterId);

                            // Parse boolean flags
                            String requiresLuckTest = extractValue(choiceString, "\"requiresLuckTest\":", ",}");
                            if (requiresLuckTest != null) {
                                choice.setRequiresLuckTest(Boolean.parseBoolean(requiresLuckTest.trim()));
                            }

                            String combatRequired = extractValue(choiceString, "\"combatRequired\":", ",}");
                            if (combatRequired != null) {
                                choice.setCombatRequired(Boolean.parseBoolean(combatRequired.trim()));
                            }

                            chapter.addChoice(choice);
                        }
                    }
                }

                scenario.addChapter(chapter);
            }

            return scenario;
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static Map<String, Object> parseJson(String json) {
        Map<String, Object> result = new HashMap<>();

        result.put("title", extractValue(json, "\"title\":", ","));

        String startId = extractValue(json, "\"startChapterId\":", ",");
        result.put("startChapterId", Integer.parseInt(startId));

        String chaptersSection = json.substring(json.indexOf("\"chapters\":"));
        chaptersSection = chaptersSection.substring(chaptersSection.indexOf("[") + 1, chaptersSection.lastIndexOf("]"));

        String[] chapterStrings = splitChapters(chaptersSection);
        Object[] chapters = new Object[chapterStrings.length];

        for (int i = 0; i < chapterStrings.length; i++) {
            Map<String, Object> chapter = new HashMap<>();

            String idStr = extractValue(chapterStrings[i], "\"id\":", ",");
            chapter.put("id", Integer.parseInt(idStr));

            String text = extractValue(chapterStrings[i], "\"text\":", "\"choices\"");
            if (text != null) {
                if (text.endsWith(",")) {
                    text = text.substring(0, text.length() - 1);
                }
                chapter.put("text", text);
            }

            if (chapterStrings[i].contains("\"choices\":[")) {
                String choicesSection = chapterStrings[i].substring(chapterStrings[i].indexOf("\"choices\":["));
                choicesSection = choicesSection.substring(0, findClosingBracket(choicesSection, '[', ']') + 1);
                chapter.put("choices", choicesSection);
            }

            chapters[i] = chapter;
        }

        result.put("chapters", chapters);
        return result;
    }

    private static int findClosingBracket(String str, char open, char close) {
        int depth = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == open) depth++;
            else if (str.charAt(i) == close) {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private static String extractValue(String json, String key, String end) {
        try {
            int start = json.indexOf(key);
            if (start == -1) return null;

            start += key.length();
            start = json.indexOf(":", start) + 1;
            while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
                start++;
            }

            int endPos = -1;
            if (json.charAt(start) == '"') {
                start++;
                endPos = json.indexOf("\"", start);
            } else {
                for (char c : end.toCharArray()) {
                    int pos = json.indexOf(c, start);
                    if (pos != -1 && (endPos == -1 || pos < endPos)) {
                        endPos = pos;
                    }
                }
            }

            if (endPos == -1) {
                endPos = json.length();
            }

            return json.substring(start, endPos).trim();
        } catch (Exception e) {
            return null;
        }
    }

    private static String[] splitChapters(String chaptersSection) {
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
        if (!choicesSection.startsWith("[")) {
            int start = choicesSection.indexOf("[");
            if (start != -1) {
                choicesSection = choicesSection.substring(start);
            }
        }

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