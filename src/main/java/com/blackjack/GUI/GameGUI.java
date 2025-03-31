package com.blackjack.GUI; // Assuming GUI classes are here

import com.blackjack.Models.Card; // Need Card for List type
// **Make sure this import points to the correct GameLogic class**

import com.blackjack.Services.GameLogic;
import com.blackjack.Models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder; // For padding
import javax.swing.border.EtchedBorder; // For visual separation
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.EnumSet;

import static com.blackjack.Services.GameLogic.GameState.*;

@SuppressWarnings("serial")
public class GameGUI extends JPanel {

    // --- Fields (gameLogic, panels, labels, buttons, constants, styling) ---
    // ... (Keep all existing field declarations as they were) ...
    private final GameLogic gameLogic;

    // Panels
    private JPanel topPanel;
    private JPanel centerPanel; // Main table area
    private JPanel dealerArea;
    private JPanel playerArea;
    private JPanel messagePanel;
    private JPanel controlsPanel;
    private JPanel bettingControlsPanel;
    private JPanel actionControlsPanel;
    private JPanel roundEndControlsPanel;

    // Labels
    private JLabel scoreLabel;
    private JLabel chipsLabel;
    private JLabel playerHandValueLabel;
    private JLabel dealerHandValueLabel;
    private JLabel gameMessageLabel;

    // Card Labels
    private final JLabel[] dealerCardLabels = new JLabel[11];
    private final JLabel[] playerCardsLabels = new JLabel[11];

    // Buttons
    private JButton hitButton, standButton, surrenderButton, insuranceButton, doubleDownButton;
    private JButton nextRoundButton, neitherButton, betAllButton, bet50Button, bet100Button;
    private JButton restartButton, exitButton;

    // --- Constants ---
    public static final int CARD_WIDTH = 100;
    public static final int CARD_HEIGHT = 145;
    public static final String CARD_IMAGE_RESOURCE_PATH = "cards/";
    private ImageIcon cardDownIcon;

    // --- Styling ---
    private static final Color FELT_GREEN = new Color(0, 80, 0);
    private static final Color BORDER_COLOR = FELT_GREEN.darker();
    private static final Color BUTTON_COLOR = new Color(200, 200, 200);
    private static final Color BUTTON_TEXT_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font MESSAGE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Color TEXT_COLOR = Color.WHITE;

    // Debug Flag
    private static final boolean DEBUG_BORDERS = false; // Set to true to see panel bounds


    // Constructor
    public GameGUI(GameLogic logic) {
        this.gameLogic = logic;
        loadCardDownImage();
        initializeGUI();
        updateGUI();
    }

    // --- Initialization Methods (loadCardDownImage, createPlaceholderIcon, initializeGUI, createTopPanel) ---
    // ... (Keep these methods exactly as they were in the previous correct version) ...
    private void loadCardDownImage() {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource(CARD_IMAGE_RESOURCE_PATH + "CardDown.png");
            if (imgURL == null) {
                System.err.println("Error loading CardDown.png: Resource not found at path: " + CARD_IMAGE_RESOURCE_PATH + "CardDown.png");
                cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back"); return;
            }
            ImageIcon originalIcon = new ImageIcon(imgURL);
            if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back");
            } else {
                cardDownIcon = new ImageIcon(originalIcon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back");
        }
    }
    private ImageIcon createPlaceholderIcon(Color color, String text) {
        BufferedImage image = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setColor(color); g.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);
            g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.BOLD, 14));
            FontMetrics fm = g.getFontMetrics(); int x = (CARD_WIDTH - fm.stringWidth(text)) / 2;
            int y = (CARD_HEIGHT + fm.getAscent()) / 2 - fm.getDescent(); g.drawString(text, x, y);
        } finally { g.dispose(); } return new ImageIcon(image);
    }
    private void initializeGUI() {
        this.setLayout(new BorderLayout(10, 10)); this.setBackground(FELT_GREEN.darker());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        createTopPanel(); createCenterPanel(); createControlsPanel();
        this.add(topPanel, BorderLayout.NORTH); this.add(centerPanel, BorderLayout.CENTER);
        this.add(controlsPanel, BorderLayout.SOUTH);
    }
    private void createTopPanel() {
        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5)); topPanel.setBackground(FELT_GREEN.darker());
        topPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, BORDER_COLOR, BORDER_COLOR.brighter()), new EmptyBorder(5, 10, 5, 10) ));
        scoreLabel = new JLabel("Wins: 0 Losses: 0 Pushes: 0"); styleLabel(scoreLabel); topPanel.add(scoreLabel);
        chipsLabel = new JLabel("Chips: 1000"); styleLabel(chipsLabel); topPanel.add(chipsLabel);
    }


    // --- createCenterPanel (Modified for Debugging and Sizing) ---
    private void createCenterPanel() {
        centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(FELT_GREEN);
        centerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, BORDER_COLOR, BORDER_COLOR.brighter()));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // Dealer Area (Top)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.4;
        dealerArea = createCardAreaPanel("Dealer Hand: ?");
        dealerHandValueLabel = (JLabel) dealerArea.getComponent(0); // Retrieve label
        // **Give dealerArea a preferred size to help layout**
        dealerArea.setPreferredSize(new Dimension(600, CARD_HEIGHT + 60)); // Width estimate, Height = card + label/padding
        if (DEBUG_BORDERS) dealerArea.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1)); // DEBUG
        setupCardLabels(dealerCardLabels, dealerArea);
        centerPanel.add(dealerArea, gbc);

        // Message Area (Middle)
        gbc.gridy = 1; gbc.weighty = 0.1; // Reduced weight slightly
        gbc.fill = GridBagConstraints.HORIZONTAL;
        messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.setOpaque(false);
        gameMessageLabel = new JLabel("Place your bet.", SwingConstants.CENTER);
        gameMessageLabel.setFont(MESSAGE_FONT); gameMessageLabel.setForeground(TEXT_COLOR);
        messagePanel.add(gameMessageLabel);
        centerPanel.add(messagePanel, gbc);

        // Player Area (Bottom)
        gbc.gridy = 2; gbc.weighty = 0.5; // Increased weight slightly
        gbc.fill = GridBagConstraints.BOTH;
        playerArea = createCardAreaPanel("Player Hand: ?");
        playerHandValueLabel = (JLabel) playerArea.getComponent(0); // Retrieve label
        // **Give playerArea a preferred size**
        playerArea.setPreferredSize(new Dimension(600, CARD_HEIGHT + 60));
        if (DEBUG_BORDERS) playerArea.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1)); // DEBUG
        setupCardLabels(playerCardsLabels, playerArea);
        centerPanel.add(playerArea, gbc);
    }

    // --- createCardAreaPanel, setupCardLabels ---
    // ... (Keep these methods exactly as they were) ...
    private JPanel createCardAreaPanel(String initialLabelText) {
        JPanel areaPanel = new JPanel(); areaPanel.setLayout(null); areaPanel.setOpaque(false);
        JLabel valueLabel = new JLabel(initialLabelText); styleLabel(valueLabel);
        valueLabel.setBounds(10, 5, 300, 25); areaPanel.add(valueLabel);
        return areaPanel;
    }
    private void setupCardLabels(JLabel[] labels, JPanel parentPanel) {
        int cardX = 10; int cardY = 35;
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel();
            labels[i].setBounds(cardX + (i * (CARD_WIDTH / 2 + 10)), cardY, CARD_WIDTH, CARD_HEIGHT);
            labels[i].setIcon(null); labels[i].setVisible(false);
            parentPanel.add(labels[i]);
        }
    }

    // --- Control Panel Methods (createControlsPanel, createStyledButton, styleLabel, setupButtonActionListeners) ---
    // ... (Keep these methods exactly as they were) ...
    private void createControlsPanel() {
        controlsPanel = new JPanel(new CardLayout()); controlsPanel.setOpaque(false);
        // Betting Panel
        bettingControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); bettingControlsPanel.setOpaque(false);
        bet50Button = createStyledButton("Bet 50"); bet100Button = createStyledButton("Bet 100"); betAllButton = createStyledButton("Bet All");
        bettingControlsPanel.add(bet50Button); bettingControlsPanel.add(bet100Button); bettingControlsPanel.add(betAllButton);
        // Action Panel
        actionControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); actionControlsPanel.setOpaque(false);
        hitButton = createStyledButton("Hit"); standButton = createStyledButton("Stand"); doubleDownButton = createStyledButton("Double Down");
        surrenderButton = createStyledButton("Surrender"); insuranceButton = createStyledButton("Insurance"); neitherButton = createStyledButton("Neither");
        actionControlsPanel.add(hitButton); actionControlsPanel.add(standButton); actionControlsPanel.add(doubleDownButton);
        actionControlsPanel.add(surrenderButton); actionControlsPanel.add(insuranceButton); actionControlsPanel.add(neitherButton);
        // Round End Panel
        roundEndControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); roundEndControlsPanel.setOpaque(false);
        nextRoundButton = createStyledButton("Next Round"); restartButton = createStyledButton("Restart"); exitButton = createStyledButton("Exit Game");
        roundEndControlsPanel.add(nextRoundButton); roundEndControlsPanel.add(restartButton); roundEndControlsPanel.add(exitButton);
        // Add panels to CardLayout
        controlsPanel.add(bettingControlsPanel, "BETTING"); controlsPanel.add(actionControlsPanel, "ACTIONS"); controlsPanel.add(roundEndControlsPanel, "ROUND_END");
        setupButtonActionListeners(); // Setup listeners
    }
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text); button.setFont(BUTTON_FONT); button.setBackground(BUTTON_COLOR); button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false); button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); button.setPreferredSize(new Dimension(120, 35));
        return button;
    }
    private void styleLabel(JLabel label) {
        label.setFont(LABEL_FONT); label.setForeground(TEXT_COLOR); label.setHorizontalAlignment(SwingConstants.LEFT);
    }
    private void setupButtonActionListeners() {
        bet50Button.addActionListener(e -> { gameLogic.placeBet(50); updateGUI(); }); bet100Button.addActionListener(e -> { gameLogic.placeBet(100); updateGUI(); });
        betAllButton.addActionListener(e -> { gameLogic.placeBet(gameLogic.getPlayer().getChips()); updateGUI(); });
        hitButton.addActionListener(e -> { gameLogic.hit(); updateGUI(); }); standButton.addActionListener(e -> { gameLogic.stand(); updateGUI(); });
        doubleDownButton.addActionListener(e -> { gameLogic.doubleDown(); updateGUI(); }); surrenderButton.addActionListener(e -> { gameLogic.surrender(); updateGUI(); });
        insuranceButton.addActionListener(e -> { gameLogic.requestInsurance(); updateGUI(); }); neitherButton.addActionListener(e -> { gameLogic.chooseNeitherInsuranceSurrender(); updateGUI(); });
        nextRoundButton.addActionListener(e -> { gameLogic.nextRound(); updateGUI(); }); restartButton.addActionListener(e -> { gameLogic.restartGame(); updateGUI(); });
        exitButton.addActionListener(e -> System.exit(0));
    }


    // --- Central Update Method ---
    public void updateGUI() {
        // Ensure called on EDT
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::updateGUI);
            return;
        }
        System.out.println("[DEBUG updateGUI] Updating..."); // DEBUG
        updateLabels();
        updateCardDisplay(dealerCardLabels, gameLogic.getDealerCards(), gameLogic.isDealerCardHidden());
        updateCardDisplay(playerCardsLabels, gameLogic.getPlayerCards(), false);
        updateButtonStates();

        // Explicitly revalidate/repaint the areas containing cards
        if (dealerArea != null) {
            dealerArea.revalidate();
            dealerArea.repaint();
        }
        if (playerArea != null) {
            playerArea.revalidate();
            playerArea.repaint();
        }

        this.revalidate();
        this.repaint();
        System.out.println("[DEBUG updateGUI] Update complete."); // DEBUG
    }

    // --- Update Helper Methods (updateLabels) ---
    // ... (Keep updateLabels method exactly as it was) ...
    private void updateLabels() {
        User user = gameLogic.getCurrentUser();
        scoreLabel.setText("Wins: " + user.getWins() + " | Losses: " + user.getLosses() + " | Pushes: " + user.getPushes());
        chipsLabel.setText("Chips: " + user.getChips());
        gameMessageLabel.setText(gameLogic.getGameMessage());
        playerHandValueLabel.setText("Player Hand: " + gameLogic.getPlayerHandValue());
        if (gameLogic.isDealerCardHidden()) {
            dealerHandValueLabel.setText("Dealer Shows: " + gameLogic.getDealerVisibleValue() + " + ?");
        } else {
            dealerHandValueLabel.setText("Dealer Hand: " + gameLogic.getDealerHandValue());
        }
    }

    // --- updateCardDisplay (Modified for Debugging) ---
    private void updateCardDisplay(JLabel[] cardLabels, List<Card> cards, boolean hideSecondCard) {
        boolean isDealer = (cardLabels == dealerCardLabels); // Identify which hand
        String handName = isDealer ? "Dealer" : "Player";
        System.out.println("[DEBUG updateCardDisplay] Updating " + handName + ". Hand size: " + cards.size()); // DEBUG

        for (int i = 0; i < cardLabels.length; i++) {
            // Check if label exists (should always be true after setup)
            if(cardLabels[i] == null) {
                System.err.println("ERROR: " + handName + " card label at index " + i + " is NULL!");
                continue;
            }

            if (i < cards.size()) {
                Card card = cards.get(i);
                if (card == null) {
                    System.err.println("ERROR: Card object at index " + i + " in " + handName + " hand is NULL!");
                    cardLabels[i].setIcon(createPlaceholderIcon(Color.MAGENTA, "ERR")); // Show error placeholder
                    cardLabels[i].setVisible(true);
                    continue;
                }

                boolean hideThisCard = (i == 1 && hideSecondCard);
                String cardStr = card.toString(); // Get string representation
                System.out.println("  " + handName + " Card " + i + ": " + cardStr + (hideThisCard ? " (Hidden)" : "")); // DEBUG

                ImageIcon icon = hideThisCard ? cardDownIcon : getCardImage(cardStr);

                if (icon == null) {
                    System.err.println("ERROR: getCardImage returned NULL for " + cardStr);
                    cardLabels[i].setIcon(createPlaceholderIcon(Color.MAGENTA, "NULL")); // Show error placeholder
                } else {
                    System.out.println("    -> Icon: " + (icon == cardDownIcon ? "CardBack" : "Loaded Image") + ", Size: " + icon.getIconWidth() + "x" + icon.getIconHeight()); // DEBUG
                    cardLabels[i].setIcon(icon);
                }
                cardLabels[i].setVisible(true); // Make label visible
                System.out.println("    -> Label " + i + " setVisible(true)"); // DEBUG

            } else {
                // No card at this position
                if (cardLabels[i].isVisible()) { // Only print if state changes
                    System.out.println("  " + handName + " Card Label " + i + ": Hiding (no card)"); // DEBUG
                }
                cardLabels[i].setIcon(null);
                cardLabels[i].setVisible(false);
            }
        }
        // Explicit repaint on the parent panel after updating children
        if (cardLabels[0] != null && cardLabels[0].getParent() != null) {
            cardLabels[0].getParent().revalidate();
            cardLabels[0].getParent().repaint();
            System.out.println("[DEBUG updateCardDisplay] Repainted parent panel for " + handName); //DEBUG
        }
    }

    // --- Card Image Loading (getCardImage - Keep as is) ---
    // ... (Keep getCardImage method exactly as it was) ...
    private ImageIcon getCardImage(String cardString) {
        if (cardString == null || !cardString.contains(" of ") || !cardString.contains("(")) { return cardDownIcon; }
        String rankName = "Unknown", suitName = "Unknown", shortCode = "?";
        try {
            String[] parts = cardString.split(" of ");
            if (parts.length == 2) {
                rankName = parts[0].trim(); String suitAndValue = parts[1].trim(); int parenIndex = suitAndValue.indexOf('(');
                if (parenIndex > 0) suitName = suitAndValue.substring(0, parenIndex).trim(); else return cardDownIcon;
            } else return cardDownIcon;
            String rankShort = rankName.equals("Ten") ? "T" : rankName.substring(0, 1); String suitShort = suitName.substring(0, 1); shortCode = rankShort + suitShort;
            String filename = rankName + suitName + ".png"; String resourcePath = CARD_IMAGE_RESOURCE_PATH + filename;
            java.net.URL imgURL = getClass().getClassLoader().getResource(resourcePath);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    return new ImageIcon(originalIcon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
                } else { System.err.println("Error loading image (incomplete): " + resourcePath); return createPlaceholderIcon(Color.RED, shortCode); }
            } else { System.err.println("Error loading image: Resource not found at '" + resourcePath + "'"); return createPlaceholderIcon(Color.BLUE, shortCode); }
        } catch (Exception e) { System.err.println("Exception processing card string '" + cardString + "' or loading image: " + e.getMessage()); return createPlaceholderIcon(Color.ORANGE, shortCode); }
    }

    // --- updateButtonStates - Keep as is ---
    // ... (Keep updateButtonStates method exactly as it was) ...
    private void updateButtonStates() {
        EnumSet<GameLogic.PlayerAction> availableActions = gameLogic.getAvailableActions();
        GameLogic.GameState currentState = gameLogic.getCurrentState();
        CardLayout cl = (CardLayout) (controlsPanel.getLayout());
        switch (currentState) {
            case BETTING: cl.show(controlsPanel, "BETTING"); break;
            case INSURANCE_SURRENDER: case PLAYER_TURN: cl.show(controlsPanel, "ACTIONS"); break;
            case ROUND_OVER: case GAME_OVER: cl.show(controlsPanel, "ROUND_END"); break;
            default: cl.show(controlsPanel, "ACTIONS"); break;
        }
        bet50Button.setVisible(availableActions.contains(GameLogic.PlayerAction.BET_50));
        bet100Button.setVisible(availableActions.contains(GameLogic.PlayerAction.BET_100));
        betAllButton.setVisible(availableActions.contains(GameLogic.PlayerAction.BET_ALL));
        hitButton.setVisible(availableActions.contains(GameLogic.PlayerAction.HIT));
        standButton.setVisible(availableActions.contains(GameLogic.PlayerAction.STAND));
        doubleDownButton.setVisible(availableActions.contains(GameLogic.PlayerAction.DOUBLE_DOWN));
        surrenderButton.setVisible(availableActions.contains(GameLogic.PlayerAction.SURRENDER));
        insuranceButton.setVisible(availableActions.contains(GameLogic.PlayerAction.INSURANCE));
        neitherButton.setVisible(availableActions.contains(GameLogic.PlayerAction.NEITHER));
        boolean enableActions = (currentState == GameLogic.GameState.PLAYER_TURN || currentState == GameLogic.GameState.INSURANCE_SURRENDER);
        hitButton.setEnabled(enableActions && hitButton.isVisible()); standButton.setEnabled(enableActions && standButton.isVisible());
        doubleDownButton.setEnabled(enableActions && doubleDownButton.isVisible()); surrenderButton.setEnabled(enableActions && surrenderButton.isVisible());
        insuranceButton.setEnabled(enableActions && insuranceButton.isVisible()); neitherButton.setEnabled(enableActions && neitherButton.isVisible());
        nextRoundButton.setVisible(availableActions.contains(GameLogic.PlayerAction.NEXT_ROUND));
        restartButton.setVisible(availableActions.contains(GameLogic.PlayerAction.RESTART));
        exitButton.setVisible(availableActions.contains(GameLogic.PlayerAction.EXIT));
        bettingControlsPanel.revalidate(); bettingControlsPanel.repaint(); actionControlsPanel.revalidate(); actionControlsPanel.repaint();
        roundEndControlsPanel.revalidate(); roundEndControlsPanel.repaint(); controlsPanel.revalidate(); controlsPanel.repaint();
    }
}