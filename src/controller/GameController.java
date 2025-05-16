package controller;

import model.Chapter;
import model.Player;
import model.Scenario;

public class GameController {
    private Scenario scenario;
    private Player player;
    private int currentChapterId;

    public GameController(Scenario scenario, Player player) {
        this.scenario = scenario;
        this.player = player;
        this.currentChapterId = scenario.getStartChapterId();
    }

    public Chapter getCurrentChapter() {
        return scenario.getChapter(currentChapterId);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean makeChoice(int choiceIndex) {
        Chapter current = getCurrentChapter();
        if (choiceIndex >= 0 && choiceIndex < current.getChoices().size()) {
            currentChapterId = current.getChoices().get(choiceIndex).getNextChapterId();
            return true;
        }
        return false;
    }

    public boolean isGameOver() {
        return currentChapterId == -1 || scenario.getChapter(currentChapterId) == null;
    }
}