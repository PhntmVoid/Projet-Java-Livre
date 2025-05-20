import controller.GameController;
import controller.ScenarioLoader;
import model.Player;
import model.Scenario;
import view.SwingUI;
import view.TextUI;

public class Main {
    public static void main(String[] args) {
        try {
            // Load scenario
            Scenario scenario = ScenarioLoader.loadScenario("manoir_enfer.json");

            // Create player with initial stats
            Player player = new Player(10, 20, 12); // Skill: 7, Stamina: 20, Luck: 7

            // Create controller
            GameController controller = new GameController(scenario, player);

            // Choose interface
            if (args.length > 0 && args[0].equals("--text")) {
                new TextUI(controller).startGame();
            } else {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    new SwingUI(controller);
                });
            }
        } catch (Exception e) {
            System.err.println("Failed to start the game: " + e.getMessage());
            e.printStackTrace();
        }
    }
}