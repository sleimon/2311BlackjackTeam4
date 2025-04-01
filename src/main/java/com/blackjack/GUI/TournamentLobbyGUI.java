package com.blackjack.GUI;

import com.blackjack.Main;
import com.blackjack.Services.TournamentService;
import com.blackjack.Services.TournamentGameLogic;
import com.blackjack.Models.Tournament;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.List;

@SuppressWarnings("serial")
public class TournamentLobbyGUI extends JPanel {

    // --- Styling Constants (Reusing from TournamentGUI) ---
    private static final Color FELT_GREEN = new Color(0, 80, 0);
    private static final Color BORDER_COLOR = FELT_GREEN.darker();
    private static final Color BUTTON_COLOR = new Color(200, 200, 200);
    private static final Color BUTTON_TEXT_COLOR = Color.BLACK;
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font MESSAGE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Color TEXT_COLOR = Color.WHITE;

    // --- UI Components ---
    private JPanel topPanel, centerPanel, bottomPanel;
    private JLabel titleLabel, createTournamentLabel, joinTournamentLabel, tournamentNameLabel, targetChipsLabel, messageLabel;
    private JTextField tournamentNameTextField, targetChipsTextField;
    private JButton createTournamentButton, joinTournamentButton, backToMenuButton;
    private JList<String> tournamentList;
    private DefaultListModel<String> tournamentListModel;
    private JScrollPane tournamentListScrollPane;
    private String currentUsername; // To pass username back to Main Menu

    // --- Constructor ---
    public TournamentLobbyGUI(String username) {
        this.currentUsername = username;
        initializeGUI();
        populateTournamentList(); // Fetch and display tournaments on start
    }

    private void initializeGUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(FELT_GREEN.darker());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        createTopPanel();
        createCenterPanel();
        createBottomPanel();

        this.add(topPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createTopPanel() {
        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(FELT_GREEN.darker());
        topPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, BORDER_COLOR, BORDER_COLOR.brighter()), new EmptyBorder(5, 10, 5, 10)));

        titleLabel = new JLabel("Tournament Lobby");
        titleLabel.setFont(MESSAGE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        topPanel.add(titleLabel);
    }

    private void createCenterPanel() {
        centerPanel = new JPanel(new GridBagLayout()); // Using GridBagLayout for better control
        centerPanel.setBackground(FELT_GREEN);
        centerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, BORDER_COLOR, BORDER_COLOR.brighter()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span two columns for section titles
        gbc.anchor = GridBagConstraints.CENTER;

        createTournamentLabel = new JLabel("Create New Tournament");
        styleLabel(createTournamentLabel);
        centerPanel.add(createTournamentLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        tournamentNameLabel = new JLabel("Tournament Name:");
        styleLabel(tournamentNameLabel);
        centerPanel.add(tournamentNameLabel, gbc);

        gbc.gridx++;
        tournamentNameTextField = new JTextField(20);
        centerPanel.add(tournamentNameTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        targetChipsLabel = new JLabel("Target Chips:");
        styleLabel(targetChipsLabel);
        centerPanel.add(targetChipsLabel, gbc);

        gbc.gridx++;
        targetChipsTextField = new JTextField(10);
        centerPanel.add(targetChipsTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        createTournamentButton = createStyledButton("Create Tournament");
        createTournamentButton.addActionListener(e -> createTournamentAction());
        centerPanel.add(createTournamentButton, gbc);

        gbc.gridy++;
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.YELLOW); // Example message color
        centerPanel.add(messageLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        joinTournamentLabel = new JLabel("Join Existing Tournament");
        styleLabel(joinTournamentLabel);
        centerPanel.add(joinTournamentLabel, gbc);

        gbc.gridy++;
        tournamentListModel = new DefaultListModel<>();
        tournamentList = new JList<>(tournamentListModel);
        tournamentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tournamentList.setBackground(BUTTON_COLOR); // Style like buttons for list background
        tournamentListScrollPane = new JScrollPane(tournamentList);
        tournamentListScrollPane.setPreferredSize(new Dimension(300, 150)); // Adjust size as needed
        centerPanel.add(tournamentListScrollPane, gbc);

        gbc.gridy++;
        joinTournamentButton = createStyledButton("Join Tournament");
        joinTournamentButton.addActionListener(e -> joinTournamentAction());
        centerPanel.add(joinTournamentButton, gbc);
    }

    private void createBottomPanel() {
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);

        backToMenuButton = createStyledButton("Main Menu");
        backToMenuButton.addActionListener(e -> returnToMainMenu());
        bottomPanel.add(backToMenuButton);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 35)); // Slightly wider buttons for lobby
        return button;
    }

    private void styleLabel(JLabel label) {
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        label.setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void populateTournamentList() {
        tournamentListModel.clear();
        List<Tournament> tournaments = TournamentService.getAllActiveTournaments(); // Assuming this method exists
        if (tournaments != null && !tournaments.isEmpty()) {
            for (Tournament tournament : tournaments) {
                tournamentListModel.addElement(tournament.getName());
            }
        } else {
            tournamentListModel.addElement("No tournaments available.");
        }
    }

    private void createTournamentAction() {
        String tournamentName = tournamentNameTextField.getText();
        String targetChipsStr = targetChipsTextField.getText();

        if (tournamentName == null || tournamentName.trim().isEmpty()) {
            messageLabel.setText("Please enter a tournament name.");
            return;
        }

        int targetChips;
        try {
            targetChips = Integer.parseInt(targetChipsStr);
            if (targetChips <= 0) {
                messageLabel.setText("Target chips must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid target chips format.");
            return;
        }

        boolean success = TournamentService.createTournament(tournamentName, targetChips);
        if (success) {
            messageLabel.setText("Tournament '" + tournamentName + "' created successfully!");
            populateTournamentList(); // Refresh the list after creation
            tournamentNameTextField.setText(""); // Clear input fields
            targetChipsTextField.setText("");
        } else {
            messageLabel.setText("Failed to create tournament. Name may be taken.");
        }
    }

    private void joinTournamentAction() {
        String selectedTournamentName = tournamentList.getSelectedValue();
        if (selectedTournamentName == null || selectedTournamentName.equals("No tournaments available.")) {
            messageLabel.setText("Please select a tournament to join.");
            return;
        }

        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (currentFrame != null) {
            Main.startGame(currentUsername, selectedTournamentName, currentFrame); // Assuming startGame takes tournament name
        } else {
            System.err.println("Error: Could not find parent frame for TournamentLobbyGUI.");
        }
    }


    private void returnToMainMenu() {
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (currentFrame != null) {
            Main.returnToMainMenu(currentUsername, currentFrame);
        } else {
            System.err.println("Error: Could not find parent frame for TournamentLobbyGUI.");
        }
    }
}