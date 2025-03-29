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
        frameLB.setSize(800, 600);
        frameLB.setLocationRelativeTo(null);

        String[][] data = {
                            {"1", "user123", "5000"},
                            {"2", "jeff554", "3201"}
                          };
        String[] columnNames = {"Rank", "Username", "Chip Count"};

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
