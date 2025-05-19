package controller;

import model.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ScenarioLoader {
    public static Scenario loadScenario(String resourcePath) {
        try (InputStream is = ScenarioLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            String jsonContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            
            // Parse the scenario manually
            Map<String, Object> scenarioMap = parseScenario(jsonContent);
            
            String title = (String) scenarioMap.get("title");
            int startChapterId = ((Number) scenarioMap.get("startChapterId")).intValue();
            
            Scenario scenario = new Scenario(title, startChapterId);
            
            List<Map<String, Object>> chapters = (List<Map<String, Object>>) scenarioMap.get("chapters");
            for (Map<String, Object> chapterMap : chapters) {
                try {
                    Chapter chapter = parseChapter(chapterMap);
                    if (chapter != null) {
                        scenario.addChapter(chapter);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing chapter: " + e.getMessage());
                }
            }
            
            return scenario;
        } catch (Exception e) {
            System.err.println("Failed to load scenario: " + e.getMessage());
            e.printStackTrace();
            return createFallbackScenario();
        }
    }

    private static Map<String, Object> parseScenario(String content) {
        Map<String, Object> scenario = new HashMap<>();
        
        // Simple parser for demonstration - you might want to implement a more robust parser
        String[] lines = content.split("\n");
        List<Map<String, Object>> chapters = new ArrayList<>();
        
        scenario.put("title", "Le Manoir de l'Enfer");
        scenario.put("startChapterId", 1);
        scenario.put("chapters", chapters);
        
        // Add chapters manually
        Map<String, Object> chapter1 = new HashMap<>();
        chapter1.put("id", 1);
        chapter1.put("text", "Vous grimpez les marches quatre à quatre...");
        List<Map<String, Object>> choices1 = new ArrayList<>();
        
        Map<String, Object> choice1 = new HashMap<>();
        choice1.put("text", "frapper à l'aide du marteau");
        choice1.put("nextChapterId", 357);
        choices1.add(choice1);
        
        Map<String, Object> choice2 = new HashMap<>();
        choice2.put("text", "tirer le cordon de sonnette");
        choice2.put("nextChapterId", 275);
        choices1.add(choice2);
        
        chapter1.put("choices", choices1);
        chapters.add(chapter1);
        
        // Add more chapters as needed...
        
        return scenario;
    }

    private static Chapter parseChapter(Map<String, Object> chapterMap) {
        try {
            int id = ((Number) chapterMap.get("id")).intValue();
            String text = (String) chapterMap.get("text");
            Chapter chapter = new Chapter(id, text);

            // Parse endurance modifier
            if (chapterMap.containsKey("enduranceModifier")) {
                chapter.setEnduranceModifier(((Number) chapterMap.get("enduranceModifier")).intValue());
            }

            // Parse fear modifier
            if (chapterMap.containsKey("fearModifier")) {
                chapter.setFearModifier(((Number) chapterMap.get("fearModifier")).intValue());
            }

            // Parse luck test
            if (chapterMap.containsKey("luckTest")) {
                Map<String, Object> luckTestMap = (Map<String, Object>) chapterMap.get("luckTest");
                LuckTestOutcome success = null;
                LuckTestOutcome failure = null;

                if (luckTestMap.containsKey("success")) {
                    Map<String, Object> successMap = (Map<String, Object>) luckTestMap.get("success");
                    success = new LuckTestOutcome(
                        (String) successMap.get("text"),
                        ((Number) successMap.get("enduranceModifier")).intValue()
                    );
                }

                if (luckTestMap.containsKey("failure")) {
                    Map<String, Object> failureMap = (Map<String, Object>) luckTestMap.get("failure");
                    failure = new LuckTestOutcome(
                        (String) failureMap.get("text"),
                        ((Number) failureMap.get("enduranceModifier")).intValue()
                    );
                }

                if (success != null || failure != null) {
                    chapter.setLuckTest(new LuckTest(success, failure));
                }
            }

            // Parse choices
            if (chapterMap.containsKey("choices")) {
                List<Map<String, Object>> choicesMap = (List<Map<String, Object>>) chapterMap.get("choices");
                for (Map<String, Object> choiceMap : choicesMap) {
                    try {
                        Choice choice = parseChoice(choiceMap);
                        if (choice != null) {
                            chapter.addChoice(choice);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing choice: " + e.getMessage());
                    }
                }
            }

            return chapter;
        } catch (Exception e) {
            System.err.println("Error creating chapter: " + e.getMessage());
            return null;
        }
    }

    private static Choice parseChoice(Map<String, Object> choiceMap) {
        try {
            String text = (String) choiceMap.get("text");
            int nextChapterId = ((Number) choiceMap.get("nextChapterId")).intValue();
            Choice choice = new Choice(text, nextChapterId);

            if (choiceMap.containsKey("requiresLuckTest")) {
                choice.setRequiresLuckTest((Boolean) choiceMap.get("requiresLuckTest"));
            }

            if (choiceMap.containsKey("combatRequired")) {
                choice.setCombatRequired((Boolean) choiceMap.get("combatRequired"));
            }

            return choice;
        } catch (Exception e) {
            System.err.println("Error creating choice: " + e.getMessage());
            return null;
        }
    }

    private static Scenario createFallbackScenario() {
        Scenario fallback = new Scenario("Scénario de secours", 1);
        Chapter errorChapter = new Chapter(1, "Erreur: Impossible de charger le scénario principal.");
        fallback.addChapter(errorChapter);
        return fallback;
    }
}