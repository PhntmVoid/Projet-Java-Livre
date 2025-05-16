package view;

import controller.GameController;
import model.Chapter;
import model.Choice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingUI {
    private JFrame frame;
    private JTextArea chapterText;
    private JPanel choicesPanel;
    private GameController controller;

    public SwingUI(GameController controller) {
        this.controller = controller;
        initialize();
        updateUI();
    }

    private void initialize() {
        frame = new JFrame("Le Manoir de l'Enfer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        chapterText = new JTextArea();
        chapterText.setEditable(false);
        chapterText.setLineWrap(true);
        chapterText.setWrapStyleWord(true);
        chapterText.setFont(new Font("Serif", Font.PLAIN, 16));
        chapterText.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(chapterText);
        frame.add(scrollPane, BorderLayout.CENTER);

        choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        frame.add(new JScrollPane(choicesPanel), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void updateUI() {
        Chapter currentChapter = controller.getCurrentChapter();
        chapterText.setText(currentChapter.getText());

        choicesPanel.removeAll();

        for (int i = 0; i < currentChapter.getChoices().size(); i++) {
            Choice choice = currentChapter.getChoices().get(i);
            JButton choiceButton = new JButton(choice.getText());
            final int choiceIndex = i;

            choiceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.makeChoice(choiceIndex);
                    if (controller.isGameOver()) {
                        chapterText.setText("Fin de l'aventure. Merci d'avoir joué!");
                        choicesPanel.removeAll();
                    } else {
                        updateUI();
                    }
                }
            });

            choicesPanel.add(choiceButton);
        }

        choicesPanel.revalidate();
        choicesPanel.repaint();
    }
}