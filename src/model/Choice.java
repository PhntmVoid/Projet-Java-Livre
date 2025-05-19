package model;

public class Choice {
    private String text;
    private int nextChapterId;
    private boolean requiresLuckTest;
    private boolean combatRequired;

    public Choice(String text, int nextChapterId) {
        this.text = text;
        this.nextChapterId = nextChapterId;
        this.requiresLuckTest = false;
        this.combatRequired = false;
    }

    // Getters and setters
    public String getText() { return text; }
    public int getNextChapterId() { return nextChapterId; }
    public boolean isRequiresLuckTest() { return requiresLuckTest; }
    public boolean isCombatRequired() { return combatRequired; }

    public void setRequiresLuckTest(boolean requiresLuckTest) { this.requiresLuckTest = requiresLuckTest; }
    public void setCombatRequired(boolean combatRequired) { this.combatRequired = combatRequired; }
}