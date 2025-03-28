package com.blackjack.Models;

import javax.swing.*;

public class Leaderboard extends JPanel {

    private JFrame frameLB;


    public Leaderboard(){

        initLeaderboard();

    }

    public void initLeaderboard(){

        frameLB = new JFrame();
        frameLB.setTitle("Blackjack Leaderboard");
        frameLB.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameLB.setSize(800, 600);
        frameLB.setLocationRelativeTo(null);



    }

}
