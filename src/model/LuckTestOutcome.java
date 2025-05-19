package model;

public class LuckTestOutcome {
    private String text;
    private int enduranceModifier;

    public LuckTestOutcome(String text, int enduranceModifier) {
        this.text = text;
        this.enduranceModifier = enduranceModifier;
    }

    public String getText() { return text; }
    public int getEnduranceModifier() { return enduranceModifier; }
}