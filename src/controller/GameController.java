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

        // Vérifier que le chapitre de départ existe
        if (scenario.getChapter(currentChapterId) == null) {
            System.err.println("ERREUR: Le chapitre de départ ID " + currentChapterId + " n'existe pas!");
            // Si le chapitre de départ n'existe pas, utiliser le premier chapitre disponible ou -1
            if (!scenario.getChapters().isEmpty()) {
                currentChapterId = scenario.getChapters().keySet().iterator().next();
            } else {
                currentChapterId = -1;
            }
        }
    }

    public Chapter getCurrentChapter() {
        Chapter chapter = scenario.getChapter(currentChapterId);
        if (chapter == null) {
            // Retourner un chapitre d'erreur au lieu de null
            return createErrorChapter();
        }
        return chapter;
    }

    private Chapter createErrorChapter() {
        Chapter errorChapter = new Chapter(-999, "Erreur: Ce chapitre n'existe pas dans le scénario.");
        return errorChapter;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean makeChoice(int choiceIndex) {
        Chapter current = getCurrentChapter();
        if (choiceIndex >= 0 && choiceIndex < current.getChoices().size()) {
            int nextChapterId = current.getChoices().get(choiceIndex).getNextChapterId();

            // Vérifier que le chapitre suivant existe
            if (scenario.getChapter(nextChapterId) != null || nextChapterId == -1) {
                currentChapterId = nextChapterId;
                return true;
            } else {
                System.err.println("ERREUR: Le chapitre cible ID " + nextChapterId + " n'existe pas!");
                return false;
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return currentChapterId == -1 || getCurrentChapter().getChoices().isEmpty();
    }
}