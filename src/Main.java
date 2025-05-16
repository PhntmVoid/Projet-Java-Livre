import controller.GameController;
import controller.ScenarioLoader;
import model.Player;
import model.Scenario;
import view.SwingUI;
import view.TextUI;

public class Main {
    public static void main(String[] args) {
        // Chargement du scénario
        Scenario scenario = ScenarioLoader.loadScenario("resources/manoir_enfer.json");

        // Création du joueur avec des valeurs initiales
        Player player = new Player(7, 20); // Habileté: 7, Endurance: 20

        // Création du contrôleur
        GameController controller = new GameController(scenario, player);

        // Choix de l'interface
        if (args.length > 0 && args[0].equals("--text")) {
            new TextUI(controller).startGame();
        } else {
            new SwingUI(controller);
        }
    }
}