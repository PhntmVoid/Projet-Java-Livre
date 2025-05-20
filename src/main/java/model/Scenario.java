package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Scenario {
    private String title;
    private int startChapterId;
    private Map<Integer, Chapter> chapters;

    public Scenario(String title, int startChapterId) {
        this.title = title;
        this.startChapterId = startChapterId;
        this.chapters = new HashMap<>();
    }

    // Getters et setters
    public int getStartChapterId() { return startChapterId; }

    public void addChapter(Chapter chapter) {
        chapters.put(chapter.getId(), chapter);
    }

    public Chapter getChapter(int id) {
        return chapters.get(id);
    }
}