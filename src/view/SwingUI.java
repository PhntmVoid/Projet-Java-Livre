package view;

import controller.GameController;
import model.Chapter;
import model.Choice;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SwingUI {
    private GameController controller;
    private JFrame frame;
    private JPanel mainMenuPanel;
    private JPanel gamePanel;
    private JPanel statsPanel;
    private JTextArea chapterTextArea;
    private JPanel choicesPanel;

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
        updateGameScreen();
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
        startButton.addActionListener(e -> showGameScreen());

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        centerPanel.add(startButton);
        centerPanel.add(Box.createVerticalGlue());

        mainMenuPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void createGameInterface() {
        gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(DARK_GREY);

        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(40, 40, 40));
        statsPanel.setPreferredSize(n