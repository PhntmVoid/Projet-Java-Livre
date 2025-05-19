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
            // Parse title
            String title = extractValue(jsonContent, "\"title\"", ",");
            if (title == null) throw new IllegalArgumentException("Title not found in JSON");

            // Parse startChapterId
            String startIdStr = extractValue(jsonContent, "\"startChapterId\"", ",");
            if (startIdStr == null) throw new IllegalArgumentException("Start chapter ID not found in JSON");
            int startChapterId = Integer.parseInt(startIdStr.trim());

            Scenario scenario = new Scenario(title, startChapterId);

            // Find chapters array
            int chaptersStart = jsonContent.indexOf("\"chapters\"");
            if (chaptersStart == -1) throw new IllegalArgumentException("Chapters array not found in JSON");
            
            chaptersStart = jsonContent.indexOf("[", chaptersStart);
            int chaptersEnd = findMatchingBracket(jsonContent, chaptersStart);
            
            if (chaptersStart == -1 || chaptersEnd == -1) {
                throw new IllegalArgumentException("Invalid chapters array structure");
            }

            String chaptersContent = jsonContent.substring(chaptersStart + 1, chaptersEnd);
            String[] chapters = splitObjects(chaptersContent);

            for (String chapterStr : chapters) {
                // Parse chapter ID
                String idStr = extractValue(chapterStr, "\"id\"", ",");
                if (idStr == null) continue;
                int id = Integer.parseInt(idStr.trim());

                // Parse chapter text
                String chapterText = extractValue(chapterStr, "\"text\"", "\"choices\"");
                if (chapterText == null) continue;
                if (chapterText.endsWith(",")) {
                    chapterText = chapterText.substring(0, chapterText.length() - 1);
                }

                Chapter chapter = new Chapter(id, chapterText);

                // Parse endurance modifier
                String enduranceModStr = extractValue(chapterStr, "\"enduranceModifier\"", ",");
                if (enduranceModStr != null) {
                    try {
                        chapter.setEnduranceModifier(Integer.parseInt(enduranceModStr.trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid endurance modifier: " + enduranceModStr);
                    }
                }

                // Parse fear modifier
                String fearModStr = extractValue(chapterStr, "\"fearModifier\"", ",");
                if (fearModStr != null) {
                    try {
                        chapter.setFearModifier(Integer.parseInt(fearModStr.trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid fear modifier: " + fearModStr);
                    }
                }

                // Parse luck test
                if (chapterStr.contains("\"luckTest\"")) {
                    String luckTestStr = extractBetween(chapterStr, "\"luckTest\"", "}");
                    if (luckTestStr != null) {
                        LuckTestOutcome success = parseLuckTestOutcome(luckTestStr, "success");
                        LuckTestOutcome failure = parseLuckTestOutcome(luckTestStr, "failure");
                        if (success != null || failure != null) {
                            chapter.setLuckTest(new LuckTest(success, failure));
                        }
                    }
                }

                // Parse choices
                if (chapterStr.contains("\"choices\"")) {
                    int choicesStart = chapterStr.indexOf("\"choices\"");
                    choicesStart = chapterStr.indexOf("[", choicesStart);
                    int choicesEnd = findMatchingBracket(chapterStr, choicesStart);
                    
                    if (choicesStart != -1 && choicesEnd != -1) {
                        String choicesStr = chapterStr.substring(choicesStart + 1, choicesEnd);
                        String[] choices = splitObjects(choicesStr);
                        
                        for (String choiceStr : choices) {
                            String choiceText = extractValue(choiceStr, "\"text\"", ",");
                            String nextIdStr = extractValue(choiceStr, "\"nextChapterId\"", "}");
                            
                            if (choiceText != null && nextIdStr != null) {
                                try {
                                    Choice choice = new Choice(choiceText, Integer.parseInt(nextIdStr.trim()));
                                    
                                    // Parse requiresLuckTest
                                    String requiresLuckTest = extractValue(choiceStr, "\"requiresLuckTest\"", ",");
                                    if (requiresLuckTest != null) {
                                        choice.setRequiresLuckTest(Boolean.parseBoolean(requiresLuckTest.trim()));
                                    }

                                    // Parse combatRequired
                                    String combatRequired = extractValue(choiceStr, "\"combatRequired\"", ",");
                                    if (combatRequired != null) {
                                        choice.setCombatRequired(Boolean.parseBoolean(combatRequired.trim()));
                                    }

                                    chapter.addChoice(choice);
                                } catch (NumberFormatException e) {
                                    System.err.println("Invalid nextChapterId in choice: " + nextIdStr);
                                }
                            }
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

    private static LuckTestOutcome parseLuckTestOutcome(String luckTestStr, String type) {
        if (!luckTestStr.contains("\"" + type + "\"")) return null;
        
        String outcomeStr = extractBetween(luckTestStr, "\"" + type + "\"", "}");
        if (outcomeStr == null) return null;

        String text = extractValue(outcomeStr, "\"text\"", ",");
        String modStr = extractValue(outcomeStr, "\"enduranceModifier\"", "}");
        
        if (text != null && modStr != null) {
            try {
                return new LuckTestOutcome(text, Integer.parseInt(modStr.trim()));
            } catch (NumberFormatException e) {
                System.err.println("Invalid endurance modifier in luck test outcome: " + modStr);
            }
        }
        return null;
    }

    private static String extractValue(String json, String key, String end) {
        try {
            int keyIndex = json.indexOf(key);
            if (keyIndex == -1) return null;

            int startIndex = json.indexOf(":", keyIndex) + 1;
            while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
                startIndex++;
            }

            boolean isQuoted = json.charAt(startIndex) == '"';
            if (isQuoted) startIndex++;

            int endIndex;
            if (isQuoted) {
                endIndex = json.indexOf("\"", startIndex);
            } else {
                endIndex = -1;
                for (String endChar : end.split("")) {
                    int pos = json.indexOf(endChar, startIndex);
                    if (pos != -1 && (endIndex == -1 || pos < endIndex)) {
                        endIndex = pos;
                    }
                }
                if (endIndex == -1) endIndex = json.length();
            }

            return json.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            return null;
        }
    }

    private static String extractBetween(String json, String startKey, String endStr) {
        try {
            int startIndex = json.indexOf(startKey);
            if (startIndex == -1) return null;

            startIndex = json.indexOf("{", startIndex);
            if (startIndex == -1) return null;

            int endIndex = startIndex;
            int depth = 1;

            while (depth > 0 && endIndex < json.length() - 1) {
                endIndex++;
                char c = json.charAt(endIndex);
                if (c == '{') depth++;
                else if (c == '}') depth--;
            }

            return json.substring(startIndex, endIndex + 1);
        } catch (Exception e) {
            return null;
        }
    }

    private static String[] splitObjects(String json) {
        java.util.List<String> objects = new java.util.ArrayList<>();
        int depth = 0;
        int start = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    objects.add(json.substring(start, i + 1));
                }
            }
        }

        return objects.toArray(new String[0]);
    }

    private static int findMatchingBracket(String text, int openBracketIndex) {
        int depth = 0;
        for (int i = openBracketIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private static Scenario createFallbackScenario() {
        Scenario fallback = new Scenario("Scénario de secours", 1);
        Chapter errorChapter = new Chapter(1, "Erreur: Impossible de charger le scénario principal.");
        fallback.addChapter(errorChapter);
        return fallback;
    }
}