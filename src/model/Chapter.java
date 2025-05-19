package model;

import java.util.ArrayList;
import java.util.List;

public class Chapter {
    private int id;
    private String text;
    private List<Choice> choices;
    private int enduranceModifier;
    private int fearModifier;
    private LuckTest luckTest;

    public Chapter(int id, String text) {
        this.id = id;
        this.text = text;
        this.choices = new ArrayList<>();
        this.enduranceModifier = 0;
        this.fearModifier = 0;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getText() { return text; }
    public List<Choice> getChoices() { return choices; }
    public int getEnduranceModifier() { return enduranceModifier; }
    public int getFearModifier() { return fearModifier; }
    public LuckTest getLuckTest() { return luckTest; }

    public void setEnduranceModifier(int modifier) { this.enduranceModifier = modifier; }
    public void setFearModifier(int modifier) { this.fearModifier = modifier; }
    public void setLuckTest(LuckTest test) { this.luckTest = test; }

    public void addChoice(Choice choice) {
        choices.add(choice);
    }
}