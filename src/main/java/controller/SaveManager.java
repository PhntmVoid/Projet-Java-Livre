package controller;

import model.Player;
import model.Scenario;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private static final String SAVE_DIR = "saves";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public static void initialize() {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create saves directory: " + e.getMessage());
        }
    }

    public static void saveGame(GameController gameController, String saveName) throws IOException {
        String filename = saveName.isEmpty() 
            ? LocalDateTime.now().format(DATE_FORMAT) + ".sav"
            : saveName + ".sav";
        
        Path savePath = Paths.get(SAVE_DIR, filename);
        
        GameState state = new GameState(
            gameController.getPlayer(),
            gameController.getCurrentChapterId(),
            gameController.getChapterHistory()
        );
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(savePath)))) {
            oos.writeObject(state);
        }
    }

    public static GameController loadGame(String filename, Scenario scenario) throws IOException, ClassNotFoundException {
        Path savePath = Paths.get(SAVE_DIR, filename);
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(savePath)))) {
            GameState state = (GameState) ois.readObject();
            return new GameController(scenario, state);
        }
    }

    public static List<String> getSaveFiles() {
        List<String> saves = new ArrayList<>();
        try {
            Files.list(Paths.get(SAVE_DIR))
                .filter(path -> path.toString().endsWith(".sav"))
                .map(path -> path.getFileName().toString())
                .forEach(saves::add);
        } catch (IOException e) {
            System.err.println("Failed to list save files: " + e.getMessage());
        }
        return saves;
    }

    private static class GameState implements Serializable {
        private final Player player;
        private final int currentChapterId;
        private final List<Integer> chapterHistory;

        public GameState(Player player, int currentChapterId, List<Integer> chapterHistory) {
            this.player = player;
            this.currentChapterId = currentChapterId;
            this.chapterHistory = new ArrayList<>(chapterHistory);
        }

        public Player getPlayer() { return player; }
        public int getCurrentChapterId() { return currentChapterId; }
        public List<Integer> getChapterHistory() { return chapterHistory; }
    }
}