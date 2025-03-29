package com.blackjack.Models;
import com.blackjack.Services.UserService;
import com.blackjack.Models.User;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.text.TableView;
import java.awt.*;
import java.util.List;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;



public class Leaderboard extends JPanel {

    private JFrame frameLB;
    private JTable table;

    public Leaderboard(){

        initLeaderboard();

    }

    public void initLeaderboard(){

        frameLB = new JFrame();
        frameLB.setTitle("Blackjack Leaderboard");
        frameLB.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameLB.setSize(600, 400);
        frameLB.setLocationRelativeTo(null);

        String[] columnNames = {"Rank", "Username", "Chips", "Wins", "Losses", "Pushes"};
        //fetching data
        List<User> sortedUsers = UserService.getAllUsers();
        sortedUsers.sort(Comparator.comparingInt(User::getChips).reversed());

        String[][] data = new String[Math.min(sortedUsers.size(),10)][6];


        table = new JTable(data, columnNames);
        table.setBounds(30, 40, 200, 300);
        JScrollPane sp = new JScrollPane(table);
        frameLB.add(sp);
        frameLB.setSize(500, 300);
        frameLB.setVisible(true);

    }

    //To-do: Implement this function that will every users chip count after each new entry.
    public void sortByChips() {



    }

}
