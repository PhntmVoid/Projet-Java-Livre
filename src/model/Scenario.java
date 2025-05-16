package model;

import java.util.HashMap;
import java.util.Map;

public class Scenario {
    private String title;
    private Map<Integer, Chapter> chapters;
    private int startChapterId;

    public Scenario(String title, int startChapterId) {
        this.title = title;
        this.startChapterId = startChapterId;
        this.chapters = new HashMap<>();
    }

    // Getters et setters
    public String getTitle() { return title; }
    public Map<Integer, Chapter> getChapters() { return chapters; }
    public int getStartChapterId() { return startChapterId; }

    public void addChapter(Chapter chapter) {
        chapters.put(chapter.getId(), chapter);
    }

    public Chapter getChapter(int id) {
        return chapters.get(id);
    }
}