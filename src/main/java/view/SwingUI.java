package view;

import controller.GameController;
import model.*;
import model.Choice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SwingUI {
    private GameController controller;
    private JFrame frame;
    private JPanel mainMenuPanel;
    private JPanel gamePanel;
    private JPanel statsPanel;
    private JTextPane chapterTextArea;
    private JPanel choicesPanel;
    private JDialog combatDialog;

    private static final Font STATS_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Color TEAL_COLOR = new Color(0, 128, 128);
    private static final Color DARK_GREY = new Color(50, 50, 50);
    private static final Color CHOICE_BUTTON_COLOR = new Color(60, 60, 60);
    private static final Color WHITE = Color.WHITE;

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;
    private static final int STATS_PANEL_WIDTH = 200;

    public SwingUI(GameController controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Le Manoir de l'Enfer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.background", CHOICE_BUTTON_COLOR);
            UIManager.put("Button.foreground", WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.setLayout(new CardLayout());
        createMainMenu();
        createGameInterface();

        frame.add(mainMenuPanel, "MENU");
        frame.add(gamePanel, "GAME");

        showMainMenu();
        frame.setVisible(true);
    }

    private void createMainMenu() {
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new BorderLayout());
        mainMenuPanel.setBackground(DARK_GREY);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Le Manoir de l'Enfer");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));
        titleLabel.setForeground(WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = new JButton("Commencer l'aventure");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMaximumSize(new Dimension(300, 50));
        configureButton(startButton, TEAL_COLOR, WHITE);
        startButton.addActionListener(e -> showPlayerCreation());

        JButton rulesButton = new JButton("Règles du jeu");
        rulesButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        rulesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rulesButton.setMaximumSize(new Dimension(300, 50));
        configureButton(rulesButton, TEAL_COLOR, WHITE);
        rulesButton.addActionListener(e -> showRules());

        JButton quitButton = new JButton("Quitter");
        quitButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setMaximumSize(new Dimension(300, 50));
        configureButton(quitButton, TEAL_COLOR, WHITE);
        quitButton.addActionListener(e -> System.exit(0));

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        centerPanel.add(startButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(rulesButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(quitButton);
        centerPanel.add(Box.createVerticalGlue());

        mainMenuPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void showRules() {
        JDialog rulesDialog = new JDialog(frame, "Règles du jeu", true);
        rulesDialog.setSize(600, 400);
        rulesDialog.setLocationRelativeTo(frame);

        JPanel rulesPanel = new JPanel();
        rulesPanel.setLayout(new BorderLayout());
        rulesPanel.setBackground(DARK_GREY);
        rulesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea rulesText = new JTextArea(
            "RÈGLES DU JEU\n\n" +
            "HABILETÉ, ENDURANCE ET CHANCE\n\n" +
            "Votre personnage est défini par trois caractéristiques principales :\n\n" +
            "HABILETÉ : Représente votre aptitude au combat.\n" +
            "ENDURANCE : Représente votre constitution et votre volonté de survivre.\n" +
            "CHANCE : Indique si vous êtes naturellement chanceux.\n\n" +
            "COMBAT\n\n" +
            "1. Calculez votre Force d'Attaque (Habileté + 2d6)\n" +
            "2. Calculez la Force d'Attaque de l'adversaire\n" +
            "3. Celui qui a la plus grande Force d'Attaque blesse l'autre\n" +
            "4. Le perdant perd 2 points d'ENDURANCE\n\n" +
            "CHANCE\n\n" +
            "Lancez 2 dés. Si le résultat est inférieur ou égal à votre CHANCE,\n" +
            "vous êtes Chanceux. Sinon, vous êtes Malchanceux.\n" +
            "Chaque test de Chance réduit votre score de 1 point."
        );
        rulesText.setEditable(false);
        rulesText.setBackground(DARK_GREY);
        rulesText.setForeground(WHITE);
        rulesText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(rulesText);
        scrollPane.setBorder(BorderFactory.createLineBorder(TEAL_COLOR));

        JButton closeButton = new JButton("Fermer");
        configureButton(closeButton, TEAL_COLOR, WHITE);
        closeButton.addActionListener(e -> rulesDialog.dispose());

        rulesPanel.add(scrollPane, BorderLayout.CENTER);
        rulesPanel.add(closeButton, BorderLayout.SOUTH);

        rulesDialog.add(rulesPanel);
        rulesDialog.setVisible(true);
    }

    private void showPlayerCreation() {
        JDialog dialog = new JDialog(frame, "Création du personnage", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(DARK_GREY);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Roll initial stats
        int skill = rollDice(1) + 6;    // 1d6 + 6
        int stamina = rollDice(2) + 12;  // 2d6 + 12
        int luck = rollDice(1) + 6;     // 1d6 + 6

        JLabel skillLabel = new JLabel("HABILETÉ: " + skill);
        JLabel staminaLabel = new JLabel("ENDURANCE: " + stamina);
        JLabel luckLabel = new JLabel("CHANCE: " + luck);

        skillLabel.setForeground(WHITE);
        staminaLabel.setForeground(WHITE);
        luckLabel.setForeground(WHITE);

        JButton rerollButton = new JButton("Relancer les dés");
        configureButton(rerollButton, TEAL_COLOR, WHITE);
        rerollButton.addActionListener(e -> {
            int newSkill = rollDice(1) + 6;
            int newStamina = rollDice(2) + 12;
            int newLuck = rollDice(1) + 6;
            skillLabel.setText("HABILETÉ: " + newSkill);
            staminaLabel.setText("ENDURANCE: " + newStamina);
            luckLabel.setText("CHANCE: " + newLuck);
        });

        JButton startButton = new JButton("Commencer l'aventure");
        configureButton(startButton, TEAL_COLOR, WHITE);
        startButton.addActionListener(e -> {
            int finalSkill = Integer.parseInt(skillLabel.getText().split(": ")[1]);
            int finalStamina = Integer.parseInt(staminaLabel.getText().split(": ")[1]);
            int finalLuck = Integer.parseInt(luckLabel.getText().split(": ")[1]);

            controller = new GameController(controller.getScenario(),
                new Player(finalSkill, finalStamina, finalLuck));
            dialog.dispose();
            showGameScreen();
        });

        panel.add(skillLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(staminaLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(luckLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(rerollButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(startButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private int rollDice(int number) {
        int total = 0;
        for (int i = 0; i < number; i++) {
            total += (int)(Math.random() * 6) + 1;
        }
        return total;
    }

    private void createGameInterface() {
        gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(DARK_GREY);

        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(40, 40, 40));
        statsPanel.setPreferredSize(new Dimension(STATS_PANEL_WIDTH, WINDOW_HEIGHT));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(DARK_GREY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        chapterTextArea = new JTextPane();
        chapterTextArea.setEditable(false);
        chapterTextArea.setContentType("text/plain");
        chapterTextArea.setFont(new Font("Serif", Font.PLAIN, 16));
        chapterTextArea.setBackground(DARK_GREY);
        chapterTextArea.setForeground(WHITE);
        chapterTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(chapterTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(TEAL_COLOR, 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(DARK_GREY);

        choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        choicesPanel.setBackground(DARK_GREY);
        choicesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(choicesPanel, BorderLayout.SOUTH);

        gamePanel.add(statsPanel, BorderLayout.WEST);
        gamePanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void showMainMenu() {
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "MENU");
    }

    private void showGameScreen() {
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), "GAME");
        frame.revalidate();
        frame.repaint();
        updateGameScreen();
    }

    private void updateGameScreen() {
        updatePlayerStats();
        updateChapterDisplay();
    }

    private void updatePlayerStats() {
        statsPanel.removeAll();

        Player player = controller.getPlayer();
        if (player == null) return;

        JLabel statsTitle = new JLabel("STATISTIQUES", SwingConstants.CENTER);
        statsTitle.setForeground(WHITE);
        statsTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        statsTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        statsPanel.add(statsTitle);

        addStatBar("Habileté", player.getCurrentSkill(), player.getMaxSkill());
        addStatBar("Endurance", player.getCurrentStamina(), player.getMaxStamina());
        addStatBar("Chance", player.getLuck(), 10);
        addStatBar("Peur", player.getCurrentFear(), player.getMaxFear());

        JLabel inventoryTitle = new JLabel("INVENTAIRE", SwingConstants.CENTER);
        inventoryTitle.setForeground(WHITE);
        inventoryTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        inventoryTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        statsPanel.add(inventoryTitle);

        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
        inventoryPanel.setOpaque(false);
        inventoryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        List<String> inventory = player.getInventory();
        if (inventory.isEmpty()) {
            JLabel emptyLabel = new JLabel("(vide)");
            emptyLabel.setForeground(WHITE);
            emptyLabel.setFont(STATS_FONT);
            inventoryPanel.add(emptyLabel);
        } else {
            for (String item : inventory) {
                JLabel itemLabel = new JLabel("- " + item);
                itemLabel.setForeground(WHITE);
                itemLabel.setFont(STATS_FONT);
                inventoryPanel.add(itemLabel);
            }
        }

        statsPanel.add(inventoryPanel);
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void addStatBar(String label, int current, int max) {
        JPanel statPanel = new JPanel();
        statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.Y_AXIS));
        statPanel.setOpaque(false);
        statPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel statLabel = new JLabel(label + ":");
        statLabel.setForeground(WHITE);
        statLabel.setFont(STATS_FONT);
        statLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar(0, max);
        progressBar.setValue(current);
        progressBar.setString(current + "/" + max);
        progressBar.setStringPainted(true);
        progressBar.setForeground(getColorForStat(label));
        progressBar.setBackground(DARK_GREY);
        progressBar.setBorder(BorderFactory.createLineBorder(WHITE, 1));
        progressBar.setPreferredSize(new Dimension(150, 20));
        progressBar.setMaximumSize(new Dimension(150, 20));

        statPanel.add(statLabel);
        statPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        statPanel.add(progressBar);

        statsPanel.add(statPanel);
    }

    private void configureButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                if (c.isOpaque()) {
                    g.setColor(c.getBackground());
                    g.fillRect(0, 0, c.getWidth(), c.getHeight());
                }
                paint(g, c);
            }
        });

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private Color getColorForStat(String stat) {
        switch (stat) {
            case "Habileté": return new Color(76, 175, 80);
            case "Endurance": return new Color(244, 67, 54);
            case "Chance": return new Color(255, 193, 7);
            case "Peur": return new Color(156, 39, 176);
            default: return TEAL_COLOR;
        }
    }

    private void handleCombat(Enemy enemy) {
        combatDialog = new JDialog(frame, "Combat!", true);
        combatDialog.setSize(400, 300);
        combatDialog.setLocationRelativeTo(frame);
        combatDialog.setLayout(new BorderLayout());

        JPanel combatPanel = new JPanel();
        combatPanel.setLayout(new BoxLayout(combatPanel, BoxLayout.Y_AXIS));
        combatPanel.setBackground(DARK_GREY);
        combatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel enemyLabel = new JLabel("Combat contre " + enemy.getName());
        enemyLabel.setForeground(WHITE);
        enemyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        enemyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statsLabel = new JLabel(String.format("Habileté: %d, Endurance: %d",
            enemy.getSkill(), enemy.getEndurance()));
        statsLabel.setForeground(WHITE);
        statsLabel.setFont(STATS_FONT);
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea combatLog = new JTextArea();
        combatLog.setEditable(false);
        combatLog.setBackground(DARK_GREY);
        combatLog.setForeground(WHITE);
        combatLog.setFont(STATS_FONT);
        JScrollPane scrollPane = new JScrollPane(combatLog);
        scrollPane.setPreferredSize(new Dimension(350, 150));

        JButton attackButton = new JButton("Attaquer");
        configureButton(attackButton, TEAL_COLOR, WHITE);
        attackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        attackButton.setMaximumSize(new Dimension(200, 40));

        Combat combat = new Combat(controller.getPlayer(), enemy);

        attackButton.addActionListener(e -> {
            CombatResult result = combat.executeRound();
            combatLog.append(result.getMessage() + "\n");
            combatLog.setCaretPosition(combatLog.getDocument().getLength());

            if (combat.isOver()) {
                attackButton.setEnabled(false);
                if (combat.playerWon()) {
                    combatLog.append("\nVous avez vaincu votre adversaire!\n");
                } else {
                    combatLog.append("\nVous avez été vaincu...\n");
                }

                Timer timer = new Timer(2000, event -> {
                    combatDialog.dispose();
                    updateGameScreen();
                });
                timer.setRepeats(false);
                timer.start();
            }

            updatePlayerStats();
        });

        combatPanel.add(enemyLabel);
        combatPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        combatPanel.add(statsLabel);
        combatPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        combatPanel.add(scrollPane);
        combatPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        combatPanel.add(attackButton);

        combatDialog.add(combatPanel);
        combatDialog.setVisible(true);
    }

    private void updateChapterDisplay() {
        Chapter currentChapter = controller.getCurrentChapter();
        if (currentChapter == null) return;

        String formattedText = currentChapter.getText().replace("\\n", "\n");
        chapterTextArea.setText(formattedText);
        chapterTextArea.setCaretPosition(0);

        choicesPanel.removeAll();

        if (!controller.isGameOver()) {
            List<Choice> choices = currentChapter.getChoices();
            if (choices != null && !choices.isEmpty()) {
                for (int i = 0; i < choices.size(); i++) {
                    final int choiceIndex = i;
                    Choice choice = choices.get(i);

                    JButton choiceButton = new JButton(choice.getText());
                    choiceButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    choiceButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                    choiceButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                    configureButton(choiceButton, CHOICE_BUTTON_COLOR, WHITE);
                    choiceButton.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(TEAL_COLOR, 1),
                            BorderFactory.createEmptyBorder(5, 10, 5, 10)
                    ));

                    choiceButton.addActionListener(e -> {
                        if (choice.isCombatRequired()) {
                            Enemy enemy = currentChapter.getEnemy();
                            handleCombat(enemy);
                        }
                        controller.makeChoice(choiceIndex);
                        updateGameScreen();
                    });

                    choicesPanel.add(choiceButton);
                    choicesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } else {
            String message = controller.isPlayerAlive()
                    ? "Félicitations!"
                    : "Vous avez péri dans le Manoir de l'Enfer!";

            JLabel endLabel = new JLabel(message);
            endLabel.setForeground(WHITE);
            endLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            endLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            choicesPanel.add(endLabel);

            JButton restartButton = new JButton("Retour au menu principal");
            restartButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            restartButton.setMaximumSize(new Dimension(250, 40));

            configureButton(restartButton, TEAL_COLOR, WHITE);
            restartButton.addActionListener(e -> showMainMenu());

            choicesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            choicesPanel.add(restartButton);
        }

        choicesPanel.revalidate();
        choicesPanel.repaint();
    }
}