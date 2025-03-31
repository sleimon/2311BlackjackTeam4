package com.blackjack.GUI;

import com.blackjack.Models.Card;
// ** Ensure this import points to your GameLogic class correctly **
import com.blackjack.Services.GameLogic;
import com.blackjack.Models.User;
import com.blackjack.Main; // Import Main to call returnToMainMenu

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.EnumSet;

@SuppressWarnings("serial")
public class GameGUI extends JPanel {

    // --- Fields ---
    private final GameLogic gameLogic;
    // Panels
    private JPanel topPanel, centerPanel, dealerArea, playerArea, messagePanel, controlsPanel;
    private JPanel bettingControlsPanel, actionControlsPanel, roundEndControlsPanel;
    // Labels
    private JLabel scoreLabel, chipsLabel, playerHandValueLabel, dealerHandValueLabel, gameMessageLabel;
    // Card Labels
    private final JLabel[] dealerCardLabels = new JLabel[11];
    private final JLabel[] playerCardsLabels = new JLabel[11];
    // Buttons
    private JButton hitButton, standButton, surrenderButton, insuranceButton, doubleDownButton;
    private JButton nextRoundButton, neitherButton, betAllButton, bet50Button, bet100Button;
    private JButton restartButton, exitButton, backToMenuButton; // Added backToMenuButton

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

    // --- Constructor ---
    public GameGUI(GameLogic logic) {
        if (logic == null) throw new IllegalArgumentException("GameLogic cannot be null");
        this.gameLogic = logic;
        loadCardDownImage();
        initializeGUI();
        updateGUI();
    }

    // --- Initialization Methods ---
    private void loadCardDownImage() {
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource(CARD_IMAGE_RESOURCE_PATH + "CardDown.png");
            if (imgURL == null) { System.err.println("Error loading CardDown.png: Resource not found at classpath:" + CARD_IMAGE_RESOURCE_PATH + "CardDown.png"); cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back"); return; }
            ImageIcon originalIcon = new ImageIcon(imgURL);
            if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE) { System.err.println("CardDown.png image data incomplete."); cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back"); }
            else cardDownIcon = new ImageIcon(originalIcon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
        } catch (Exception e) { System.err.println("Exception loading CardDown.png: " + e); cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back"); }
    }
    private ImageIcon createPlaceholderIcon(Color color, String text) {
        BufferedImage image = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try { g.setColor(color); g.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT); g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.BOLD, 14)); FontMetrics fm = g.getFontMetrics(); int x = (CARD_WIDTH - fm.stringWidth(text)) / 2; int y = (CARD_HEIGHT + fm.getAscent()) / 2 - fm.getDescent(); g.drawString(text, x, y);
        } finally { g.dispose(); } return new ImageIcon(image);
    }
    private void initializeGUI() {
        this.setLayout(new BorderLayout(10, 10)); this.setBackground(FELT_GREEN.darker());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        createTopPanel(); createCenterPanel(); createControlsPanel();
        this.add(topPanel, BorderLayout.NORTH); this.add(centerPanel, BorderLayout.CENTER); this.add(controlsPanel, BorderLayout.SOUTH);
    }
    private void createTopPanel() {
        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5)); topPanel.setBackground(FELT_GREEN.darker());
        topPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, BORDER_COLOR, BORDER_COLOR.brighter()), new EmptyBorder(5, 10, 5, 10) ));
        scoreLabel = new JLabel("W:0 L:0 P:0"); styleLabel(scoreLabel); topPanel.add(scoreLabel);
        chipsLabel = new JLabel("Chips: 0"); styleLabel(chipsLabel); topPanel.add(chipsLabel);
    }
    private void createCenterPanel() {
        centerPanel = new JPanel(new GridBagLayout()); centerPanel.setBackground(FELT_GREEN);
        centerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, BORDER_COLOR, BORDER_COLOR.brighter()));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 10, 10, 10); gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0;
        // Dealer Area
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.4; dealerArea = createCardAreaPanel("Dealer Hand: ?"); dealerHandValueLabel = (JLabel) dealerArea.getComponent(0);
        dealerArea.setPreferredSize(new Dimension(600, CARD_HEIGHT + 60)); if (DEBUG_BORDERS) dealerArea.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
        setupCardLabels(dealerCardLabels, dealerArea); centerPanel.add(dealerArea, gbc);
        // Message Area
        gbc.gridy = 1; gbc.weighty = 0.1; gbc.fill = GridBagConstraints.HORIZONTAL; messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); messagePanel.setOpaque(false);
        gameMessageLabel = new JLabel("Msg", SwingConstants.CENTER); gameMessageLabel.setFont(MESSAGE_FONT); gameMessageLabel.setForeground(TEXT_COLOR);
        messagePanel.add(gameMessageLabel); centerPanel.add(messagePanel, gbc);
        // Player Area
        gbc.gridy = 2; gbc.weighty = 0.5; gbc.fill = GridBagConstraints.BOTH; playerArea = createCardAreaPanel("Player Hand: ?"); playerHandValueLabel = (JLabel) playerArea.getComponent(0);
        playerArea.setPreferredSize(new Dimension(600, CARD_HEIGHT + 60)); if (DEBUG_BORDERS) playerArea.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1));
        setupCardLabels(playerCardsLabels, playerArea); centerPanel.add(playerArea, gbc);
    }
    private JPanel createCardAreaPanel(String initialLabelText) { JPanel areaPanel = new JPanel(); areaPanel.setLayout(null); areaPanel.setOpaque(false); JLabel valueLabel = new JLabel(initialLabelText); styleLabel(valueLabel); valueLabel.setBounds(10, 5, 300, 25); areaPanel.add(valueLabel); return areaPanel; }
    private void setupCardLabels(JLabel[] labels, JPanel parentPanel) { int cardX = 10; int cardY = 35; int xOffset = CARD_WIDTH / 2 + 10; for (int i = 0; i < labels.length; i++) { labels[i] = new JLabel(); labels[i].setBounds(cardX + (i * xOffset), cardY, CARD_WIDTH, CARD_HEIGHT); labels[i].setIcon(null); labels[i].setVisible(false); parentPanel.add(labels[i]); } }
    private void createControlsPanel() {
        controlsPanel = new JPanel(new CardLayout()); controlsPanel.setOpaque(false);
        // Betting Panel
        bettingControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); bettingControlsPanel.setOpaque(false);
        bet50Button = createStyledButton("Bet 50"); bet100Button = createStyledButton("Bet 100"); betAllButton = createStyledButton("Bet All");
        bettingControlsPanel.add(bet50Button); bettingControlsPanel.add(bet100Button); bettingControlsPanel.add(betAllButton);
        // Action Panel
        actionControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); actionControlsPanel.setOpaque(false);
        hitButton = createStyledButton("Hit"); standButton = createStyledButton("Stand"); doubleDownButton = createStyledButton("Double Down");
        surrenderButton = createStyledButton("Surrender"); insuranceButton = createStyledButton("Insurance"); neitherButton = createStyledButton("Decline Ins.");
        actionControlsPanel.add(hitButton); actionControlsPanel.add(standButton); actionControlsPanel.add(doubleDownButton);
        actionControlsPanel.add(surrenderButton); actionControlsPanel.add(insuranceButton); actionControlsPanel.add(neitherButton);
        // Round End Panel
        roundEndControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); roundEndControlsPanel.setOpaque(false);
        nextRoundButton = createStyledButton("Next Round"); restartButton = createStyledButton("Restart Game");
        backToMenuButton = createStyledButton("Main Menu"); // Defined button
        exitButton = createStyledButton("Exit App");
        roundEndControlsPanel.add(nextRoundButton); roundEndControlsPanel.add(restartButton);
        roundEndControlsPanel.add(backToMenuButton); // Added button to panel
        roundEndControlsPanel.add(exitButton);
        // Add panels to CardLayout
        controlsPanel.add(bettingControlsPanel, "BETTING"); controlsPanel.add(actionControlsPanel, "ACTIONS"); controlsPanel.add(roundEndControlsPanel, "ROUND_END");
        setupButtonActionListeners(); // Setup listeners after all buttons created
    }
    private JButton createStyledButton(String text) { JButton button = new JButton(text); button.setFont(BUTTON_FONT); button.setBackground(BUTTON_COLOR); button.setForeground(BUTTON_TEXT_COLOR); button.setFocusPainted(false); button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); button.setPreferredSize(new Dimension(120, 35)); return button; }
    private void styleLabel(JLabel label) { label.setFont(LABEL_FONT); label.setForeground(TEXT_COLOR); label.setHorizontalAlignment(SwingConstants.LEFT); }
    private void setupButtonActionListeners() {
        bet50Button.addActionListener(e -> { gameLogic.placeBet(50); updateGUI(); });
        bet100Button.addActionListener(e -> { gameLogic.placeBet(100); updateGUI(); });
        betAllButton.addActionListener(e -> { if(gameLogic.getCurrentUser()!=null) gameLogic.placeBet(gameLogic.getCurrentUser().getChips()); updateGUI(); });
        hitButton.addActionListener(e -> { gameLogic.hit(); updateGUI(); });
        standButton.addActionListener(e -> { gameLogic.stand(); updateGUI(); });
        doubleDownButton.addActionListener(e -> { gameLogic.doubleDown(); updateGUI(); });
        surrenderButton.addActionListener(e -> { gameLogic.surrender(); updateGUI(); });
        insuranceButton.addActionListener(e -> { gameLogic.requestInsurance(); updateGUI(); });
        neitherButton.addActionListener(e -> { gameLogic.declineInsurance(); updateGUI(); });
        nextRoundButton.addActionListener(e -> { gameLogic.nextRound(); updateGUI(); });
        restartButton.addActionListener(e -> { gameLogic.restartGame(); updateGUI(); });
        exitButton.addActionListener(e -> System.exit(0));
        // Listener for Back to Menu button
        backToMenuButton.addActionListener(e -> {
            String currentUsername = (gameLogic.getCurrentUser() != null) ? gameLogic.getCurrentUser().getUsername() : "user";
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (currentFrame != null) {
                Main.returnToMainMenu(currentUsername, currentFrame); // Call Main's navigation method
            } else { System.err.println("Error: Could not find parent frame for GameGUI."); }
        });
    }

    // --- Update Methods ---
    public void updateGUI() { if (!SwingUtilities.isEventDispatchThread()) { SwingUtilities.invokeLater(this::updateGUI); return; } updateLabels(); updateCardDisplay(); updateButtonStates(); if (dealerArea != null) { dealerArea.revalidate(); dealerArea.repaint(); } if (playerArea != null) { playerArea.revalidate(); playerArea.repaint(); } this.revalidate(); this.repaint(); }
    private void updateLabels() { User user = gameLogic.getCurrentUser(); if (user == null) return; scoreLabel.setText("W: " + user.getWins() + " | L: " + user.getLosses() + " | P: " + user.getPushes()); chipsLabel.setText("Chips: " + user.getChips()); gameMessageLabel.setText(gameLogic.getGameMessage()); if (gameLogic.getCurrentState() == GameLogic.GameState.BETTING) { playerHandValueLabel.setText("Player Hand: ?"); dealerHandValueLabel.setText("Dealer Hand: ?"); } else { playerHandValueLabel.setText("Player Hand: " + gameLogic.getPlayerHandValue()); if (gameLogic.isDealerCardHidden()) { dealerHandValueLabel.setText("Dealer Shows: " + gameLogic.getDealerVisibleValue() + " + ?"); } else { dealerHandValueLabel.setText("Dealer Hand: " + gameLogic.getDealerHandValue()); } } }
    private void updateCardDisplay() { if (gameLogic.getCurrentState() == GameLogic.GameState.BETTING) { if(dealerCardLabels[0]!=null) { dealerCardLabels[0].setIcon(cardDownIcon); dealerCardLabels[0].setVisible(true); } if(dealerCardLabels[1]!=null) { dealerCardLabels[1].setIcon(cardDownIcon); dealerCardLabels[1].setVisible(true); } if(playerCardsLabels[0]!=null) { playerCardsLabels[0].setIcon(cardDownIcon); playerCardsLabels[0].setVisible(true); } if(playerCardsLabels[1]!=null) { playerCardsLabels[1].setIcon(cardDownIcon); playerCardsLabels[1].setVisible(true); } for (int i = 2; i < dealerCardLabels.length; i++) { if(dealerCardLabels[i]!=null) dealerCardLabels[i].setVisible(false); if(playerCardsLabels[i]!=null) playerCardsLabels[i].setVisible(false); } } else { updateSpecificHandDisplay(dealerCardLabels, gameLogic.getDealerCards(), gameLogic.isDealerCardHidden()); updateSpecificHandDisplay(playerCardsLabels, gameLogic.getPlayerCards(), false); } }
    private void updateSpecificHandDisplay(JLabel[] cardLabels, List<Card> cards, boolean hideSecondCard) { for (int i = 0; i < cardLabels.length; i++) { if(cardLabels[i] == null) continue; if (i < cards.size()) { Card card = cards.get(i); if (card == null) { cardLabels[i].setIcon(createPlaceholderIcon(Color.MAGENTA, "ERR")); cardLabels[i].setVisible(true); continue; } boolean hideThisCard = (i == 1 && hideSecondCard); String cardStr = card.toString(); ImageIcon icon = hideThisCard ? cardDownIcon : getCardImage(cardStr); if (icon == null) cardLabels[i].setIcon(createPlaceholderIcon(Color.MAGENTA, "NULL")); else cardLabels[i].setIcon(icon); cardLabels[i].setVisible(true); } else { cardLabels[i].setIcon(null); cardLabels[i].setVisible(false); } } if (cardLabels.length > 0 && cardLabels[0] != null && cardLabels[0].getParent() != null) { cardLabels[0].getParent().revalidate(); cardLabels[0].getParent().repaint(); } }
    private ImageIcon getCardImage(String cardString) { if (cardString == null || !cardString.contains(" of ") || !cardString.contains("(")) return cardDownIcon; String rankName = "Unknown", suitName = "Unknown", shortCode = "?"; try { String[] parts = cardString.split(" of "); if (parts.length == 2) { rankName = parts[0].trim(); String suitAndValue = parts[1].trim(); int parenIndex = suitAndValue.indexOf('('); if (parenIndex > 0) suitName = suitAndValue.substring(0, parenIndex).trim(); else return cardDownIcon; } else return cardDownIcon; String rankShort = rankName.equals("Ten") ? "T" : (rankName.length() > 0 ? rankName.substring(0, 1) : "?"); String suitShort = suitName.length() > 0 ? suitName.substring(0, 1) : "?"; shortCode = rankShort + suitShort; String filename = rankName + suitName + ".png"; String resourcePath = CARD_IMAGE_RESOURCE_PATH + filename; java.net.URL imgURL = getClass().getClassLoader().getResource(resourcePath); if (imgURL != null) { ImageIcon originalIcon = new ImageIcon(imgURL); if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) return new ImageIcon(originalIcon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)); else { System.err.println("Error loading image (incomplete): " + resourcePath); return createPlaceholderIcon(Color.RED, shortCode); } } else { System.err.println("Error loading image: Resource not found at '" + resourcePath + "'"); return createPlaceholderIcon(Color.BLUE, shortCode); } } catch (Exception e) { System.err.println("Exception processing card string '" + cardString + "' or loading image: " + e.getMessage()); return createPlaceholderIcon(Color.ORANGE, shortCode); } }
    private void updateButtonStates() { EnumSet<GameLogic.PlayerAction> availableActions = gameLogic.getAvailableActions(); GameLogic.GameState currentState = gameLogic.getCurrentState(); CardLayout cl = (CardLayout) (controlsPanel.getLayout()); String panelToShow = "ACTIONS"; switch (currentState) { case BETTING: panelToShow = "BETTING"; break; case INSURANCE_SURRENDER: case PLAYER_TURN: panelToShow = "ACTIONS"; break; case ROUND_OVER: case GAME_OVER: panelToShow = "ROUND_END"; break; case DEALER_TURN: panelToShow = "ACTIONS"; break; } cl.show(controlsPanel, panelToShow); bet50Button.setVisible(availableActions.contains(GameLogic.PlayerAction.BET_50)); bet100Button.setVisible(availableActions.contains(GameLogic.PlayerAction.BET_100)); betAllButton.setVisible(availableActions.contains(GameLogic.PlayerAction.BET_ALL)); hitButton.setVisible(availableActions.contains(GameLogic.PlayerAction.HIT)); standButton.setVisible(availableActions.contains(GameLogic.PlayerAction.STAND)); doubleDownButton.setVisible(availableActions.contains(GameLogic.PlayerAction.DOUBLE_DOWN)); surrenderButton.setVisible(availableActions.contains(GameLogic.PlayerAction.SURRENDER)); insuranceButton.setVisible(availableActions.contains(GameLogic.PlayerAction.INSURANCE)); neitherButton.setVisible(availableActions.contains(GameLogic.PlayerAction.NEITHER)); nextRoundButton.setVisible(availableActions.contains(GameLogic.PlayerAction.NEXT_ROUND)); restartButton.setVisible(availableActions.contains(GameLogic.PlayerAction.RESTART)); exitButton.setVisible(availableActions.contains(GameLogic.PlayerAction.EXIT)); backToMenuButton.setVisible(currentState == GameLogic.GameState.ROUND_OVER || currentState == GameLogic.GameState.GAME_OVER); boolean enablePlayerActions = (currentState == GameLogic.GameState.PLAYER_TURN); boolean enableInsuranceActions = (currentState == GameLogic.GameState.INSURANCE_SURRENDER); hitButton.setEnabled(enablePlayerActions && hitButton.isVisible()); standButton.setEnabled(enablePlayerActions && standButton.isVisible()); doubleDownButton.setEnabled(enablePlayerActions && doubleDownButton.isVisible()); surrenderButton.setEnabled(enablePlayerActions && surrenderButton.isVisible()); insuranceButton.setEnabled(enableInsuranceActions && insuranceButton.isVisible()); neitherButton.setEnabled(enableInsuranceActions && neitherButton.isVisible()); nextRoundButton.setEnabled(nextRoundButton.isVisible()); restartButton.setEnabled(restartButton.isVisible()); exitButton.setEnabled(exitButton.isVisible()); backToMenuButton.setEnabled(backToMenuButton.isVisible()); bettingControlsPanel.revalidate(); bettingControlsPanel.repaint(); actionControlsPanel.revalidate(); actionControlsPanel.repaint(); roundEndControlsPanel.revalidate(); roundEndControlsPanel.repaint(); controlsPanel.revalidate(); controlsPanel.repaint(); }

} // End of GameGUI class