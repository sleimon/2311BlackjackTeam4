package com.blackjack.GUI;

import com.blackjack.Models.Card; // Ensure Card model is accessible
import com.blackjack.Main; // To return to main menu

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener; // Needed for listeners
import java.awt.image.BufferedImage; // For placeholder icon
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Interactive Tutorial GUI for Blackjack.
 * Guides the user through actions by highlighting buttons.
 * Accepts and uses the current username for returning to the main menu.
 */
@SuppressWarnings("serial")
public class TutorialGUI extends JPanel {

    // --- Fields ---
    private TutorialScript tutorialScript;
    private String currentUsername; // <-- ADDED: Store the username

    // Panels (Keep as is)
    private JPanel centerPanel, dealerArea, playerArea, messagePanel, controlsPanelContainer;
    private JPanel bettingControlsPanel, actionControlsPanel, roundEndControlsPanel;

    // Labels (Keep as is)
    private JLabel playerHandValueLabel, dealerHandValueLabel, tutorialMessageLabel;

    // Card Labels (Keep as is)
    private final JLabel[] dealerCardLabels = new JLabel[5];
    private final JLabel[] playerCardsLabels = new JLabel[5];

    // Buttons (Keep as is)
    private JButton hitButton, standButton;
    private JButton doubleDownButton;
    private JButton surrenderButton;
    private JButton insuranceButton;
    private JButton neitherButton;
    private JButton bet50Button, bet100Button, betAllButton;
    private JButton nextRoundButton;
    private JButton exitTutorialButton;

    // Button Mapping & Highlighting (Keep as is)
    private Map<TutorialAction, JButton> actionButtonMap;
    private JButton currentlyHighlightedButton = null;
    private Border defaultButtonBorder;
    private static final Border HIGHLIGHT_BORDER = new LineBorder(Color.YELLOW, 3);

    // --- Constants & Styling (Keep as is) ---
    public static final int CARD_WIDTH = 100;
    public static final int CARD_HEIGHT = 145;
    public static final String CARD_IMAGE_RESOURCE_PATH = "cards/";
    private ImageIcon cardDownIcon;
    private static final Color FELT_GREEN = new Color(0, 80, 0);
    private static final Color BORDER_COLOR = FELT_GREEN.darker();
    private static final Color BUTTON_COLOR = new Color(200, 200, 200);
    private static final Color BUTTON_TEXT_COLOR = Color.BLACK;
    private static final Font VALUE_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font MESSAGE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final boolean DEBUG_BORDERS = false;

    // --- Constructor ---
    // Modified to accept username
    public TutorialGUI(String username) {
        this.currentUsername = username; // <-- ADDED: Store username

        // Run GUI setup on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            this.tutorialScript = new TutorialScript();
            this.actionButtonMap = new HashMap<>();
            loadCardDownImage();
            if (cardDownIcon == null) {
                System.err.println("FATAL: CardDown icon failed to load. Tutorial cannot proceed.");
                setLayout(new BorderLayout());
                JLabel errorLabel = new JLabel("Error: Failed to load card images. Tutorial cannot start.", SwingConstants.CENTER);
                errorLabel.setForeground(Color.RED);
                errorLabel.setFont(MESSAGE_FONT);
                add(errorLabel, BorderLayout.CENTER);
                return;
            }
            showIntroPopup(); // Show intro popup FIRST
            initializeGUI();
            mapActionsToButtons(); // Map AFTER buttons created
            updateTutorialDisplay(); // Show first step
        });
    }

    // --- Introductory Popup --- (Keep as is)
    private void showIntroPopup() {
        String introMessage = "<html><h2>Welcome to Blackjack!</h2><p><b>The Goal:</b> Get a hand total closer to 21 than the dealer's hand, without going over 21 ('Busting').</p><p><b>Card Values:</b><br>  • Number cards (2-10) = Face value<br>  • Face cards (King, Queen, Jack) = 10<br>  • Ace = 1 or 11 (whichever helps your hand most without busting)</p><p>Follow the instructions and click the <font color='yellow'>highlighted</font> buttons to proceed.</p><p>Click 'OK' to start the interactive tutorial.</p></html>";
        JOptionPane.showMessageDialog(this, introMessage, "Blackjack Basics", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Initialization Methods --- (Keep as is)
    private void loadCardDownImage() { /* ... */
        try {
            java.io.InputStream imgStream = getClass().getClassLoader().getResourceAsStream(CARD_IMAGE_RESOURCE_PATH + "CardDown.png");
            if (imgStream == null) {
                System.err.println("Error loading CardDown.png: Resource not found at classpath:" + CARD_IMAGE_RESOURCE_PATH + "CardDown.png");
                cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back");
                return;
            }
            ImageIcon originalIcon = new ImageIcon(javax.imageio.ImageIO.read(imgStream));
            imgStream.close();
            if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE || originalIcon.getIconWidth() <= 0) {
                System.err.println("CardDown.png image data invalid or loading failed.");
                cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back");
            } else {
                cardDownIcon = new ImageIcon(originalIcon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            System.err.println("Exception loading CardDown.png: " + e);
            e.printStackTrace();
            cardDownIcon = createPlaceholderIcon(Color.DARK_GRAY, "Back");
        }
    }

    private ImageIcon createPlaceholderIcon(Color color, String text) { /* ... */
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

    private void initializeGUI() { /* ... */
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(FELT_GREEN.darker());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        createCenterPanel();
        createControlsPanels();
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(controlsPanelContainer, BorderLayout.SOUTH);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        exitTutorialButton = createStyledButton("Exit Tutorial");
        JPanel exitButtonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitButtonWrapper.setOpaque(false);
        exitButtonWrapper.add(exitTutorialButton);
        topPanel.add(exitButtonWrapper, BorderLayout.EAST);
        this.add(topPanel, BorderLayout.NORTH);
        setupActionListeners();
    }

    private void createCenterPanel() { /* ... */
        centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(FELT_GREEN);
        centerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, BORDER_COLOR, BORDER_COLOR.brighter()));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.4; // Dealer Area
        dealerArea = createCardAreaPanel("Dealer Hand: ?");
        dealerHandValueLabel = (JLabel) dealerArea.getComponent(0);
        dealerArea.setPreferredSize(new Dimension(600, CARD_HEIGHT + 60));
        if (DEBUG_BORDERS) dealerArea.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
        setupCardLabels(dealerCardLabels, dealerArea);
        centerPanel.add(dealerArea, gbc);
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Message Area
        messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.setOpaque(false);
        tutorialMessageLabel = new JLabel("Loading Tutorial...", SwingConstants.CENTER);
        tutorialMessageLabel.setFont(MESSAGE_FONT);
        tutorialMessageLabel.setForeground(TEXT_COLOR);
        tutorialMessageLabel.setPreferredSize(new Dimension(550, 60));
        messagePanel.add(tutorialMessageLabel);
        centerPanel.add(messagePanel, gbc);
        gbc.gridy = 2;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH; // Player Area
        playerArea = createCardAreaPanel("Player Hand: ?");
        playerHandValueLabel = (JLabel) playerArea.getComponent(0);
        playerArea.setPreferredSize(new Dimension(600, CARD_HEIGHT + 60));
        if (DEBUG_BORDERS) playerArea.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1));
        setupCardLabels(playerCardsLabels, playerArea);
        centerPanel.add(playerArea, gbc);
    }

    private JPanel createCardAreaPanel(String initialLabelText) { /* ... */
        JPanel areaPanel = new JPanel();
        areaPanel.setLayout(null);
        areaPanel.setOpaque(false);
        JLabel valueLabel = new JLabel(initialLabelText);
        valueLabel.setFont(VALUE_LABEL_FONT);
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setBounds(10, 5, 400, 25);
        areaPanel.add(valueLabel);
        return areaPanel;
    }

    private void setupCardLabels(JLabel[] labels, JPanel parentPanel) { /* ... */
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

    private void createControlsPanels() { /* ... */
        controlsPanelContainer = new JPanel(new CardLayout());
        controlsPanelContainer.setOpaque(false);
        bettingControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bettingControlsPanel.setOpaque(false);
        bet50Button = createStyledButton("Bet 50");
        bet100Button = createStyledButton("Bet 100");
        betAllButton = createStyledButton("Bet All");
        bettingControlsPanel.add(bet50Button);
        bettingControlsPanel.add(bet100Button);
        bettingControlsPanel.add(betAllButton);
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
        roundEndControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        roundEndControlsPanel.setOpaque(false);
        nextRoundButton = createStyledButton("Next Round");
        roundEndControlsPanel.add(nextRoundButton);
        controlsPanelContainer.add(bettingControlsPanel, "BETTING");
        controlsPanelContainer.add(actionControlsPanel, "ACTIONS");
        controlsPanelContainer.add(roundEndControlsPanel, "ROUND_END");
        if (bet50Button != null) {
            defaultButtonBorder = bet50Button.getBorder();
        } else if (hitButton != null) {
            defaultButtonBorder = hitButton.getBorder();
        } else {
            defaultButtonBorder = new EmptyBorder(10, 10, 10, 10);
        }
    }

    private JButton createStyledButton(String text) { /* ... */
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        Border standardBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BUTTON_COLOR.darker()), new EmptyBorder(8, 15, 8, 15));
        button.setBorder(standardBorder);
        if (defaultButtonBorder == null && !text.equals("Exit Tutorial")) {
            defaultButtonBorder = standardBorder;
        } else if (text.equals("Exit Tutorial")) {
            button.setPreferredSize(new Dimension(100, 30));
            button.setBackground(new Color(150, 0, 0));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED.brighter()), new EmptyBorder(5, 10, 5, 10)));
        }
        return button;
    }

    private void mapActionsToButtons() { /* ... */
        actionButtonMap.put(TutorialAction.BET_50, bet50Button);
        actionButtonMap.put(TutorialAction.BET_100, bet100Button);
        actionButtonMap.put(TutorialAction.BET_ALL, betAllButton);
        actionButtonMap.put(TutorialAction.HIT, hitButton);
        actionButtonMap.put(TutorialAction.STAND, standButton);
        actionButtonMap.put(TutorialAction.DOUBLE_DOWN, doubleDownButton);
        actionButtonMap.put(TutorialAction.SURRENDER, surrenderButton);
        actionButtonMap.put(TutorialAction.INSURANCE, insuranceButton);
        actionButtonMap.put(TutorialAction.NEITHER, neitherButton);
        actionButtonMap.put(TutorialAction.NEXT_ROUND, nextRoundButton);
        actionButtonMap.put(TutorialAction.EXIT, exitTutorialButton);
    }

    private void handleTutorialAction(TutorialAction clickedAction) { /* ... */
        TutorialStep currentStep = tutorialScript.getCurrentStep();
        if (currentStep == null) return;
        if (currentStep.getExpectedAction() == clickedAction) {
            boolean advanced = tutorialScript.advanceStep();
            if (advanced) {
                updateTutorialDisplay();
                TutorialStep nextStep = tutorialScript.getCurrentStep();
                if (nextStep != null && isInformationalStep(nextStep.getExpectedAction())) {
                    Timer timer = new Timer(1200, e -> handleTutorialAction(nextStep.getExpectedAction()));
                    timer.setRepeats(false);
                    timer.start();
                }
            } else {
                if (clickedAction != TutorialAction.EXIT) {
                    System.err.println("Script end/error: Didn't advance but action wasn't EXIT.");
                }
            }
        } else {
            System.out.println("Incorrect action. Expected: " + currentStep.getExpectedAction() + ", Clicked: " + clickedAction);
            flashButton(actionButtonMap.get(currentStep.getExpectedAction()));
        }
    }

    private boolean isInformationalStep(TutorialAction action) { /* ... */
        return action == TutorialAction.START_TUTORIAL;
    }

    // Modified Exit Button Listener
    private void setupActionListeners() {
        // Betting Listeners...
        bet50Button.addActionListener(e -> handleTutorialAction(TutorialAction.BET_50));
        bet100Button.addActionListener(e -> handleTutorialAction(TutorialAction.BET_100));
        betAllButton.addActionListener(e -> handleTutorialAction(TutorialAction.BET_ALL));
        // Action Listeners...
        hitButton.addActionListener(e -> handleTutorialAction(TutorialAction.HIT));
        standButton.addActionListener(e -> handleTutorialAction(TutorialAction.STAND));
        doubleDownButton.addActionListener(e -> handleTutorialAction(TutorialAction.DOUBLE_DOWN));
        surrenderButton.addActionListener(e -> handleTutorialAction(TutorialAction.SURRENDER));
        insuranceButton.addActionListener(e -> handleTutorialAction(TutorialAction.INSURANCE));
        neitherButton.addActionListener(e -> handleTutorialAction(TutorialAction.NEITHER));
        // Round End Listener...
        nextRoundButton.addActionListener(e -> handleTutorialAction(TutorialAction.NEXT_ROUND));

        // Exit Listener (USES STORED USERNAME)
        exitTutorialButton.addActionListener(e -> {
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (currentFrame != null) {
                // Use the stored username when returning
                Main.returnToMainMenu(this.currentUsername, currentFrame); // <-- MODIFIED
            } else {
                System.err.println("Error: No parent frame for TutorialGUI.");
                System.exit(0); // Fallback
            }
        });
    }


    // --- Update Methods --- (Keep as is)
    private void updateTutorialDisplay() { /* ... */
        TutorialStep currentStep = tutorialScript.getCurrentStep();
        if (currentStep == null) {
            System.err.println("Err: Null step in update.");
            return;
        }
        tutorialMessageLabel.setText(currentStep.getMessage());
        playerHandValueLabel.setText(currentStep.getPlayerValueText());
        dealerHandValueLabel.setText(currentStep.getDealerValueText());
        updateCardDisplayForTutorial(playerCardsLabels, currentStep.getPlayerCards(), false);
        updateCardDisplayForTutorial(dealerCardLabels, currentStep.getDealerCards(), currentStep.isDealerCardHidden());
        TutorialAction expectedAction = currentStep.getExpectedAction();
        JButton buttonToHighlight = actionButtonMap.get(expectedAction);
        String panelToShow = getPanelNameForAction(expectedAction);
        CardLayout cl = (CardLayout) controlsPanelContainer.getLayout();
        cl.show(controlsPanelContainer, panelToShow);
        unhighlightCurrentButton();
        disableAllActionButtons();
        if (buttonToHighlight != null && buttonToHighlight != exitTutorialButton) {
            buttonToHighlight.setEnabled(true);
            highlightButton(buttonToHighlight);
            currentlyHighlightedButton = buttonToHighlight;
        } else if (expectedAction != TutorialAction.EXIT) {
            System.err.println("Warn: No button mapped or EXIT expected: " + expectedAction);
        }
        if (exitTutorialButton != null) {
            exitTutorialButton.setEnabled(true);
        }
        this.revalidate();
        this.repaint();
    }

    private String getPanelNameForAction(TutorialAction action) { /* ... */
        switch (action) {
            case BET_50:
            case BET_100:
            case BET_ALL:
                return "BETTING";
            case HIT:
            case STAND:
            case DOUBLE_DOWN:
            case SURRENDER:
            case INSURANCE:
            case NEITHER:
                return "ACTIONS";
            case NEXT_ROUND:
            case RESTART:
                return "ROUND_END";
            case START_TUTORIAL:
                return "BETTING";
            case EXIT:
                return determineExitPanelContext();
            default:
                System.err.println("Warn: Unknown panel for action: " + action);
                return "ACTIONS";
        }
    }

    private String determineExitPanelContext() { /* ... */
        if (tutorialScript != null && tutorialScript.isAtEnd()) {
            return "ROUND_END";
        }
        return "ACTIONS";
    }

    private void disableAllActionButtons() { /* ... */
        for (Map.Entry<TutorialAction, JButton> entry : actionButtonMap.entrySet()) {
            if (entry.getKey() != TutorialAction.EXIT && entry.getValue() != null) {
                entry.getValue().setEnabled(false);
            }
        }
    }

    private void highlightButton(JButton button) { /* ... */
        if (button != null && defaultButtonBorder != null) {
            button.setBorder(HIGHLIGHT_BORDER);
        } else if (button != null) {
            button.setBorder(HIGHLIGHT_BORDER);
            System.err.println("Warn: Highlighting but default border null.");
        }
    }

    private void unhighlightCurrentButton() { /* ... */
        if (currentlyHighlightedButton != null && defaultButtonBorder != null) {
            currentlyHighlightedButton.setBorder(defaultButtonBorder);
            currentlyHighlightedButton = null;
        } else if (currentlyHighlightedButton != null) {
            currentlyHighlightedButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BUTTON_COLOR.darker()), new EmptyBorder(8, 15, 8, 15)));
            System.err.println("Warn: Unhighlighting using fallback border.");
            currentlyHighlightedButton = null;
        }
    }

    private void flashButton(JButton button) { /* ... */
        if (button == null) return;
        Border oB = button.getBorder();
        Color oBg = button.getBackground();
        button.setBorder(new LineBorder(Color.RED, 3));
        button.setBackground(Color.PINK);
        Timer t = new Timer(150, e -> {
            button.setBorder(oB);
            button.setBackground(oBg);
            if (button == currentlyHighlightedButton) {
                highlightButton(button);
            }
        });
        t.setRepeats(false);
        t.start();
    }

    private void updateCardDisplayForTutorial(JLabel[] cardLabels, List<Card> cards, boolean hideSecondCard) { /* ... */
        if (cardDownIcon == null) {
            return;
        }
        for (int i = 0; i < cardLabels.length; i++) {
            if (cardLabels[i] == null) continue;
            if (i < cards.size()) {
                Card card = cards.get(i);
                ImageIcon iconToShow;
                if (card == null) {
                    iconToShow = cardDownIcon;
                } else {
                    boolean hideThis = (i == 1 && hideSecondCard);
                    iconToShow = hideThis ? cardDownIcon : getCardImage(card.toString());
                }
                cardLabels[i].setIcon(iconToShow);
                cardLabels[i].setVisible(iconToShow != null);
            } else {
                cardLabels[i].setIcon(null);
                cardLabels[i].setVisible(false);
            }
        }
        if (cardLabels.length > 0 && cardLabels[0] != null && cardLabels[0].getParent() != null) {
            cardLabels[0].getParent().revalidate();
            cardLabels[0].getParent().repaint();
        }
    }

    private ImageIcon getCardImage(String cardString) { /* ... */
        if (cardDownIcon == null) return createPlaceholderIcon(Color.MAGENTA, "NO BKG");
        if (cardString == null || !cardString.contains(" of ") || !cardString.contains("(")) {
            return cardDownIcon;
        }
        String rN = "Unk", sN = "Unk";
        try {
            String[] parts = cardString.split(" of ");
            if (parts.length == 2) {
                rN = parts[0].trim();
                String sV = parts[1].trim();
                int pI = sV.indexOf('(');
                sN = (pI > 0) ? sV.substring(0, pI).trim() : sV;
            } else {
                System.err.println("Parse err card str: " + cardString);
                return cardDownIcon;
            }
            String filename = (rN + sN + ".png").replaceAll("\\s+", "");
            String rp = CARD_IMAGE_RESOURCE_PATH + filename;
            java.io.InputStream imgStream = getClass().getClassLoader().getResourceAsStream(rp);
            if (imgStream != null) {
                ImageIcon oI = new ImageIcon(javax.imageio.ImageIO.read(imgStream));
                imgStream.close();
                if (oI.getImageLoadStatus() == MediaTracker.COMPLETE && oI.getIconWidth() > 0) {
                    return new ImageIcon(oI.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
                } else {
                    String rS = rN.substring(0, 1), sS = sN.substring(0, 1);
                    return createPlaceholderIcon(Color.RED, rS + sS);
                }
            } else {
                System.err.println("Res not found: '" + rp + "'");
                String rS = rN.substring(0, 1), sS = sN.substring(0, 1);
                return createPlaceholderIcon(Color.BLUE, rS + sS);
            }
        } catch (Exception e) {
            System.err.println("Exce proc card '" + cardString + "' or loading img: " + e.getMessage());
            e.printStackTrace();
            String rS = rN.substring(0, 1), sS = sN.substring(0, 1);
            return createPlaceholderIcon(Color.ORANGE, rS + sS);
        }
    }

} // End of TutorialGUI class