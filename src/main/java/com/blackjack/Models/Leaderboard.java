package com.blackjack.Models;
import com.blackjack.Services.UserService;
import com.blackjack.Models.User;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

public class Leaderboard extends JPanel {

    public Leaderboard() {
        initLeaderboard();
    }
    public void initLeaderboard() {
        try {
            List<User> sortedUsers = UserService.getAllUsers();
            sortedUsers.sort(Comparator.comparingInt(User::getChips).reversed());

            if (sortedUsers.isEmpty()) {
                showMessage("No users available", "Leaderboard");
                return;
            }
            JFrame frame = createLeaderboardFrame();
            JTable table = createLeaderboardTable(sortedUsers);
            frame.add(new JScrollPane(table), BorderLayout.CENTER);
            frame.setVisible(true);
        } catch (Exception e) {
            showMessage("Error loading the leaderboard: " + e.getMessage(), "Error");
        }
    }

    private JFrame createLeaderboardFrame() {
        JFrame frame = new JFrame("\uD83C\uDFC6 Blackjack Leaderboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.decode("#427643"));
        return frame;
    }

    private JTable createLeaderboardTable(List<User> users) {
        String[] columns = {"Rank", "Username", "Chips", "Wins", "Losses", "Pushes"};
        Object[][] data = new Object[users.size()][columns.length];
        for (int i = 0; i < data.length; i++) {
            User user = users.get(i);
            data[i][0] = String.valueOf(i + 1);
            data[i][1] = user.getUsername();
            data[i][2] = String.valueOf(user.getChips());
            data[i][3] = String.valueOf(user.getWins());
            data[i][4] = String.valueOf(user.getLosses());
            data[i][5] = String.valueOf(user.getPushes());
        };
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        stylingTable(table);
        return table;
    }

    private void stylingTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(Color.decode("#2A4E2A"));
        header.setForeground(Color.WHITE);
    }

    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(null,message,title,JOptionPane.INFORMATION_MESSAGE);
    }
}