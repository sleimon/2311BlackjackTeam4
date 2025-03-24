package com.blackjack.Models;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel{

    private JLabel Blackjack;
    private JButton PlayGame;
    private JButton LeaderBoard;
    private JButton Quit;

   public void setupMainMenuGUI() {

       this.setSize(800, 500);

       PlayGame = new JButton("Play Game");
       PlayGame.setBounds(10, 10, 100, 20);

       LeaderBoard = new JButton("Leaderboard");
       LeaderBoard.setBounds(120, 10, 100, 20);

       Quit = new JButton("Quit");
       Quit.setBounds(230, 10, 100, 20);

       PlayGame.setVisible(true);
       LeaderBoard.setVisible(true);
       Quit.setVisible(true);


   }

}
