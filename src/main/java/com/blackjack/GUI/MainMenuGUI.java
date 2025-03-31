package com.blackjack.GUI;

import com.blackjack.Main; // Import Main to call its static startGame method

import javax.swing.*;
import java.awt.*;

// MainMenu doesn't necessarily need to be a JPanel itself if it just creates a JFrame
// public class MainMenu extends JPanel{
public class MainMenuGUI { // Changed to a standard class that manages a JFrame

    private JFrame windowMainMenu;
    // private JLabel Blackjack; // This label wasn't used, can be removed or added properly if needed
    private JButton playGameButton; // Renamed for clarity
    private JButton leaderBoardButton; // Renamed for clarity
    private JButton quitButton; // Renamed for clarity
    private String username;
    private JFrame loginFrameToDispose; // To hold the frame we need to close

    /**
     * Constructor for the Main Menu.
     * @param username The username of the logged-in user.
     * @param loginFrame The login JFrame instance to dispose once the main menu is shown.
     */
    public MainMenuGUI(String username, JFrame loginFrame) {
        this.username = username;
        this.loginFrameToDispose = loginFrame;
        initializeMainMenu(); // Create and show the main menu GUI
    }

    /**
     * Sets up and displays the Main Menu JFrame.
     */
    private void initializeMainMenu() {

        windowMainMenu = new JFrame();
        windowMainMenu.setTitle("Blackjack Main Menu - Welcome " + username); // Personalized title
        windowMainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application if this window is closed
        windowMainMenu.setSize(400, 200); // Adjusted size, can be packed later
        windowMainMenu.setLocationRelativeTo(null); // Center screen

        // Create a panel for the buttons
        JPanel panelMainMenu = new JPanel();
        // Using GridBagLayout for better centering and spacing
        panelMainMenu.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around buttons
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make buttons same width

        // Add a welcome label (Optional but nice)
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 0;
        panelMainMenu.add(welcomeLabel, gbc);

        // Create and add buttons
        playGameButton = new JButton("Play Blackjack");
        gbc.gridy = 1; // Next row
        panelMainMenu.add(playGameButton, gbc);

        leaderBoardButton = new JButton("Leaderboard");
        gbc.gridy = 2; // Next row
        panelMainMenu.add(leaderBoardButton, gbc);

        quitButton = new JButton("Quit Game");
        gbc.gridy = 3; // Next row
        panelMainMenu.add(quitButton, gbc);

        // Set background if desired (applied to panel)
        // panelMainMenu.setBackground(Color.GREEN); // A bit harsh, maybe light gray?
        panelMainMenu.setBackground(new Color(230, 230, 230));

        // Add panel to the frame's content pane
        windowMainMenu.setContentPane(panelMainMenu); // Set as the main content

        // Add action listeners using a separate method for clarity
        setupButtonActions();

        // Dispose the login frame passed from Main
        if (loginFrameToDispose != null) {
            loginFrameToDispose.dispose();
        } else {
            System.err.println("Warning: MainMenu created without a login frame to dispose.");
        }

        // Pack the frame to fit components and make it visible
        // windowMainMenu.pack(); // Calculate optimal size
        windowMainMenu.setVisible(true); // Show the main menu
    }

    /**
     * Sets up the ActionListener for each button.
     */
    private void setupButtonActions() {
        playGameButton.addActionListener(e -> {
            System.out.println("Play Blackjack button pressed by user: " + username);
            // Call the static startGame method in the Main class
            // Pass the username and THIS main menu frame (windowMainMenu) to be disposed
            Main.startGame(this.username, this.windowMainMenu);
            // No need to dispose windowMainMenu here, Main.startGame will do it
        });

        leaderBoardButton.addActionListener(e -> {
            System.out.println("Leaderboard button pressed.");
            // --- Placeholder for Leaderboard Functionality ---
            // You would typically create and show a new JFrame or JDialog here
            // to display the leaderboard, fetching data via UserService or similar.
            JOptionPane.showMessageDialog(windowMainMenu,
                    "Leaderboard feature not yet implemented.",
                    "Leaderboard",
                    JOptionPane.INFORMATION_MESSAGE);
            // --- End Placeholder ---
        });

        quitButton.addActionListener(e -> {
            System.out.println("Quit button pressed.");
            // Ask for confirmation before exiting (optional but good practice)
            int choice = JOptionPane.showConfirmDialog(windowMainMenu,
                    "Are you sure you want to quit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0); // Terminate the application
            }
        });
    }

    /*
     * This method is now removed because the responsibility of starting the game
     * (creating GameLogic, GameGUI, and the game JFrame) belongs to the Main class.
     * The Play button now calls Main.startGame(...) instead.
     *
    private static void startGame(String username) {
        // ... old implementation ...
    }
    */
}