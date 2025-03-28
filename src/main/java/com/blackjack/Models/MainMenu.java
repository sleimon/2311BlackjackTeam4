package com.blackjack.Models;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel{

    private JFrame windowMainMenu;
    private JButton playGame;
    private JButton leaderboard;
    private JButton quit;
    private String username;

    public static final int CARD_WIDTH = 100;
    public static final int CARD_HEIGHT = 145;
    public static final String IMAGE_DIR = "target/classes/cards/";

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
       panelMainMenu.setLayout(null); // This allows us to set the position manually

       playGame = new JButton("Play Blackjack");
       leaderboard = new JButton("Leaderboard");
       quit = new JButton("Quit");

       int buttonWidth = 150;
       int buttonHeight = 30;

       // Center the buttons horizontally by calculating the X position
       int xPosition = (windowMainMenu.getWidth() - buttonWidth) / 2;

       playGame.setBounds(xPosition, 300, buttonWidth, buttonHeight);
       leaderboard.setBounds(xPosition, 350, buttonWidth, buttonHeight);
       quit.setBounds(xPosition, 400, buttonWidth, buttonHeight);

       panelMainMenu.add(playGame);
       panelMainMenu.add(leaderboard);
       panelMainMenu.add(quit);

       addCardImages(panelMainMenu);

       panelMainMenu.setBackground(Color.decode("#427643"));
       windowMainMenu.add(panelMainMenu, BorderLayout.CENTER);

       buttonPresses(playGame);
       buttonPresses(leaderboard);
       buttonPresses(quit);

       windowMainMenu.setVisible(true);
   }

    private void addCardImages(JPanel panel) {

        //Adding images this way so that the card size is reduced and fits into the main menu
        ImageIcon ace = new ImageIcon(new ImageIcon(IMAGE_DIR + "AceSpades.png").getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
        ImageIcon king = new ImageIcon(new ImageIcon(IMAGE_DIR + "KingHearts.png").getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));

        JLabel aceLabel = new JLabel(ace);
        JLabel kingLabel = new JLabel(king);

        //Using this equation to find a suitable x position based on the size of the window and the size of the card
        int xPosition = (windowMainMenu.getWidth() - CARD_WIDTH - CARD_WIDTH - 20) / 2;

        aceLabel.setBounds(xPosition, 100, CARD_WIDTH, CARD_HEIGHT);
        kingLabel.setBounds(xPosition + CARD_WIDTH + 10, 100, CARD_WIDTH, CARD_HEIGHT);

        panel.add(aceLabel);
        panel.add(kingLabel);
    }

   public void buttonPresses(JButton button) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(button == playGame){
                    windowMainMenu.dispose();
                    startGame(username);
                }
                else if(button == leaderboard){
                    //TODO: leaderboard
                }
                else { //button == quit
                    System.exit(0);
                }
            }
        });
   }

    private static void startGame(String username) {
        Game game = new Game(username);
        JFrame gameFrame = new JFrame("BlackJack");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(800, 600);
        gameFrame.add(game);
        gameFrame.setVisible(true);
    }
}