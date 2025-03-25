package com.blackjack.Models;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel{

    private JFrame windowMainMenu;
    private JLabel Blackjack;
    private JButton PlayGame;
    private JButton LeaderBoard;
    private JButton Quit;
    private String username;

    public MainMenu(String username){

        this.username = username;
        initializeMainMenu();

    }

   public void initializeMainMenu() {

       windowMainMenu = new JFrame();
       windowMainMenu.setTitle("Blackjack Main Menu");
       windowMainMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       windowMainMenu.setSize(800, 500);
       windowMainMenu.setLocationRelativeTo(null);

       JPanel panelMainMenu = new JPanel();
       panelMainMenu.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

       PlayGame = new JButton("Play Blackjack");
       panelMainMenu.add(PlayGame);
       LeaderBoard = new JButton("Leaderboard");
       panelMainMenu.add(LeaderBoard);
       Quit = new JButton("Quit");
       panelMainMenu.add(Quit);

       panelMainMenu.setBackground(Color.GREEN);
       windowMainMenu.add(panelMainMenu, BorderLayout.CENTER);

       buttonPresses(PlayGame);
       buttonPresses(LeaderBoard);
       buttonPresses(Quit);

       windowMainMenu.setVisible(true);

   }

   public void buttonPresses(JButton button) {

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button == PlayGame){
                    System.out.println("The Play Blackjack button was pressed.  ");
                    windowMainMenu.dispose();
                    startGame(username);
                }
                else if(button == LeaderBoard){
                    System.out.println("The Leaderboard button was pressed.  ");
                }
                else {
                    System.exit(0);
                }
            }
        });
   }

    private static void startGame(String username) {
        // Create and show the game window
        Game game = new Game(username);
        JFrame gameFrame = new JFrame("BlackJack");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(800, 600);
        gameFrame.add(game);
        gameFrame.setVisible(true);
    }

}
