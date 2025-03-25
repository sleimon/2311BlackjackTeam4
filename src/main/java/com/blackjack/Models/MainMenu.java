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

       // Create the buttons
       playGame = new JButton("Play Blackjack");
       leaderboard = new JButton("Leaderboard");
       quit = new JButton("Quit");

       // Set the width and height of each button
       int buttonWidth = 150;
       int buttonHeight = 30;

       // Center the buttons horizontally by calculating the X position
       int xPosition = (windowMainMenu.getWidth() - buttonWidth) / 2;

       // Set the positions of the buttons (you can adjust the y-position as needed)
       playGame.setBounds(xPosition, 300, buttonWidth, buttonHeight); // (centered, y = 150)
       leaderboard.setBounds(xPosition, 350, buttonWidth, buttonHeight); // (centered, y = 200)
       quit.setBounds(xPosition, 400, buttonWidth, buttonHeight); // (centered, y = 250)

       // Add buttons to the panel
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
        ImageIcon ace = new ImageIcon(new ImageIcon(IMAGE_DIR + "AceSpades.png").getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
        ImageIcon king = new ImageIcon(new ImageIcon(IMAGE_DIR + "KingHearts.png").getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));


        // Create JLabel for each card with the respective image
        JLabel aceLabel = new JLabel(ace);
        JLabel kingLabel = new JLabel(king);

        // Set the bounds for positioning the cards
        // For example, place them centered horizontally and spaced vertically
        int xPosition = (windowMainMenu.getWidth() - CARD_WIDTH - CARD_WIDTH - 20) / 2; // Calculate position for both cards

        // Set positions of card images
        aceLabel.setBounds(xPosition, 100, CARD_WIDTH, CARD_HEIGHT);
        kingLabel.setBounds(xPosition + CARD_WIDTH + 10, 100, CARD_WIDTH, CARD_HEIGHT); // Place card 2 next to card 1

        // Add the cards to the panel
        panel.add(aceLabel);
        panel.add(kingLabel);
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
