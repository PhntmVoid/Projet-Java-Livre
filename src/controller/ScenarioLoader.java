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
                JSONObject chapterJson = chapters.getJSONObject(i);
                
                int id = chapterJson.getInt("id");
                String text = chapterJson.getString("text");
                Chapter chapter = new Chapter(id, text);
                
                // Handle endurance modifier
                if (chapterJson.has("enduranceModifier")) {
                    chapter.setEnduranceModifier(chapterJson.getInt("enduranceModifier"));
                }
                
                // Handle fear modifier
                if (chapterJson.has("fearModifier")) {
                    chapter.setFearModifier(chapterJson.getInt("fearModifier"));
                }
                
                // Handle luck test
                if (chapterJson.has("luckTest")) {
                    JSONObject luckTest = chapterJson.getJSONObject("luckTest");
                    LuckTestOutcome success = null;
                    LuckTestOutcome failure = null;
                    
                    if (luckTest.has("success")) {
                        JSONObject successJson = luckTest.getJSONObject("success");
                        success = new LuckTestOutcome(
                            successJson.getString("text"),
                            successJson.getInt("enduranceModifier")
                        );
                    }
                    
                    if (luckTest.has("failure")) {
                        JSONObject failureJson = luckTest.getJSONObject("failure");
                        failure = new LuckTestOutcome(
                            failureJson.getString("text"),
                            failureJson.getInt("enduranceModifier")
                        );
                    }
                    
                    chapter.setLuckTest(new LuckTest(success, failure));
                }
                
                // Handle choices
                if (chapterJson.has("choices")) {
                    JSONArray choices = chapterJson.getJSONArray("choices");
                    for (int j = 0; j < choices.length(); j++) {
                        JSONObject choiceJson = choices.getJSONObject(j);
                        
                        String choiceText = choiceJson.getString("text");
                        int nextChapterId = choiceJson.getInt("nextChapterId");
                        
                        Choice choice = new Choice(choiceText, nextChapterId);
                        
                        if (choiceJson.has("requiresLuckTest")) {
                            choice.setRequiresLuckTest(choiceJson.getBoolean("requiresLuckTest"));
                        }
                        
                        if (choiceJson.has("combatRequired")) {
                            choice.setCombatRequired(choiceJson.getBoolean("combatRequired"));
                        }
                        
                        chapter.addChoice(choice);
                    }
                }
                
                scenario.addChapter(chapter);
            }
            
            return scenario;
        } catch (Exception e) {
            System.err.println("Failed to load scenario: " + e.getMessage());
            e.printStackTrace();
            return createFallbackScenario();
        }
    }

    private static Scenario createFallbackScenario() {
        Scenario fallback = new Scenario("Scénario de secours", 1);
        Chapter errorChapter = new Chapter(1, "Erreur: Impossible de charger le scénario principal.");
        fallback.addChapter(errorChapter);
        return fallback;
    }
}