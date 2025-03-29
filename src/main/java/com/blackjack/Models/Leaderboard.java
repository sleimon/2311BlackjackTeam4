package com.blackjack.Models;
import com.blackjack.Services.UserService;
//import com.blackjack.Models.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;



public class Leaderboard extends JPanel {

    public Leaderboard(){

        initLeaderboard();

    }

    public void initLeaderboard(){

        JFrame frameLB = new JFrame();
        frameLB.setTitle("Blackjack Leaderboard");
        frameLB.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameLB.setSize(600, 400);
        frameLB.setLocationRelativeTo(null);

        String[] columnNames = {"Rank", "Username", "Chips", "Wins", "Losses", "Pushes"};
        //fetching data
        List<User> sortedUsers = UserService.getAllUsers();
        sortedUsers.sort(Comparator.comparingInt(User::getChips).reversed());

        String[][] data = new String[Math.min(sortedUsers.size(),10)][6];
        for(int i=0; i<data.length; i++){
            User user = sortedUsers.get(i);
            data[i][0]= String.valueOf(i+1);
            data[i][1]= user.getUsername();
            data[i][2]= String.valueOf(user.getChips());
            data[i][3]= String.valueOf(user.getWins());
            data[i][4]= String.valueOf(user.getLosses());
            data[i][5]= String.valueOf(user.getPushes());
        }

        JTable table = new JTable(new DefaultTableModel(data, columnNames));
        //table.setBounds(30, 40, 200, 300);
        JScrollPane sp = new JScrollPane(table);
        frameLB.add(sp, BorderLayout.CENTER);
        //frameLB.setSize(500, 300);
        frameLB.setVisible(true);

    }

}
