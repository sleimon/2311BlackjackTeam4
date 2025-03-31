package com.blackjack.GUI; // Put GUI classes in their own package

import com.blackjack.Main; // To call Main.startMainMenu and access useStubDatabase
import com.blackjack.Services.LoginService; // Depends on LoginService

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LoginGUI {

    private JFrame loginFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JToggleButton toggleDatabaseButton;
    private JButton loginButton;
    private JButton signUpButton;

    private final LoginService loginService; // Reference to the logic service

    public LoginGUI(LoginService service) {
        if (service == null) {
            throw new IllegalArgumentException("LoginService cannot be null");
        }
        this.loginService = service;
        initialize(); // Build the GUI
    }

    /**
     * Creates and configures the Swing components for the login window.
     */
    private void initialize() {
        loginFrame = new JFrame("BlackJack Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 200); // Initial size, pack later
        loginFrame.setLocationRelativeTo(null); // Center

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label and Field
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Password Label and Field
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1;
        loginButton = new JButton("Login");
        panel.add(loginButton, gbc);

        // Sign Up Button
        gbc.gridx = 2; gbc.gridy = 2; gbc.gridwidth = 1;
        signUpButton = new JButton("Sign Up");
        panel.add(signUpButton, gbc);

        // Database Toggle Button
        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Database:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        toggleDatabaseButton = new JToggleButton("Use Real Database");
        toggleDatabaseButton.setSelected(!Main.useStubDatabase); // Reflect state from Main
        panel.add(toggleDatabaseButton, gbc);

        loginFrame.add(panel);

        // Add Action Listeners
        setupActionListeners();

        loginFrame.pack(); // Size window to fit contents
    }

    /**
     * Sets up the action listeners for the buttons.
     */
    private void setupActionListeners() {
        // Database Toggle Listener
        toggleDatabaseButton.addActionListener(e -> {
            // Update the static flag in Main directly
            Main.useStubDatabase = !toggleDatabaseButton.isSelected();
            toggleDatabaseButton.setText(Main.useStubDatabase ? "Use Stub Database" : "Use Real Database");
            System.out.println("[LoginGUI] Database mode set to: " + (Main.useStubDatabase ? "Stub" : "Real"));
        });

        // Login Button Listener
        loginButton.addActionListener(e -> handleLogin());

        // Sign Up Button Listener
        signUpButton.addActionListener(e -> handleSignup());
    }

    /**
     * Handles the login button click event.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars).trim();

        // Optional: Add byte logging if still needed for extreme debugging
        // byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        // System.out.println("[LoginGUI] Login Bytes: " + bytesToHex(passwordBytes));

        // Clear password from memory
        Arrays.fill(passwordChars, '\0');

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame, "Please enter both username and password.", "Input Error", JOptionPane.WARNING_MESSAGE);
            passwordField.setText(""); // Clear field
            return;
        }

        // Call the LoginService to attempt login
        boolean loginSuccess = loginService.attemptLogin(username, password);

        if (loginSuccess) {
            System.out.println("[LoginGUI] Login successful for: " + username);
            // Navigate to Main Menu - Call the static method in Main
            Main.startMainMenu(username, loginFrame); // Pass own frame to be disposed
        } else {
            System.out.println("[LoginGUI] Login failed for: " + username);
            JOptionPane.showMessageDialog(loginFrame, "Incorrect username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText(""); // Clear field
        }
    }

    /**
     * Handles the sign up button click event.
     */
    private void handleSignup() {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars).trim();

        // Optional: Add byte logging if still needed for extreme debugging
        // byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        // System.out.println("[LoginGUI] Signup Bytes: " + bytesToHex(passwordBytes));

        // Clear password from memory
        Arrays.fill(passwordChars, '\0');

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame, "Please enter both username and password.", "Input Error", JOptionPane.WARNING_MESSAGE);
            passwordField.setText(""); // Clear field
            return;
        }

        // Call the LoginService to attempt signup
        boolean signupSuccess = loginService.attemptSignUp(username, password);

        if (signupSuccess) {
            System.out.println("[LoginGUI] Signup successful for: " + username);
            JOptionPane.showMessageDialog(loginFrame, "User '" + username + "' created successfully. Please log in.", "Sign Up Success", JOptionPane.INFORMATION_MESSAGE);
            usernameField.setText(""); // Clear fields for login
            passwordField.setText("");
        } else {
            System.out.println("[LoginGUI] Signup failed for: " + username);
            // Specific error (like user exists) should be handled by service logic,
            // but GUI shows a generic message unless service provides more detail.
            // In this setup, LoginService doesn't return error type, so check logs.
            JOptionPane.showMessageDialog(loginFrame, "Signup failed. Username might already exist or a database error occurred.", "Sign Up Failed", JOptionPane.WARNING_MESSAGE);
            passwordField.setText(""); // Clear field
        }
    }

    /**
     * Makes the login window visible. Call this after creating the LoginGUI instance.
     */
    public void display() {
        if (loginFrame != null) {
            loginFrame.setVisible(true);
        } else {
            System.err.println("LoginGUI Error: Attempted to display before GUI initialization.");
        }
    }

    // Helper method (if you keep byte logging)
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}