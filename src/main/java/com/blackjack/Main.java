package com.blackjack;

// GUI Imports
import com.blackjack.GUI.GameGUI;
import com.blackjack.GUI.LoginGUI; // Import the new Login GUI
import com.blackjack.GUI.MainMenuGUI;

// Model/Logic Imports
import com.blackjack.GUI.TournamentGUI;
import com.blackjack.Models.Player;
import com.blackjack.Services.GameLogic;
// No longer need User model directly in Main usually
// import com.blackjack.Models.User;
import com.blackjack.Services.TournamentGameLogic;

// Service Imports
import com.blackjack.Services.LoginService; // Import the new Login Service

// Swing Imports
import javax.swing.*;
// No longer need these directly in Main:
// import java.awt.*;
// import java.nio.charset.StandardCharsets;
// import java.util.Arrays;

public class Main {

    // This flag controls database choice; LoginService will access it.
    // Consider passing this to LoginService constructor if you prefer less static coupling.
    public static boolean useStubDatabase = false; // Default to Real DB

    public static void main(String[] args) {
        // Standard Swing entry point
        SwingUtilities.invokeLater(() -> {
            // 1. Create the service layer object
            LoginService loginService = new LoginService();

            // 2. Create the initial GUI, passing the service
            LoginGUI loginGui = new LoginGUI(loginService);

            // 3. Display the GUI
            loginGui.display();
        });
    }

    // --- Navigation Methods ---
    // These are called by other GUI classes to transition views

    /**
     * Called by LoginGUI upon successful login to show the Main Menu.
     * @param username The username of the logged-in user.
     * @param frameToDispose The JFrame of the previous screen (LoginGUI) to close.
     */
    public static void startMainMenu(String username, JFrame frameToDispose) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("[Main] Starting Main Menu for: " + username);
            MainMenuGUI menu = new MainMenuGUI(username, frameToDispose);
            // MainMenuGUI's constructor should handle disposing the old frame
            // and making the new one visible.
        });
    }

    /**
     * Called when returning to the main menu from another screen (Game, Leaderboard).
     * @param username The username to display on the menu.
     * @param frameToDispose The JFrame of the screen being left (GameGUI/LeaderboardGUI) to close.
     */
    public static void returnToMainMenu(String username, JFrame frameToDispose) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("[Main] Returning to Main Menu for: " + username);
            // Dispose the incoming frame FIRST
            if (frameToDispose != null) {
                frameToDispose.dispose();
            }
            // Create and show the Main Menu (passing null as the frame to dispose this time)
            // MainMenuGUI constructor needs to handle a null frame parameter gracefully.
            MainMenuGUI menu = new MainMenuGUI(username, null); // Pass null for loginFrame
        });
    }


    /**
     * Called by MainMenuGUI to start the actual game.
     * @param username The username of the player.
     * @param frameToDispose The JFrame of the previous screen (MainMenuGUI) to close.
     */
    public static void startGame(String username, JFrame frameToDispose) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("[Main] Starting game for user: " + username);

            // Create game logic and UI (GameGUI is just the panel)
            GameLogic gameLogic = new GameLogic(username); // Uses Main.useStubDatabase internally
            GameGUI gamePanel = new GameGUI(gameLogic);

            // Setup game window (This is where the change happens)
            JFrame gameFrame = new JFrame("Blackjack - " + username);
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setResizable(true);
            gameFrame.add(gamePanel); // Add the GameGUI panel to the frame
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            // Dispose old frame (Main Menu) and show game frame
            if (frameToDispose != null) {
                frameToDispose.dispose();
            }
            gameFrame.setVisible(true); // Make the fullscreen frame visible
        });
    }

    public static void startGame(String username, String tournamentName, JFrame frameToDispose) {
        SwingUtilities.invokeLater(() -> {
            // Create tournament logic and UI (TournamentGUI is the panel)
            TournamentGameLogic tournamentGameLogic = new TournamentGameLogic(username, tournamentName);
            // Player object seems redundant here if TournamentGameLogic manages it, but keep if needed
            // Player player = new Player(1000);
            // player.setName(username);
            TournamentGUI tournamentGUI = new TournamentGUI(tournamentGameLogic);

            // Setup game window (This is where the change happens)
            JFrame gameFrame = new JFrame("Blackjack Tournament - " + username + " (" + tournamentName + ")"); // More informative title
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setResizable(true);
            gameFrame.setContentPane(tournamentGUI); // Use setContentPane since TournamentGUI is the main panel
            // Revalidate and repaint might not be strictly needed here, but pack is better
            // gameFrame.revalidate();
            // gameFrame.repaint();
            gameFrame.pack(); // Pack the frame to fit the content
            gameFrame.setLocationRelativeTo(null); // Center the frame
            gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // *** ADD THIS LINE *** Set fullscreen state

            // Dispose old frame (Lobby) and show game frame
            if (frameToDispose != null) {
                frameToDispose.dispose();
            }
            gameFrame.setVisible(true); // Make the fullscreen frame visible
        });
        // Dispose of the lobby frame after starting game (This seems redundant if already disposed above)
    }

    // Removed methods that are now in LoginService or LoginGUI:
    // - createAndShowLoginGUI() -> Moved to LoginGUI constructor/initialize
    // - attemptLogin() -> Moved to LoginService
    // - attemptSignUp() -> Moved to LoginService
    // - bytesToHex() -> Moved to LoginGUI (if still needed for debug)
}