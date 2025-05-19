package controller;

import model.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class ScenarioLoader {
    public static Scenario loadScenario(String resourcePath) {
        try (InputStream is = ScenarioLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            String jsonContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonContent);
            
            String title = json.getString("title");
            int startChapterId = json.getInt("startChapterId");
            
            Scenario scenario = new Scenario(title, startChapterId);
            
            JSONArray chapters = json.getJSONArray("chapters");
            for (int i = 0; i < chapters.length(); i++) {
                try {
                    JSONObject chapterJson = chapters.getJSONObject(i);
                    Chapter chapter = parseChapter(chapterJson);
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

    private static Chapter parseChapter(JSONObject chapterJson) {
        try {
            int id = chapterJson.getInt("id");
            String text = chapterJson.getString("text");
            Chapter chapter = new Chapter(id, text);

            // Parse endurance modifier
            if (chapterJson.has("enduranceModifier")) {
                chapter.setEnduranceModifier(chapterJson.getInt("enduranceModifier"));
            }

            // Parse fear modifier
            if (chapterJson.has("fearModifier")) {
                chapter.setFearModifier(chapterJson.getInt("fearModifier"));
            }

            // Parse luck test
            if (chapterJson.has("luckTest")) {
                JSONObject luckTestJson = chapterJson.getJSONObject("luckTest");
                LuckTestOutcome success = null;
                LuckTestOutcome failure = null;

                if (luckTestJson.has("success")) {
                    JSONObject successJson = luckTestJson.getJSONObject("success");
                    success = new LuckTestOutcome(
                        successJson.getString("text"),
                        successJson.getInt("enduranceModifier")
                    );
                }

                if (luckTestJson.has("failure")) {
                    JSONObject failureJson = luckTestJson.getJSONObject("failure");
                    failure = new LuckTestOutcome(
                        failureJson.getString("text"),
                        failureJson.getInt("enduranceModifier")
                    );
                }

                if (success != null || failure != null) {
                    chapter.setLuckTest(new LuckTest(success, failure));
                }
            }

            // Parse choices
            if (chapterJson.has("choices")) {
                JSONArray choicesJson = chapterJson.getJSONArray("choices");
                for (int i = 0; i < choicesJson.length(); i++) {
                    try {
                        JSONObject choiceJson = choicesJson.getJSONObject(i);
                        Choice choice = parseChoice(choiceJson);
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

    private static Choice parseChoice(JSONObject choiceJson) {
        try {
            String text = choiceJson.getString("text");
            int nextChapterId = choiceJson.getInt("nextChapterId");
            Choice choice = new Choice(text, nextChapterId);

            if (choiceJson.has("requiresLuckTest")) {
                choice.setRequiresLuckTest(choiceJson.getBoolean("requiresLuckTest"));
            }

            if (choiceJson.has("combatRequired")) {
                choice.setCombatRequired(choiceJson.getBoolean("combatRequired"));
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