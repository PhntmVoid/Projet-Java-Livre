package model;

import java.util.ArrayList;
import java.util.List;

public class Chapter {
    private int id;
    private String text;
    private List<Choice> choices;

    public Chapter(int id, String text) {
        this.id = id;
        this.text = text;
        this.choices = new ArrayList<>();
    }

    // Getters et setters
    public int getId() { return id; }
    public String getText() { return text; }
    public List<Choice> getChoices() { return choices; }

    public void addChoice(Choice choice) {
        choices.add(choice);
    }
}