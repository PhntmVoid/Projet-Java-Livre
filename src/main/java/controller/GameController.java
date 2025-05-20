package controller;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {
    private Scenario scenario;
    private Player player;
    private int currentChapterId;
    private List<Integer> chapterHistory;
    private Random random;
    private Combat currentCombat;

    public GameController(Scenario scenario, Player player) {
        if (scenario == null || player == null) {
            throw new IllegalArgumentException("Scenario and Player cannot be null");
        }

        this.scenario = scenario;
        this.player = player;
        this.chapterHistory = new ArrayList<>();
        this.random = new Random();

        initializeStartingChapter();
    }

    public void loadState(SaveManager.GameState state) {
        this.player = state.getPlayer();
        this.currentChapterId = state.getCurrentChapterId();
        this.chapterHistory = new ArrayList<>(state.getChapterHistory());
    }

    public Scenario getScenario() { return scenario; }
    public Player getPlayer() { return player; }
    public int getCurrentChapterId() { return currentChapterId; }
    public List<Integer> getChapterHistory() { return new ArrayList<>(chapterHistory); }

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

    public Combat getCurrentCombat() {
        return currentCombat;
    }

    public boolean makeChoice(int choiceIndex) {
        Chapter current = getCurrentChapter();
        if (current == null || choiceIndex < 0 || choiceIndex >= current.getChoices().size()) {
            return false;
        }

        Choice choice = current.getChoices().get(choiceIndex);

        // Apply modifiers from current chapter
        if (current.getEnduranceModifier() != 0) {
            player.modifyStamina(current.getEnduranceModifier());
        }
        if (current.getFearModifier() != 0) {
            player.modifyFear(current.getFearModifier());
            if (choice.isCombatRequired() && player.getCurrentFear() >= player.getMaxFear() * 0.7) {
                player.modifySkill(-1);
            }
        }

        // Handle luck test
        boolean luckTestPassed = true;
        if (choice.isRequiresLuckTest()) {
            luckTestPassed = testLuck();
            LuckTest luckTest = current.getLuckTest();
            if (luckTest != null) {
                if (luckTestPassed && luckTest.getSuccess() != null) {
                    LuckTestOutcome success = luckTest.getSuccess();
                    player.modifyStamina(success.getEnduranceModifier());
                } else if (!luckTestPassed && luckTest.getFailure() != null) {
                    LuckTestOutcome failure = luckTest.getFailure();
                    player.modifyStamina(failure.getEnduranceModifier());
                }
            }
        }

        // Handle combat
        if (choice.isCombatRequired()) {
            Enemy enemy = current.getEnemy();
            if (enemy != null) {
                currentCombat = new Combat(player, enemy);
                if (!player.isAlive()) {
                    currentChapterId = -1;
                    chapterHistory.add(currentChapterId);
                    return true;
                }
            }
        } else {
            currentCombat = null;
        }

        int nextChapterId = choice.getNextChapterId();
        Chapter nextChapter = scenario.getChapter(nextChapterId);

        if (nextChapterId == -1 || nextChapter == null) {
            currentChapterId = -999;
            chapterHistory.add(currentChapterId);
            return true;
        }

        currentChapterId = nextChapterId;
        chapterHistory.add(currentChapterId);
        return true;
    }

    public boolean testLuck() {
        player.modifyLuck(-1);
        int roll = random.nextInt(6) + 1 + random.nextInt(6) + 1;
        return roll <= player.getLuck();
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
                currentChapterId == -999 ||
                getCurrentChapter().getChoices().isEmpty();
    }

    public boolean isPlayerAlive() {
        return player.isAlive();
    }
}