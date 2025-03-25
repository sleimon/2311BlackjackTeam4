package com.blackjack.Models;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel{

    private JFrame windowMainMenu;
    private JLabel Blackjack;
    private JButton playGame;
    private JButton leaderboard;
    private JButton quit;
    private String username;
    private JLabel king;
    private JLabel ace;

    public MainMenu(String username){

        this.username = username;
        initializeMainMenu();

    }

   public void initializeMainMenu() {

       windowMainMenu = new JFrame();
       windowMainMenu.setTitle("Blackjack Main Menu");
       windowMainMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       windowMainMenu.setSize(800, 600);
       windowMainMenu.setLocationRelativeTo(null);

       JPanel panelMainMenu = new JPanel();
       //panelMainMenu.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

       playGame = new JButton("Play Blackjack");
       playGame.setBounds(400, 10, 100, 20);
       panelMainMenu.add(playGame);
       leaderboard = new JButton("Leaderboard");
       panelMainMenu.add(leaderboard);
       quit = new JButton("Quit");
       panelMainMenu.add(quit);

       panelMainMenu.setBackground(Color.decode("#427643"));
       windowMainMenu.add(panelMainMenu, BorderLayout.CENTER);

       buttonPresses(playGame);
       buttonPresses(leaderboard);
       buttonPresses(quit);

       windowMainMenu.setVisible(true);

   }

   public void buttonPresses(JButton button) {

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button == playGame){
                    System.out.println("The Play Blackjack button was pressed.  ");
                    windowMainMenu.dispose();
                    startGame(username);
                }
                else if(button == leaderboard){
                    System.out.println("The leaderboard button was pressed.  ");
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
