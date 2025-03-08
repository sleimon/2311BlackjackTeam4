package com.blackjack;

import com.blackjack.Models.Game;
import com.blackjack.Models.User;
import com.blackjack.Services.UserService;
import com.blackjack.stubdatabase.StubDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    public static boolean useStubDatabase = false; // Toggle state

    public static void main(String[] args) {
        // Create a panel for the login/sign-up form
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2)); // Extra row for toggle switch

        // Username and password fields
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        // Login and Sign Up buttons
        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign Up");

        // Toggle switch for database selection
        JToggleButton toggleDatabaseButton = new JToggleButton("Use Stub Database (OFF)");

        // Add components to panel
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(signUpButton);
        panel.add(new JLabel("Database Mode:"));
        panel.add(toggleDatabaseButton);

        // Create the frame and add the panel
        JFrame frame = new JFrame("BlackJack Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.add(panel);
        frame.setVisible(true);

        // Toggle button action listener
        toggleDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                useStubDatabase = toggleDatabaseButton.isSelected();
                toggleDatabaseButton.setText(useStubDatabase ? "Use Stub Database (ON)" : "Use Stub Database (OFF)");
            }
        });

        // Login button action listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both username and password.");
                    return;
                }

                // Get user based on the toggle state
                User user = useStubDatabase ? StubDatabase.getUser(username) : UserService.getUser(username);

                if (user != null && UserService.validatePassword(username, password)) {
                    System.out.println("User logged in: " + username);
                    startGame(username);
                    frame.dispose(); // Close the login window
                } else {
                    JOptionPane.showMessageDialog(frame, "Incorrect username or password.");
                }
            }
        });

        // Sign-up button action listener
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both username and password.");
                    return;
                }

                // Check if user already exists in the selected database
                User existingUser = useStubDatabase ? StubDatabase.getUser(username) : UserService.getUser(username);

                if (existingUser != null) {
                    JOptionPane.showMessageDialog(frame, "Username already exists. Please choose a different username.");
                } else {
                    // Create new user and add to the selected database
                    User newUser = new User(username, password, 1000, 0, 0, 0); // 1000 chips by default

                    if (useStubDatabase) {
                        StubDatabase.addUser(newUser);
                    } else {
                        UserService.addUser(newUser);
                    }

                    JOptionPane.showMessageDialog(frame, "User created successfully.");
                    startGame(username);
                    frame.dispose(); // Close the sign-up window
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
