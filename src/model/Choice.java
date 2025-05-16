package model;

public class Choice {
    private String text;
    private int nextChapterId;

    public Choice(String text, int nextChapterId) {
        this.text = text;
        this.nextChapterId = nextChapterId;
    }

    // Getters
    public String getText() { return text; }
    public int getNextChapterId() { return nextChapterId; }
}