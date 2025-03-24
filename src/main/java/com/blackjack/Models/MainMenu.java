package com.blackjack.Models;

import javax.swing.*;

public class MainMenu extends JPanel{

    private JFrame windowMainMenu;
    private JLabel Blackjack;
    private JButton PlayGame;
    private JButton LeaderBoard;
    private JButton Quit;

    public MainMenu(){

        windowMainMenu = new JFrame();
        windowMainMenu.setTitle("Blackjack Main Menu");
        windowMainMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        windowMainMenu.setSize(800, 500);
        windowMainMenu.setLocationRelativeTo(null);

    }

   public void showMainMenu() {

       windowMainMenu.setVisible(true);


   }

}
