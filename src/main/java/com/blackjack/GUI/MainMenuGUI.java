package com.blackjack.GUI;

import com.blackjack.Main;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainMenuGUI {

    private JFrame windowMainMenu;
    private JButton playGameButton;
    private JButton leaderBoardButton;
    private JButton quitButton;
    private String username;
    private JFrame loginFrameToDispose;

    // --- Theming Constants ---
    private static final Color FELT_GREEN = new Color(0, 85, 30);        // Rich dark green
    private static final Color DARK_WOOD_BORDER = new Color(51, 34, 17);   // Dark brown for border
    private static final Color BUTTON_BG = new Color(20, 20, 20);          // Very dark grey/black
    private static final Color BUTTON_FG = Color.WHITE;
    private static final Color BUTTON_BORDER_COLOR = new Color(218, 165, 32); // Gold color for border
    private static final Color BUTTON_HOVER_BG = new Color(50, 50, 50);    // Lighter grey on hover
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color WELCOME_TEXT_COLOR = new Color(255, 230, 180); // Soft gold/off-white

    private static final Font TITLE_FONT = new Font("Georgia", Font.BOLD, 28); // More elegant title font
    private static final Font WELCOME_FONT = new Font("Georgia", Font.PLAIN, 18);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14); // Keep button font clean

    // Button Border: Gold line outside, padding inside
    private static final Border BUTTON_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BUTTON_BORDER_COLOR, 1), // Gold line border
            new EmptyBorder(10, 30, 10, 30) // Padding
    );
    private static final Dimension BUTTON_SIZE = new Dimension(220, 50); // Slightly larger buttons

    public MainMenuGUI(String username, JFrame loginFrame) {
        this.username = username;
        this.loginFrameToDispose = loginFrame;
        // Ensure GUI creation happens on the Event Dispatch Thread
        SwingUtilities.invokeLater(this::initializeMainMenu);
    }

    private void initializeMainMenu() {
        windowMainMenu = new JFrame("Blackjack"); // Simple Title
        windowMainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        windowMainMenu.setMinimumSize(new Dimension(400, 400)); // Ensure minimum size
        windowMainMenu.setLocationRelativeTo(null);
        // Use a panel with border as the main background container
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(DARK_WOOD_BORDER); // Outer border color
        backgroundPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Space around the felt

        // Main content panel (the "felt")
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(FELT_GREEN); // Felt green background
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DARK_WOOD_BORDER.brighter(), 1), // Subtle inner border
                new EmptyBorder(40, 60, 40, 60) // Padding inside the felt
        ));

        // Title Label
        JLabel titleLabel = new JLabel("BLACKJACK");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(BUTTON_BORDER_COLOR); // Use gold color
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0)); // Space below title

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(WELCOME_FONT);
        welcomeLabel.setForeground(WELCOME_TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 35, 0)); // More space below welcome

        contentPanel.add(titleLabel);
        contentPanel.add(welcomeLabel);

        // Create and style buttons
        playGameButton = createStyledButton("Play Game");
        leaderBoardButton = createStyledButton("Leaderboard");
        quitButton = createStyledButton("Quit");

        // Add buttons with spacing
        contentPanel.add(playGameButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Vertical space
        contentPanel.add(leaderBoardButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Vertical space
        contentPanel.add(quitButton);

        // Add felt panel to background panel
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        // Set background panel as the content pane
        windowMainMenu.setContentPane(backgroundPanel);

        // Add action listeners
        setupButtonActions();

        // Dispose login frame
        disposeLoginFrame();

        windowMainMenu.pack(); // Pack to fit contents
        windowMainMenu.setLocationRelativeTo(null); // Re-center after packing
        windowMainMenu.setVisible(true);
    }

    // Helper to create themed buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(BUTTON_BG);
        button.setForeground(BUTTON_FG);
        button.setBorder(BUTTON_BORDER);
        button.setFocusPainted(false); // Remove blue outline on focus
        button.setOpaque(true); // Needed for background color on some systems
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Set min/max/pref size for consistency with BoxLayout
        button.setMaximumSize(BUTTON_SIZE);
        button.setPreferredSize(BUTTON_SIZE);
        button.setMinimumSize(BUTTON_SIZE);

        // Hover effect
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
            Main.startGame(this.username, this.windowMainMenu);
        });

        leaderBoardButton.addActionListener(e -> {
            System.out.println("Leaderboard button pressed.");
            try {
                LeaderboardGUI leaderboardGUI = new LeaderboardGUI();
                // Consider disabling main menu while leaderboard is open if it's modal
            } catch (Exception ex) {
                System.err.println("Error opening Leaderboard: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(windowMainMenu,
                        "Could not display Leaderboard.", "Leaderboard Error",
                        JOptionPane.WARNING_MESSAGE);
            }
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
            // Ensure disposal happens on the EDT
            SwingUtilities.invokeLater(() -> loginFrameToDispose.dispose());
        } else {
            System.err.println("Warning: MainMenu created without a login frame to dispose.");
        }
    }
}