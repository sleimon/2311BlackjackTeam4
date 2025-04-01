package com.blackjack.GUI;

import com.blackjack.Main; // Make sure Main has the necessary methods like startGame

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// Import the TutorialGUI class
import com.blackjack.GUI.TutorialGUI;
// Import the TournamentLobbyGUI class
import com.blackjack.GUI.TournamentLobbyGUI;

public class MainMenuGUI {

    private JFrame windowMainMenu;
    private JButton playGameButton;
    private JButton leaderBoardButton;
    private JButton tutorialButton;
    private JButton quitButton;
    private JButton tournamentButton; // <-- ADDED: Tournament Button field
    private String username;
    private JFrame loginFrameToDispose;

    // --- Theming Constants (Keep as they are) ---
    private static final Color FELT_GREEN = new Color(0, 85, 30);
    private static final Color DARK_WOOD_BORDER = new Color(51, 34, 17);
    private static final Color BUTTON_BG = new Color(20, 20, 20);
    private static final Color BUTTON_FG = Color.WHITE;
    private static final Color BUTTON_BORDER_COLOR = new Color(218, 165, 32);
    private static final Color BUTTON_HOVER_BG = new Color(50, 50, 50);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color WELCOME_TEXT_COLOR = new Color(255, 230, 180);

    private static final Font TITLE_FONT = new Font("Georgia", Font.BOLD, 28);
    private static final Font WELCOME_FONT = new Font("Georgia", Font.PLAIN, 18);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final Border BUTTON_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BUTTON_BORDER_COLOR, 1),
            new EmptyBorder(10, 30, 10, 30)
    );
    private static final Dimension BUTTON_SIZE = new Dimension(220, 50);

    public MainMenuGUI(String username, JFrame loginFrame) {
        this.username = username;
        this.loginFrameToDispose = loginFrame;
        SwingUtilities.invokeLater(this::initializeMainMenu);
    }

    private void initializeMainMenu() {
        windowMainMenu = new JFrame("Blackjack");
        windowMainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        windowMainMenu.setMinimumSize(new Dimension(400, 400));
        windowMainMenu.setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(DARK_WOOD_BORDER);
        backgroundPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(FELT_GREEN);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DARK_WOOD_BORDER.brighter(), 1),
                new EmptyBorder(40, 60, 40, 60)
        ));

        JLabel titleLabel = new JLabel("BLACKJACK");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(BUTTON_BORDER_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(WELCOME_FONT);
        welcomeLabel.setForeground(WELCOME_TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 35, 0));

        contentPanel.add(titleLabel);
        contentPanel.add(welcomeLabel);

        // Create and style buttons
        playGameButton = createStyledButton("Play Game");
        leaderBoardButton = createStyledButton("Leaderboard");
        tutorialButton = createStyledButton("Tutorial");
        quitButton = createStyledButton("Quit");
        tournamentButton = createStyledButton("Online Multiplayer (Tournament)"); // <-- ADDED: Create tournament button

        // Add buttons with spacing
        contentPanel.add(playGameButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(leaderBoardButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(tutorialButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(tournamentButton); // <-- ADDED: Add tournament button to panel
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(quitButton);

        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        windowMainMenu.setContentPane(backgroundPanel);

        // Add action listeners
        setupButtonActions();

        disposeLoginFrame();

        windowMainMenu.pack();
        windowMainMenu.setLocationRelativeTo(null);
        windowMainMenu.setVisible(true);
    }

    // Helper to create themed buttons (Keep as is)
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(BUTTON_BG);
        button.setForeground(BUTTON_FG);
        button.setBorder(BUTTON_BORDER);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(BUTTON_SIZE);
        button.setPreferredSize(BUTTON_SIZE);
        button.setMinimumSize(BUTTON_SIZE);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_BG);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_BG);
            }
        });

        return button;
    }

    private void setupButtonActions() {
        playGameButton.addActionListener(e -> {
            System.out.println("Play Blackjack button pressed by user: " + username);
            Main.startGame(this.username, this.windowMainMenu); // Assumes Main.startGame handles showing GameGUI
        });

        leaderBoardButton.addActionListener(e -> {
            System.out.println("Leaderboard button pressed.");
            try {
                LeaderboardGUI leaderboardGUI = new LeaderboardGUI(); // Assuming this shows a new window/dialog
                // leaderboardGUI.setVisible(true); // Or however it's shown
            } catch (Exception ex) {
                System.err.println("Error opening Leaderboard: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(windowMainMenu,
                        "Could not display Leaderboard.", "Leaderboard Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        });


        tutorialButton.addActionListener(e -> {
            System.out.println("Tutorial button pressed by user: " + username);
            // Create the TutorialGUI panel
            TutorialGUI tutorialPanel = new TutorialGUI(username);
            // Replace the current content pane (MainMenuGUI) with the tutorial panel
            windowMainMenu.setContentPane(tutorialPanel);
            // Revalidate and repaint the frame to show the changes
            windowMainMenu.revalidate();
            windowMainMenu.repaint();
        });

        tournamentButton.addActionListener(e -> {
            System.out.println("Tournament button pressed by user: " + username);
            // Create the TournamentLobbyGUI panel
            TournamentLobbyGUI tournamentLobbyPanel = new TournamentLobbyGUI(username);
            // Replace the current content pane with the tournament lobby panel
            windowMainMenu.setContentPane(tournamentLobbyPanel);
            // Revalidate and repaint the frame
            windowMainMenu.revalidate();
            windowMainMenu.repaint();
        });


        quitButton.addActionListener(e -> {
            System.out.println("Quit button pressed.");
            int choice = JOptionPane.showConfirmDialog(windowMainMenu,
                    "Are you sure you want to quit?", "Confirm Exit",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private void disposeLoginFrame() {
        if (loginFrameToDispose != null) {
            SwingUtilities.invokeLater(() -> loginFrameToDispose.dispose());
        } else {
            System.err.println("Warning: MainMenu created without a login frame to dispose.");
        }
    }
}