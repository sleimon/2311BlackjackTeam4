package com.blackjack.GUI;

import com.blackjack.Models.User;
import com.blackjack.Services.UserService;
import com.blackjack.Main; // To check DB mode if needed
import com.blackjack.stubdatabase.StubDatabase; // For stub mode

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList; // Import ArrayList
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Manages its own JFrame
public class LeaderboardGUI {

    private JFrame frameLB; // Keep reference to the frame

    // --- Theming Constants ---
    private static final Color FELT_GREEN = new Color(0, 85, 30);
    private static final Color DARK_WOOD_BORDER_BG = new Color(35, 25, 10);
    private static final Color BORDER_LINE_COLOR = new Color(80, 60, 30);
    private static final Color HEADER_BG = new Color(20, 40, 15);
    private static final Color TABLE_GRID_COLOR = new Color(0, 110, 40);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color GOLD_TEXT_COLOR = new Color(218, 165, 32);
    private static final Font TITLE_FONT = new Font("Georgia", Font.BOLD, 26);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font RANK_FONT = new Font("Segoe UI", Font.BOLD, 14);
    // Button styling
    private static final Color BUTTON_BG = new Color(75, 75, 75);
    private static final Color BUTTON_HOVER_BG = new Color(95, 95, 95);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);


    /**
     * Constructor - Immediately initializes and shows the leaderboard window.
     */
    public LeaderboardGUI() {
        SwingUtilities.invokeLater(this::initLeaderboard);
    }

    /**
     * Initializes and displays the Leaderboard JFrame.
     */
    public void initLeaderboard() {
        frameLB = new JFrame(); // Assign to field
        frameLB.setTitle("Blackjack Leaderboard");
        frameLB.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose only this window
        frameLB.setMinimumSize(new Dimension(650, 450));
        frameLB.setLocationRelativeTo(null);
        frameLB.getContentPane().setBackground(DARK_WOOD_BORDER_BG);

        // Main content panel (the "felt")
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(FELT_GREEN);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LINE_COLOR, 2),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("Leaderboard - Top Players", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT); titleLabel.setForeground(GOLD_TEXT_COLOR); titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Table Setup ---
        String[] columnNames = {"Rank", "Username", "Chips", "Wins", "Losses", "Pushes"};
        List<User> sortedUsers = fetchAndSortUsers(); // Fetch data
        // Create model, prevent editing
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        // Populate model (Top 10)
        int rank = 1;
        for (User user : sortedUsers) {
            if (rank > 10) break;
            tableModel.addRow(new Object[]{ rank, user.getUsername(), user.getChips(), user.getWins(), user.getLosses(), user.getPushes() });
            rank++;
        }
        // Create and style table
        JTable table = new JTable(tableModel);
        styleTable(table);
        // Create and style scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        styleScrollPane(scrollPane);
        contentPanel.add(scrollPane, BorderLayout.CENTER); // Add table to center

        // --- Close Button ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false); // Transparent background
        JButton closeButton = createStyledButton("Close"); // Use styling helper
        closeButton.setPreferredSize(new Dimension(100, 35)); // Smaller button
        // Action: Dispose this JFrame when clicked
        closeButton.addActionListener(e -> frameLB.dispose());
        bottomPanel.add(closeButton);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH); // Add button panel to bottom

        // Finalize frame
        frameLB.setContentPane(contentPanel);
        frameLB.pack();
        frameLB.setLocationRelativeTo(null);
        frameLB.setVisible(true);
    }

    /**
     * Fetches user data based on the database mode and sorts by chips descending.
     * @return Sorted List of User objects.
     */
    private List<User> fetchAndSortUsers() {
        List<User> users = null;
        try { // Add try-catch for database operations
            if (Main.useStubDatabase) {
               //not stub database feature
            } else {
                users = UserService.getAllUsers(); // Fetch from real DB
            }

            if (users != null) {
                // Sort by chips descending
                users.sort(Comparator.comparingInt(User::getChips).reversed());
            } else {
                users = Collections.emptyList(); // Use empty list if fetch failed
            }
        } catch (Exception e) {
            System.err.println("Error fetching or sorting user data for leaderboard: " + e.getMessage());
            e.printStackTrace();
            users = Collections.emptyList(); // Return empty on error
        }
        return users;
    }

    /**
     * Applies Blackjack-themed styling to the JTable component.
     * @param table The JTable to style.
     */
    private void styleTable(JTable table) {
        table.setBackground(FELT_GREEN); table.setForeground(TEXT_COLOR);
        table.setFont(TABLE_FONT); table.setGridColor(TABLE_GRID_COLOR);
        table.setRowHeight(28); table.setFillsViewportHeight(true);
        table.setShowVerticalLines(true); table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        // Header Styling
        JTableHeader header = table.getTableHeader(); header.setBackground(HEADER_BG);
        header.setForeground(GOLD_TEXT_COLOR); header.setFont(HEADER_FONT);
        header.setPreferredSize(new Dimension(100, 35));
        header.setBorder(BorderFactory.createLineBorder(BORDER_LINE_COLOR));
        header.setReorderingAllowed(false);
        // Column Alignment and Width
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); centerRenderer.setHorizontalAlignment(SwingConstants.CENTER); centerRenderer.setBackground(FELT_GREEN); centerRenderer.setForeground(TEXT_COLOR);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer(); leftRenderer.setHorizontalAlignment(SwingConstants.LEFT); leftRenderer.setBackground(FELT_GREEN); leftRenderer.setForeground(TEXT_COLOR);
        TableColumnModel columnModel = table.getColumnModel();
        // Rank (Column 0) - Special styling
        columnModel.getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component cell = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                cell.setFont(RANK_FONT); cell.setForeground(GOLD_TEXT_COLOR); setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) cell.setBackground(FELT_GREEN); else cell.setBackground(FELT_GREEN.brighter()); return cell;
            }
        });
        columnModel.getColumn(0).setPreferredWidth(50); columnModel.getColumn(0).setMaxWidth(70);
        // Username (Column 1) - Left
        columnModel.getColumn(1).setCellRenderer(leftRenderer); columnModel.getColumn(1).setPreferredWidth(150);
        // Numerical Columns (2-5) - Center
        for (int i = 2; i <= 5; i++) { columnModel.getColumn(i).setCellRenderer(centerRenderer); columnModel.getColumn(i).setPreferredWidth(80); }
        // Selection Styling
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(FELT_GREEN.brighter().brighter());
        table.setSelectionForeground(Color.BLACK);
    }

    /**
     * Applies styling to the JScrollPane containing the table.
     * @param scrollPane The JScrollPane to style.
     */
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.getViewport().setBackground(FELT_GREEN); // Viewport background
        scrollPane.setBackground(FELT_GREEN); // Scroll pane background
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LINE_COLOR, 1)); // Border
    }

    /**
     * Helper method to create themed buttons (copied from MainMenuGUI for consistency).
     * @param text The text for the button.
     * @return A styled JButton.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT); button.setBackground(BUTTON_BG); button.setForeground(TEXT_COLOR);
        // Use a simpler border for the close button maybe
        button.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(BORDER_LINE_COLOR), new EmptyBorder(5, 15, 5, 15) ));
        button.setFocusPainted(false); button.setOpaque(true); button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { button.setBackground(BUTTON_HOVER_BG); }
            public void mouseExited(java.awt.event.MouseEvent evt) { button.setBackground(BUTTON_BG); }
        });
        return button;
    }
} // End of LeaderboardGUI class