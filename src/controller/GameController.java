package controller;

import model.Chapter;
import model.Player;
import model.Scenario;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private Scenario scenario;
    private Player player;
    private int currentChapterId;
    private List<Integer> chapterHistory;

    public GameController(Scenario scenario, Player player) {
        if (scenario == null || player == null) {
            throw new IllegalArgumentException("Scenario and Player cannot be null");
        }

        this.scenario = scenario;
        this.player = player;
        this.chapterHistory = new ArrayList<>();

        initializeStartingChapter();
    }

    private void initializeStartingChapter() {
        this.currentChapterId = scenario.getStartChapterId();
        chapterHistory.add(currentChapterId);

        if (scenario.getChapter(currentChapterId) == null) {
            throw new IllegalStateException("Starting chapter not found in scenario");
        }
    }

    public Chapter getCurrentChapter() {
        Chapter chapter = scenario.getChapter(currentChapterId);
        return chapter != null ? chapter : createErrorChapter();
    }

    private Chapter createErrorChapter() {
        return new Chapter(-999, "Erreur: Le chemin que vous avez choisi ne mène nulle part.\n\n" +
                "Vous vous retrouvez dans un endroit étrange et vide. L'aventure semble s'être arrêtée brusquement.\n\n" +
                "(Ceci est une erreur technique - le chapitre suivant n'existe pas dans le scénario)");
    }

    public Player getPlayer() {
        return player;
    }

    public boolean makeChoice(int choiceIndex) {
        Chapter current = getCurrentChapter();
        if (current == null || choiceIndex < 0 || choiceIndex >= current.getChoices().size()) {
            return false;
        }

        int nextChapterId = current.getChoices().get(choiceIndex).getNextChapterId();
        Chapter nextChapter = scenario.getChapter(nextChapterId);

        if (nextChapterId == -1 || nextChapter == null) {
            // Create a default "invalid path" chapter if the next chapter doesn't exist
            currentChapterId = -999;
            chapterHistory.add(currentChapterId);
            return true;
        }

        currentChapterId = nextChapterId;
        chapterHistory.add(currentChapterId);
        return true;
    }

    public boolean goBack() {
        if (chapterHistory.size() > 1) {
            chapterHistory.remove(chapterHistory.size() - 1);
            currentChapterId = chapterHistory.get(chapterHistory.size() - 1);
            return true;
        }
        return false;
    }

    public boolean isGameOver() {
        return !player.isAlive() ||
                currentChapterId == -1 ||
                currentChapterId == -999 || // Add this line
                getCurrentChapter().getChoices().isEmpty();
    }

    public boolean isPlayerAlive() {
        return player.isAlive();
    }
}