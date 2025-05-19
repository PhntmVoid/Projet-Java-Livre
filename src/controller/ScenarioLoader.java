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
            Map<String, Object> jsonMap = new HashMap<>();
            
            // Parse title
            String title = extractValue(jsonContent, "\"title\"", ",");
            jsonMap.put("title", title);
            
            // Parse startChapterId
            String startIdStr = extractValue(jsonContent, "\"startChapterId\"", ",");
            int startChapterId = Integer.parseInt(startIdStr.trim());
            jsonMap.put("startChapterId", startChapterId);

            Scenario scenario = new Scenario(title, startChapterId);

            // Extract chapters array
            String chaptersStr = jsonContent.substring(
                jsonContent.indexOf("\"chapters\""),
                jsonContent.lastIndexOf("]") + 1
            );
            chaptersStr = chaptersStr.substring(chaptersStr.indexOf("[") + 1, chaptersStr.lastIndexOf("]"));

            // Split chapters
            String[] chapters = splitObjects(chaptersStr);

            for (String chapterStr : chapters) {
                // Parse chapter ID
                String idStr = extractValue(chapterStr, "\"id\"", ",");
                int id = Integer.parseInt(idStr.trim());

                // Parse chapter text
                String text = extractValue(chapterStr, "\"text\"", "\"choices\"");
                if (text != null && text.endsWith(",")) {
                    text = text.substring(0, text.length() - 1);
                }

                Chapter chapter = new Chapter(id, text);

                // Parse endurance modifier if present
                String enduranceModStr = extractValue(chapterStr, "\"enduranceModifier\"", ",");
                if (enduranceModStr != null) {
                    chapter.setEnduranceModifier(Integer.parseInt(enduranceModStr.trim()));
                }

                // Parse fear modifier if present
                String fearModStr = extractValue(chapterStr, "\"fearModifier\"", ",");
                if (fearModStr != null) {
                    chapter.setFearModifier(Integer.parseInt(fearModStr.trim()));
                }

                // Parse choices if present
                if (chapterStr.contains("\"choices\"")) {
                    String choicesSection = chapterStr.substring(chapterStr.indexOf("\"choices\""));
                    choicesSection = choicesSection.substring(choicesSection.indexOf("[") + 1);
                    choicesSection = choicesSection.substring(0, choicesSection.lastIndexOf("]"));

                    String[] choices = splitObjects(choicesSection);
                    
                    for (String choiceStr : choices) {
                        String choiceText = extractValue(choiceStr, "\"text\"", ",");
                        String nextIdStr = extractValue(choiceStr, "\"nextChapterId\"", "}");
                        
                        if (choiceText != null && nextIdStr != null) {
                            Choice choice = new Choice(choiceText, Integer.parseInt(nextIdStr.trim()));
                            
                            // Parse requiresLuckTest if present
                            String requiresLuckTest = extractValue(choiceStr, "\"requiresLuckTest\"", ",");
                            if (requiresLuckTest != null) {
                                choice.setRequiresLuckTest(Boolean.parseBoolean(requiresLuckTest.trim()));
                            }

                            // Parse combatRequired if present
                            String combatRequired = extractValue(choiceStr, "\"combatRequired\"", ",");
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

    private static Scenario createFallbackScenario() {
        Scenario fallback = new Scenario("Scénario de secours", 1);
        Chapter errorChapter = new Chapter(1, "Erreur: Impossible de charger le scénario principal.");
        fallback.addChapter(errorChapter);
        return fallback;
    }
}