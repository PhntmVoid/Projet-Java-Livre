package view;

import controller.GameController;
import model.Chapter;
import model.Choice;

import java.util.Scanner;

public class TextUI {
    private GameController controller;
    private Scanner scanner;

    public TextUI(GameController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        while (!controller.isGameOver()) {
            Chapter currentChapter = controller.getCurrentChapter();

            System.out.println("\n=== " + controller.getPlayer().getCurrentStamina() + " ENDURANCE ===\n");
            System.out.println(currentChapter.getText());

            if (currentChapter.getChoices().isEmpty()) {
                System.out.println("\nFin de l'aventure.");
                break;
            }

            System.out.println("\nChoix possibles:");
            for (int i = 0; i < currentChapter.getChoices().size(); i++) {
                System.out.println((i + 1) + ". " + currentChapter.getChoices().get(i).getText());
            }

            System.out.print("\nVotre choix: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine()) - 1;
                if (choice >= 0 && choice < currentChapter.getChoices().size()) {
                    controller.makeChoice(choice);
                } else {
                    System.out.println("Choix invalide!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre!");
            }
        }

        System.out.println("\n=== GAME OVER ===");
        scanner.close();
    }
}