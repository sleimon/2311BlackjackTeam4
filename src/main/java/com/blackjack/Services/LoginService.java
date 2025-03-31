package com.blackjack.Services; // Or a more specific package like com.blackjack.Auth

import com.blackjack.Main; // To access useStubDatabase flag (can be passed instead)
import com.blackjack.Models.User;
import com.blackjack.stubdatabase.StubDatabase;
import javax.swing.JOptionPane; // Keep for error messages originated from logic issues

public class LoginService {

    /**
     * Attempts to log in a user with the given credentials.
     *
     * @param username The username entered by the user.
     * @param password The plain text password entered by the user.
     * @return true if login is successful, false otherwise.
     */
    public boolean attemptLogin(String username, String password) {
        System.out.println("[LoginService] Attempting login for: " + username);
        User user;
        boolean passwordMatches;

        if (Main.useStubDatabase) {
            user = StubDatabase.getUser(username);
            // Simple plain text comparison for stub
            passwordMatches = (user != null && user.getPassword().equals(password));
            System.out.println("[LoginService] Stub DB check. User found: " + (user != null) + ", Password match: " + passwordMatches);
        } else {
            // Real DB uses UserService validation (which uses Argon2 checkPassword)
            passwordMatches = UserService.validatePassword(username, password);
            System.out.println("[LoginService] Real DB check. Password validation result: " + passwordMatches);
        }

        return passwordMatches; // Return true only if user exists and password is valid
    }

    /**
     * Attempts to sign up a new user.
     * Handles checking for existing users and adding the new user to the appropriate database.
     *
     * @param username The desired username.
     * @param password The desired plain text password.
     * @return true if signup was successful, false otherwise (e.g., user exists, DB error).
     */
    public boolean attemptSignUp(String username, String password) {
        System.out.println("[LoginService] Attempting signup for: " + username);
        // 1. Check if user already exists
        User existingUser = Main.useStubDatabase ? StubDatabase.getUser(username) : UserService.getUser(username);
        if (existingUser != null) {
            System.out.println("[LoginService] Signup failed: Username already exists.");
            // Error message handled by GUI
            return false;
        }

        // 2. Create user object (with plain password initially for addUser)
        // UserService.addUser will hash it if using the real DB.
        User newUser = new User(username, password, 1000, 0, 0, 0); // Default values

        // 3. Add user to the selected database
        boolean added;
        if (Main.useStubDatabase) {
            System.out.println("[LoginService] Adding user to Stub DB.");
            added = StubDatabase.addUser(newUser); // Stub likely stores plain text
        } else {
            System.out.println("[LoginService] Adding user to Real DB (will hash password).");
            // UserService.addUser receives the user object with the clean plain password
            // and hashes it internally using Argon2.
            added = UserService.addUser(newUser);
        }

        // 4. Check if adding was successful
        if (!added) {
            System.err.println("[LoginService] Signup failed: Could not save user to database.");
            // Show a generic error from GUI, but log specific error here
            JOptionPane.showMessageDialog(null, "Failed to save new user. Check logs.", "Sign Up Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        System.out.println("[LoginService] Signup successful for: " + username);
        return true;
    }
}