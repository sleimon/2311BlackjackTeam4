package com.blackjack.GUI;

import com.blackjack.Models.Card;
import com.blackjack.Models.Player;
import com.blackjack.Services.TournamentGameLogic;
import com.blackjack.Services.UserService; // Import needed for getting winner username
import com.blackjack.Models.User;
import com.blackjack.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.EnumSet;

@SuppressWarnings("serial")
public class TournamentGUI extends JPanel {

    // --- Fields ---
    private final TournamentGameLogic gameLogic;
    // Panels
    private JPanel topPanel, centerPanel, dealerArea, playerArea, messagePanel, controlsPanel;
    private JPanel bettingControlsPanel, actionControlsPanel, roundEndControlsPanel;
    // Labels
    private JLabel scoreLabel, chipsLabel, playerHandValueLabel, dealerHandValueLabel, gameMessageLabel, tournamentTargetLabel;
    // Card Labels
    private final JLabel[] dealerCardLabels = new JLabel[11];
    private final JLabel[] playerCardsLabels = new JLabel[11];
    // Buttons
    private JButton hitButton, standButton, surrenderButton, insuranceButton, doubleDownButton;
    private JButton nextRoundButton, neitherButton, betAllButton, bet50Button, bet100Button;
    private JButton restartButton, exitButton, backToMenuButton;

    // --- Constants & Styling ---
    public static final int CARD_WIDTH = 100;
    public static final int CARD_HEIGHT = 145;
    public static final String CARD_IMAGE_RESOURCE_PATH = "cards/";
    private ImageIcon cardDownIcon;
    private static final Color FELT_GREEN = new Color(0, 80, 0);
    private static final Color BORDER_COLOR = FELT_GREEN.darker();
    private static final Color BUTTON_COLOR = new Color(200, 200, 200);
    private static final Color BUTTON_TEXT_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font MESSAGE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final boolean DEBUG_BORDERS = false; // Keep false unless debugging

    // --- State Flags ---
    private boolean tournamentEndPopupShown = false; // Flag to show popup only once

    // --- Constructor ---
    public TournamentGUI(TournamentGameLogic logic) {
        if (logic == null) throw new IllegalArgumentException("TournamentGameLogic cannot be null");
        this.gameLogic = logic;
        loadCardDownImage();
        initializeGUI();
        updateGUI(); // Initial GUI update
    }

    // --- Initialization Methods ---
    private void loadCardDownImage() {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource(CARD_IMAGE_RESOURCE_PATH + "CardDown.png");
            if (imgURL == null) {
                System.err.println("Error loading CardDown.png: Resource not found at classpath:" + CARD_IMAGE_RESOURCE_PATH + "CardDown.png");
                cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back");
                return;
            }
            ImageIcon originalIcon = new ImageIcon(imgURL);
            if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                System.err.println("CardDown.png image data incomplete.");
                cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back");
            } else
                cardDownIcon = new ImageIcon(originalIcon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.err.println("Exception loading CardDown.png: " + e);
            cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back");
        }
    }

    private ImageIcon createPlaceholderIcon(Color color, String text) {
        BufferedImage image = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setColor(color);
            g.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g.getFontMetrics();
            int x = (CARD_WIDTH - fm.stringWidth(text)) / 2;
            int y = (CARD_HEIGHT + fm.getAscent()) / 2 - fm.getDescent();
            g.drawString(text, x, y);
        } finally {
            g.dispose();
        }
        return new ImageIcon(image);
    }

    private void initializeGUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(FELT_GREEN.darker());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        createTopPanel();
        createCenterPanel();
        createControlsPanel();
        this.add(topPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(controlsPanel, BorderLayout.SOUTH);
    }

    private void createTopPanel() {
        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        topPanel.setBackground(FELT_GREEN.darker());
        topPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, BORDER_COLOR, BORDER_COLOR.brighter()), new EmptyBorder(5, 10, 5, 10)));

        // No separate score label in tournament (usually just chips)
        // scoreLabel = new JLabel("W:0 L:0 P:0");
        // styleLabel(scoreLabel);
        // topPanel.add(scoreLabel);

        chipsLabel = new JLabel("Chips: 0");
        styleLabel(chipsLabel);
        topPanel.add(chipsLabel);

        tournamentTargetLabel = new JLabel("Target: 0"); // Initialize target label
        styleLabel(tournamentTargetLabel);
        topPanel.add(tournamentTargetLabel); // Add to top panel
    }

    private void createCenterPanel() {
        centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(FELT_GREEN);
        centerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, BORDER_COLOR, BORDER_COLOR.brighter()));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        // Dealer Area
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.4;
        dealerArea = createCardAreaPanel("Dealer Hand: ?");
        dealerHandValueLabel = (JLabel) dealerArea.getComponent(0);
        dealerArea.setPreferredSize(new Dimension(600, CARD_HEIGHT + 60));
        if (DEBUG_BORDERS) dealerArea.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
        setupCardLabels(dealerCardLabels, dealerArea);
        centerPanel.add(dealerArea, gbc);
        // Message Area
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.setOpaque(false);
        gameMessageLabel = new JLabel("Msg", SwingConstants.CENTER);
        gameMessageLabel.setFont(MESSAGE_FONT);
        gameMessageLabel.setForeground(TEXT_COLOR);
        messagePanel.add(gameMessageLabel);
        centerPanel.add(messagePanel, gbc);
        // Player Area
        gbc.gridy = 2;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        playerArea = createCardAreaPanel("Player Hand: ?");
        playerHandValueLabel = (JLabel) playerArea.getComponent(0);
        playerArea.setPreferredSize(new Dimension(600, CARD_HEIGHT + 60));
        if (DEBUG_BORDERS) playerArea.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1));
        setupCardLabels(playerCardsLabels, playerArea);
        centerPanel.add(playerArea, gbc);
    }

    private JPanel createCardAreaPanel(String initialLabelText) {
        JPanel areaPanel = new JPanel();
        areaPanel.setLayout(null);
        areaPanel.setOpaque(false);
        JLabel valueLabel = new JLabel(initialLabelText);
        styleLabel(valueLabel);
        valueLabel.setBounds(10, 5, 300, 25);
        areaPanel.add(valueLabel);
        return areaPanel;
    }

    private void setupCardLabels(JLabel[] labels, JPanel parentPanel) {
        int cardX = 10;
        int cardY = 35;
        int xOffset = CARD_WIDTH / 2 + 10;
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel();
            labels[i].setBounds(cardX + (i * xOffset), cardY, CARD_WIDTH, CARD_HEIGHT);
            labels[i].setIcon(null);
            labels[i].setVisible(false);
            parentPanel.add(labels[i]);
        }
    }

    private void createControlsPanel() {
        controlsPanel = new JPanel(new CardLayout());
        controlsPanel.setOpaque(false);
        // Betting Panel
        bettingControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bettingControlsPanel.setOpaque(false);
        bet50Button = createStyledButton("Bet 50");
        bet100Button = createStyledButton("Bet 100");
        betAllButton = createStyledButton("Bet All");
        bettingControlsPanel.add(bet50Button);
        bettingControlsPanel.add(bet100Button);
        bettingControlsPanel.add(betAllButton);
        // Action Panel
        actionControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        actionControlsPanel.setOpaque(false);
        hitButton = createStyledButton("Hit");
        standButton = createStyledButton("Stand");
        doubleDownButton = createStyledButton("Double Down");
        surrenderButton = createStyledButton("Surrender");
        insuranceButton = createStyledButton("Insurance");
        neitherButton = createStyledButton("Decline Ins.");
        actionControlsPanel.add(hitButton);
        actionControlsPanel.add(standButton);
        actionControlsPanel.add(doubleDownButton);
        actionControlsPanel.add(surrenderButton);
        actionControlsPanel.add(insuranceButton);
        actionControlsPanel.add(neitherButton);
        // Round End Panel (Handles game over/tournament end states too)
        roundEndControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        roundEndControlsPanel.setOpaque(false);
        nextRoundButton = createStyledButton("Next Round");
        restartButton = createStyledButton("Restart Game"); // May be hidden/disabled in tournament
        backToMenuButton = createStyledButton("Main Menu");
        exitButton = createStyledButton("Exit App");
        roundEndControlsPanel.add(nextRoundButton);
        roundEndControlsPanel.add(restartButton);
        roundEndControlsPanel.add(backToMenuButton);
        roundEndControlsPanel.add(exitButton);
        // Add panels to CardLayout
        controlsPanel.add(bettingControlsPanel, "BETTING");
        controlsPanel.add(actionControlsPanel, "ACTIONS");
        controlsPanel.add(roundEndControlsPanel, "ROUND_END"); // Also used for game/tournament over
        setupButtonActionListeners();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }

    private void styleLabel(JLabel label) {
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        label.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setupButtonActionListeners() {
        // Betting Actions
        bet50Button.addActionListener(e -> { gameLogic.placeBet(50); updateGUI(); });
        bet100Button.addActionListener(e -> { gameLogic.placeBet(100); updateGUI(); });
        betAllButton.addActionListener(e -> {
            if (gameLogic.getPlayer()!= null) gameLogic.placeBet(gameLogic.getPlayer().getChips());
            updateGUI();
        });

        // Player Turn Actions
        hitButton.addActionListener(e -> { gameLogic.hit(); updateGUI(); });
        standButton.addActionListener(e -> { gameLogic.stand(); updateGUI(); });
        doubleDownButton.addActionListener(e -> { gameLogic.doubleDown(); updateGUI(); });
        surrenderButton.addActionListener(e -> { gameLogic.surrender(); updateGUI(); });

        // Insurance Actions
        insuranceButton.addActionListener(e -> { gameLogic.requestInsurance(); updateGUI(); });
        neitherButton.addActionListener(e -> { gameLogic.declineInsurance(); updateGUI(); });

        // Round/Game End Actions
        nextRoundButton.addActionListener(e -> { gameLogic.nextRound(); resetTournamentEndPopupFlag(); updateGUI(); }); // Reset flag on next round
        restartButton.addActionListener(e -> { gameLogic.restartGame(); resetTournamentEndPopupFlag(); updateGUI(); }); // Reset flag on restart (if applicable)
        exitButton.addActionListener(e -> System.exit(0));
        backToMenuButton.addActionListener(e -> {
            // Ensure currentFrame is disposed when returning to menu
            String currentUsername = (gameLogic.getCurrentUser() != null) ? gameLogic.getCurrentUser().getUsername() : "user";
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (currentFrame != null) {
                Main.returnToMainMenu(currentUsername, currentFrame); // Main handles dispose
            } else {
                System.err.println("Error: Could not find parent frame for TournamentGUI.");
            }
        });
    }

    // --- Update Methods ---

    /**
     * Updates all visual components based on the current GameLogic state.
     * This includes labels, cards, button visibility/enabled state, and popups.
     */
    public void updateGUI() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::updateGUI);
            return;
        }

        // 1. Update standard labels and card displays
        updateLabels();
        updateCardDisplay();

        // 2. Check for and display end-game/tournament popups (only once)
        displayEndGamePopupIfNeeded();

        // 3. Update button states based on available actions *after* checking state for popups
        updateButtonStates();

        // 4. Repaint relevant panels
        if (dealerArea != null) dealerArea.repaint(); // Repaint is usually sufficient if bounds don't change
        if (playerArea != null) playerArea.repaint();
        this.revalidate(); // Revalidate the main panel
        this.repaint();    // Repaint the main panel
    }

    /**
     * Displays a popup message if the tournament has ended or the player is out of chips,
     * but only if the popup hasn't been shown for this specific end condition yet.
     */
    private void displayEndGamePopupIfNeeded() {
        TournamentGameLogic.GameState currentState = gameLogic.getCurrentState();

        // Check conditions for showing the popup
        boolean showPopup = !tournamentEndPopupShown &&
                (currentState == TournamentGameLogic.GameState.TOURNAMENT_WON_BY_PLAYER ||
                        currentState == TournamentGameLogic.GameState.TOURNAMENT_ENDED_OTHER_WINNER ||
                        currentState == TournamentGameLogic.GameState.GAME_OVER);

        if (showPopup) {
            String popupTitle = "Tournament Status";
            String popupMessage = null;
            int messageType = JOptionPane.INFORMATION_MESSAGE; // Default type

            switch (currentState) {
                case TOURNAMENT_WON_BY_PLAYER:
                    popupTitle = "Tournament Won!";
                    popupMessage = String.format("Congratulations, %s!\n" +
                                    "You won the '%s' tournament by reaching %d / %d chips!",
                            gameLogic.getUsername(),
                            gameLogic.getTournamentName(),
                            gameLogic.getPlayer().getChips(), // Current chips
                            gameLogic.currentTournament.getTargetChips()); // Target
                    break;

                case TOURNAMENT_ENDED_OTHER_WINNER:
                    popupTitle = "Tournament Over";
                    String winnerUsername = "Unknown";
                    // Safely get winner username
                    if (gameLogic.currentTournament != null && gameLogic.currentTournament.getWonBy() > 0) {
                        User winner = UserService.getUserbyId(gameLogic.currentTournament.getWonBy()); // Requires UserService import
                        if (winner != null) {
                            winnerUsername = winner.getUsername();
                        } else {
                            winnerUsername = "ID(" + gameLogic.currentTournament.getWonBy() + ")";
                        }
                    }
                    popupMessage = String.format("The '%s' tournament has already ended.\n" +
                                    "Winner: %s",
                            gameLogic.getTournamentName(),
                            winnerUsername);
                    break;

                case GAME_OVER: // Player eliminated due to lack of chips
                    popupTitle = "Eliminated";
                    popupMessage = "You are out of chips and have been eliminated from the tournament.";
                    messageType = JOptionPane.WARNING_MESSAGE; // Use warning icon
                    break;

                default:
                    // Should not happen if 'showPopup' logic is correct, but good practice
                    System.err.println("Warning: displayEndGamePopupIfNeeded called with unexpected state: " + currentState);
                    break;
            }

            // Show the popup if a message was generated
            if (popupMessage != null) {
                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                JOptionPane.showMessageDialog(parentWindow,
                        popupMessage,
                        popupTitle,
                        messageType); // Use appropriate message type
                tournamentEndPopupShown = true; // Mark as shown AFTER displaying
            }
        }
    }


    private void updateLabels() {
        // Ensure player and tournament objects exist
        if (gameLogic.getPlayer() == null || gameLogic.currentTournament == null) {
            // Display error state or default values if critical objects are missing
            chipsLabel.setText("Chips: ERROR");
            tournamentTargetLabel.setText("Target: ERROR");
            gameMessageLabel.setText("Error loading game data.");
            playerHandValueLabel.setText("Player Hand: ?");
            dealerHandValueLabel.setText("Dealer Hand: ?");
            return;
        }

        chipsLabel.setText("Chips: " + gameLogic.getPlayer().getChips());
        tournamentTargetLabel.setText("Target: " + gameLogic.currentTournament.getTargetChips());

        String message = gameLogic.getGameMessage();
        // Use HTML for potential line breaks and centering within the label space
        gameMessageLabel.setText("<html><body style='text-align: center; width: 95%;'>" + (message != null ? message : "") + "</body></html>");

        // Update hand value labels based on state
        if (gameLogic.getCurrentState() == TournamentGameLogic.GameState.BETTING) {
            playerHandValueLabel.setText("Player Hand: ?");
            dealerHandValueLabel.setText("Dealer Hand: ?");
        } else {
            playerHandValueLabel.setText("Player Hand: " + gameLogic.getPlayerHandValue());
            if (gameLogic.isDealerCardHidden()) {
                dealerHandValueLabel.setText("Dealer Shows: " + gameLogic.getDealerVisibleValue() + " + ?");
            } else {
                dealerHandValueLabel.setText("Dealer Hand: " + gameLogic.getDealerHandValue());
            }
        }
    }


    private void updateCardDisplay() {
        // Reset cards display during betting phase
        if (gameLogic.getCurrentState() == TournamentGameLogic.GameState.BETTING) {
            if (dealerCardLabels[0] != null) { dealerCardLabels[0].setIcon(cardDownIcon); dealerCardLabels[0].setVisible(true); }
            if (dealerCardLabels[1] != null) { dealerCardLabels[1].setIcon(cardDownIcon); dealerCardLabels[1].setVisible(true); }
            if (playerCardsLabels[0] != null) { playerCardsLabels[0].setIcon(cardDownIcon); playerCardsLabels[0].setVisible(true); }
            if (playerCardsLabels[1] != null) { playerCardsLabels[1].setIcon(cardDownIcon); playerCardsLabels[1].setVisible(true); }
            // Hide extra card slots
            for (int i = 2; i < dealerCardLabels.length; i++) {
                if (dealerCardLabels[i] != null) dealerCardLabels[i].setVisible(false);
                if (playerCardsLabels[i] != null) playerCardsLabels[i].setVisible(false);
            }
        } else {
            // Update based on actual hands, considering hidden dealer card
            updateSpecificHandDisplay(dealerCardLabels, gameLogic.getDealerCards(), gameLogic.isDealerCardHidden());
            updateSpecificHandDisplay(playerCardsLabels, gameLogic.getPlayerCards(), false); // Player cards always visible
        }
    }

    private void updateSpecificHandDisplay(JLabel[] cardLabels, List<Card> cards, boolean hideSecondCard) {
        if (cards == null) return; // Safety check

        for (int i = 0; i < cardLabels.length; i++) {
            if (cardLabels[i] == null) continue; // Skip if label wasn't created

            if (i < cards.size()) {
                Card card = cards.get(i);
                if (card == null) {
                    // Handle unexpected null card in hand
                    cardLabels[i].setIcon(createPlaceholderIcon(Color.MAGENTA, "ERR"));
                    cardLabels[i].setToolTipText("Error: Null card data");
                    cardLabels[i].setVisible(true);
                    continue;
                }

                // Determine if this specific card should be hidden
                boolean hideThisCard = (i == 1 && hideSecondCard);
                ImageIcon icon = hideThisCard ? cardDownIcon : getCardImage(card.toString()); // Use existing toString for image lookup key
                cardLabels[i].setIcon(icon);
                cardLabels[i].setToolTipText(hideThisCard ? "Dealer's Hole Card" : card.toString()); // Tooltip for info
                cardLabels[i].setVisible(true);
            } else {
                // Hide unused labels
                cardLabels[i].setIcon(null);
                cardLabels[i].setVisible(false);
                cardLabels[i].setToolTipText(null);
            }
        }
        // No need to revalidate/repaint parent here, updateGUI handles it later
    }

    private ImageIcon getCardImage(String cardString) {
        // This method expects cardString format like "Rank of Suit(Value)"
        if (cardString == null || !cardString.contains(" of ") || !cardString.contains("(")) return cardDownIcon;

        String rankName = "Unknown", suitName = "Unknown", shortCode = "?";
        try {
            String[] parts = cardString.split(" of ");
            if (parts.length == 2) {
                rankName = parts[0].trim();
                String suitAndValue = parts[1].trim();
                int parenIndex = suitAndValue.indexOf('(');
                if (parenIndex > 0) suitName = suitAndValue.substring(0, parenIndex).trim();
                else return cardDownIcon; // Malformed string
            } else return cardDownIcon; // Malformed string

            // Generate filename like "AceHearts.png", "TenSpades.png"
            String filename = rankName + suitName + ".png";
            String resourcePath = CARD_IMAGE_RESOURCE_PATH + filename;
            java.net.URL imgURL = getClass().getClassLoader().getResource(resourcePath);

            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE)
                    return new ImageIcon(originalIcon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
                else {
                    System.err.println("Error loading image (incomplete): " + resourcePath);
                    return createPlaceholderIcon(Color.RED, rankName.substring(0,1)+suitName.substring(0,1)); // Short code fallback
                }
            } else {
                System.err.println("Error loading image: Resource not found at '" + resourcePath + "'");
                return createPlaceholderIcon(Color.BLUE, rankName.substring(0,1)+suitName.substring(0,1)); // Short code fallback
            }
        } catch (Exception e) {
            System.err.println("Exception processing card string '" + cardString + "' or loading image: " + e.getMessage());
            return createPlaceholderIcon(Color.ORANGE, shortCode); // Generic error fallback
        }
    }

    private void updateButtonStates() {
        EnumSet<TournamentGameLogic.PlayerAction> availableActions = gameLogic.getAvailableActions();
        TournamentGameLogic.GameState currentState = gameLogic.getCurrentState();
        CardLayout cl = (CardLayout) (controlsPanel.getLayout());
        String panelToShow;

        // Determine which control panel to show based on state
        switch (currentState) {
            case BETTING:
                panelToShow = "BETTING";
                break;
            case INSURANCE_SURRENDER:
            case PLAYER_TURN:
            case DEALER_TURN: // Show action panel during dealer turn, but buttons will be disabled
                panelToShow = "ACTIONS";
                break;
            case ROUND_OVER:
            case GAME_OVER: // Game over (eliminated)
            case TOURNAMENT_WON_BY_PLAYER: // Player won
            case TOURNAMENT_ENDED_OTHER_WINNER: // Someone else won
                panelToShow = "ROUND_END"; // Show the end-game controls panel
                break;
            default:
                System.err.println("Warning: Unknown game state in updateButtonStates: " + currentState);
                panelToShow = "ROUND_END"; // Default to end panel in unknown state
                break;
        }
        cl.show(controlsPanel, panelToShow);

        // Set visibility and enabled state for each button based on availableActions
        bet50Button.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.BET_50));
        bet100Button.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.BET_100));
        betAllButton.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.BET_ALL));

        hitButton.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.HIT));
        standButton.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.STAND));
        doubleDownButton.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.DOUBLE_DOWN));
        surrenderButton.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.SURRENDER));
        insuranceButton.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.INSURANCE));
        neitherButton.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.NEITHER));

        nextRoundButton.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.NEXT_ROUND));
        // Restart might be inappropriate for tournaments, consider hiding/disabling permanently
        restartButton.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.RESTART));
        // Maybe disable restart in tournament context:
        // restartButton.setEnabled(availableActions.contains(TournamentGameLogic.PlayerAction.RESTART) && !isInTournamentMode()); // Requires a flag/check

        exitButton.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.EXIT));
        backToMenuButton.setVisible(availableActions.contains(TournamentGameLogic.PlayerAction.BACK_TO_MENU));

        // Set enabled state (buttons might be visible but disabled, e.g., during dealer turn)
        // Note: Visibility check is redundant if setVisible(false) was used, but good practice
        boolean enablePlayerActions = (currentState == TournamentGameLogic.GameState.PLAYER_TURN);
        boolean enableInsuranceActions = (currentState == TournamentGameLogic.GameState.INSURANCE_SURRENDER);
        boolean enableBetActions = (currentState == TournamentGameLogic.GameState.BETTING);
        boolean enableEndActions = (currentState == TournamentGameLogic.GameState.ROUND_OVER ||
                currentState == TournamentGameLogic.GameState.GAME_OVER ||
                currentState == TournamentGameLogic.GameState.TOURNAMENT_WON_BY_PLAYER ||
                currentState == TournamentGameLogic.GameState.TOURNAMENT_ENDED_OTHER_WINNER);


        bet50Button.setEnabled(enableBetActions && bet50Button.isVisible());
        bet100Button.setEnabled(enableBetActions && bet100Button.isVisible());
        betAllButton.setEnabled(enableBetActions && betAllButton.isVisible());

        hitButton.setEnabled(enablePlayerActions && hitButton.isVisible());
        standButton.setEnabled(enablePlayerActions && standButton.isVisible());
        doubleDownButton.setEnabled(enablePlayerActions && doubleDownButton.isVisible());
        surrenderButton.setEnabled(enablePlayerActions && surrenderButton.isVisible());

        insuranceButton.setEnabled(enableInsuranceActions && insuranceButton.isVisible());
        neitherButton.setEnabled(enableInsuranceActions && neitherButton.isVisible());

        nextRoundButton.setEnabled(enableEndActions && nextRoundButton.isVisible() && currentState == TournamentGameLogic.GameState.ROUND_OVER); // Only enable if round over, not game over
        restartButton.setEnabled(enableEndActions && restartButton.isVisible()); // Enable based on availableActions
        exitButton.setEnabled(exitButton.isVisible()); // Generally always enabled if visible
        backToMenuButton.setEnabled(enableBetActions || (enableEndActions && backToMenuButton.isVisible())); // Enable in betting or end states


        // Revalidate panels if visibility changes significantly (usually not needed just for enable/disable)
        // bettingControlsPanel.revalidate();
        // actionControlsPanel.revalidate();
        // roundEndControlsPanel.revalidate();
        // controlsPanel.revalidate(); // Revalidating CardLayout container might be useful
    }

    /**
     * Resets the flag that prevents the end-game popup from showing multiple times.
     * Call this when starting a new game/round where the popup might be relevant again.
     */
    public void resetTournamentEndPopupFlag() {
        this.tournamentEndPopupShown = false;
        System.out.println("[TournamentGUI] Popup flag reset."); // For debugging
    }

} // End of TournamentGUI class