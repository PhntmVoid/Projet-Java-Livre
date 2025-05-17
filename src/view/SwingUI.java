package view;

import controller.GameController;
import model.Chapter;
import model.Choice;
import model.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SwingUI {
    private JFrame frame;
    private JTextArea chapterText;
    private JPanel choicesPanel;
    private JPanel statsPanel;
    private JLabel chapterLabel;
    private JPanel mainMenuPanel;
    private JPanel gamePanel;
    private List<JButton> choiceButtons;
    private GameController controller;

    // Couleurs et apparence
    private final Color TEAL_COLOR = new Color(0, 150, 170);
    private final Color DARK_GREY = new Color(30, 30, 30);
    private final Color WHITE = new Color(255, 255, 255);
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
    private final Font TEXT_FONT = new Font("Serif", Font.PLAIN, 14);
    private final Font STATS_FONT = new Font("SansSerif", Font.PLAIN, 14);

    public SwingUI(GameController controller) {
        this.controller = controller;
        this.choiceButtons = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        // Configuration de la fenêtre principale
        frame = new JFrame("Le Manoir de l'Enfer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLayout(new CardLayout());

        // Création du menu principal
        createMainMenu();

        // Création de l'interface de jeu
        createGameInterface();

        // Afficher d'abord le menu principal
        frame.add(mainMenuPanel, "MENU");
        frame.add(gamePanel, "GAME");

        ((CardLayout) frame.getContentPane().getLayout()).show(frame.getContentPane(), "MENU");
        frame.setVisible(true);
    }

    private void createMainMenu() {
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new BorderLayout());
        mainMenuPanel.setBackground(new Color(200, 200, 200));

        // Panneau central avec les boutons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(150, 0, 150, 0));

        // Bouton Jouer
        JButton playButton = createMenuButton("Jouer");
        playButton.addActionListener(e -> {
            ((CardLayout) frame.getContentPane().getLayout()).show(frame.getContentPane(), "GAME");
            updateUI(); // Charger le premier chapitre
        });

        // Bouton Règles
        JButton rulesButton = createMenuButton("Règles");
        rulesButton.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                "Bienvenue dans Le Manoir de l'Enfer!\n\n" +
                        "Dans ce livre-jeu, vous prenez des décisions qui influencent votre aventure.\n" +
                        "Votre personnage dispose de caractéristiques (Habileté, Endurance) qui peuvent\n" +
                        "être affectées au cours de l'aventure. Si votre endurance tombe à zéro, vous mourrez.\n\n" +
                        "Bonne chance dans cette périlleuse aventure!",
                "Règles du jeu", JOptionPane.INFORMATION_MESSAGE));

        // Bouton Quitter
        JButton quitButton = createMenuButton("Quitter");
        quitButton.addActionListener(e -> System.exit(0));

        // Ajout des boutons au panneau
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(playButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(rulesButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(quitButton);
        buttonsPanel.add(Box.createVerticalGlue());

        // Centrer les boutons
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (Component comp : buttonsPanel.getComponents()) {
            if (comp instanceof JButton) {
                ((JButton) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        }

        mainMenuPanel.add(buttonsPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(TEAL_COLOR);
        button.setForeground(WHITE);
        button.setFont(TITLE_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        return button;
    }

    private void createGameInterface() {
        gamePanel = new JPanel(new BorderLayout());

        // Panneau principal divisé en trois parties
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Panneau pour le texte du chapitre
        JPanel chapterPanel = new JPanel(new BorderLayout());
        chapterPanel.setBackground(TEAL_COLOR);
        chapterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // En-tête avec bouton retour et numéro de chapitre
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JButton backButton = new JButton("←");
        backButton.setForeground(WHITE);
        backButton.setBackground(TEAL_COLOR);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            ((CardLayout) frame.getContentPane().getLayout()).show(frame.getContentPane(), "MENU");
        });

        chapterLabel = new JLabel("CHAPTER N", SwingConstants.CENTER);
        chapterLabel.setForeground(WHITE);
        chapterLabel.setFont(TITLE_FONT);

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(chapterLabel, BorderLayout.CENTER);

        chapterPanel.add(headerPanel, BorderLayout.NORTH);

        // Zone de texte pour le contenu du chapitre
        chapterText = new JTextArea("\"TEXT\"");
        chapterText.setEditable(false);
        chapterText.setLineWrap(true);
        chapterText.setWrapStyleWord(true);
        chapterText.setFont(TEXT_FONT);
        chapterText.setMargin(new Insets(20, 20, 20, 20));
        chapterText.setBackground(TEAL_COLOR);
        chapterText.setForeground(WHITE);

        JScrollPane scrollPane = new JScrollPane(chapterText);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(TEAL_COLOR);

        chapterPanel.add(scrollPane, BorderLayout.CENTER);

        // Panneau pour les choix
        choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        choicesPanel.setBackground(new Color(220, 220, 220));
        choicesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panneau pour les statistiques du joueur
        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(TEAL_COLOR);
        statsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        statsPanel.setPreferredSize(new Dimension(150, frame.getHeight()));

        JLabel statsTitle = new JLabel("Stats");
        statsTitle.setForeground(WHITE);
        statsTitle.setFont(TITLE_FONT);
        statsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        statsPanel.add(statsTitle);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        String[] statLabels = {"Habileté : n/N", "Stamina : n/N", "Chance : N", "Peur : n/N", "Inventaire"};
        for (String label : statLabels) {
            JLabel statLabel = new JLabel(label);
            statLabel.setForeground(WHITE);
            statLabel.setFont(STATS_FONT);
            statLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsPanel.add(statLabel);
            statsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        // Assemblage des panneaux
        centerPanel.add(chapterPanel, BorderLayout.CENTER);
        centerPanel.add(choicesPanel, BorderLayout.SOUTH);

        gamePanel.add(centerPanel, BorderLayout.CENTER);
        gamePanel.add(statsPanel, BorderLayout.EAST);
    }

    private void updateUI() {
        try {
            Chapter currentChapter = controller.getCurrentChapter();
            if (currentChapter == null) {
                showErrorMessage("Erreur : Impossible de charger le chapitre courant.");
                return;
            }

            // Mise à jour du texte et du numéro de chapitre
            chapterText.setText(currentChapter.getText());
            chapterLabel.setText("CHAPITRE " + currentChapter.getId());

            // Mise à jour des boutons de choix
            choicesPanel.removeAll();
            choiceButtons.clear();

            // Si pas de choix, c'est la fin du jeu
            if (currentChapter.getChoices().isEmpty()) {
                JButton endButton = createChoiceButton("Retour au menu principal");
                endButton.addActionListener(e -> {
                    ((CardLayout) frame.getContentPane().getLayout()).show(frame.getContentPane(), "MENU");
                });

                choicesPanel.add(endButton);
            } else {
                // Ajouter les choix disponibles
                for (int i = 0; i < currentChapter.getChoices().size(); i++) {
                    Choice choice = currentChapter.getChoices().get(i);
                    JButton choiceButton = createChoiceButton(choice.getText());

                    final int choiceIndex = i;
                    choiceButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            controller.makeChoice(choiceIndex);
                            if (controller.isGameOver()) {
                                showGameOver();
                            } else {
                                updateUI();
                            }
                        }
                    });

                    choicesPanel.add(choiceButton);
                    choiceButtons.add(choiceButton);

                    // Ajouter des points de séparation entre les boutons (sauf pour le dernier)
                    if (i < currentChapter.getChoices().size() - 1) {
                        JPanel dotsPanel = new JPanel();
                        dotsPanel.setLayout(new BoxLayout(dotsPanel, BoxLayout.Y_AXIS));
                        dotsPanel.setOpaque(false);

                        for (int j = 0; j < 3; j++) {
                            JLabel dot = new JLabel("•");
                            dot.setFont(new Font("SansSerif", Font.BOLD, 24));
                            dot.setForeground(TEAL_COLOR);
                            dot.setAlignmentX(Component.CENTER_ALIGNMENT);
                            dotsPanel.add(dot);
                        }

                        choicesPanel.add(dotsPanel);
                    }
                }
            }

            // Mise à jour des statistiques du joueur
            updatePlayerStats();

            choicesPanel.revalidate();
            choicesPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Une erreur s'est produite : " + e.getMessage());
        }
    }

    private JButton createChoiceButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(TEAL_COLOR);
        button.setForeground(WHITE);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(frame.getWidth(), 50));

        return button;
    }

    private void updatePlayerStats() {
        Player player = controller.getPlayer();
        if (player != null) {
            // Mettre à jour les statistiques dans le panneau stats
            Component[] components = statsPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    String text = label.getText();

                    if (text.startsWith("Habileté")) {
                        label.setText("Habileté : " + player.getSkill() + "/" + player.getSkill());
                    } else if (text.startsWith("Stamina")) {
                        label.setText("Stamina : " + player.getStamina() + "/" + player.getStamina());
                    }
                }
            }
        }
    }

    private void showGameOver() {
        chapterText.setText("FIN DE L'AVENTURE\n\nVotre périple dans le Manoir de l'Enfer s'achève ici.");
        choicesPanel.removeAll();

        JButton restartButton = createChoiceButton("Retour au menu principal");
        restartButton.addActionListener(e -> {
            ((CardLayout) frame.getContentPane().getLayout()).show(frame.getContentPane(), "MENU");
        });

        choicesPanel.add(restartButton);
        choicesPanel.revalidate();
        choicesPanel.repaint();
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}