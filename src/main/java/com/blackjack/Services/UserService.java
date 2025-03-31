package com.blackjack.Services;

import com.blackjack.Models.User;
// Import Argon2 classes
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types; // Specify Argon2id

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets; // For converting password to bytes

public class UserService {

    // Argon2 instance - create once, reuse. Argon2id is recommended.
    // Adjust parameters as needed based on security requirements and server capability.
    private static final Argon2 ARGON2 = Argon2Factory.create(
            Argon2Types.ARGON2id, // Use Argon2id variant
            16, // Salt length in bytes
            32  // Hash length in bytes
    );

    // Hashing parameters - adjust based on performance/security needs
    private static final int ITERATIONS = 10;     // More iterations = more secure, but slower
    private static final int MEMORY_KiB = 65536; // 64 MB - More memory = more secure
    private static final int PARALLELISM = 1;    // Number of threads

    // --- Password Hashing & Verification (Argon2) ---

    /**
     * Hashes a plain text password using Argon2id.
     * @param plainPassword The password to hash. Cannot be null or empty.
     * @return The Argon2 encoded hash string.
     * @throws IllegalArgumentException if the password is null or empty.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        // Convert password to char array or byte array for hashing
        char[] passwordChars = plainPassword.toCharArray();
        try {
            // Hash method returns the full encoded hash string including salt, params, etc.
            System.out.println("[DEBUG hashPassword] Hashing password with Argon2..."); // Debug
            String hash = ARGON2.hash(ITERATIONS, MEMORY_KiB, PARALLELISM, passwordChars, StandardCharsets.UTF_8);
            System.out.println("[DEBUG hashPassword] Argon2 hash generated successfully."); // Debug
            return hash;
        } finally {
            // IMPORTANT: Wipe the password from memory after hashing
            ARGON2.wipeArray(passwordChars);
            System.out.println("[DEBUG hashPassword] Password char array wiped."); // Debug
        }
    }

    /**
     * Verifies a plain text password against a stored Argon2 hash.
     * @param plainPassword The plain text password attempt.
     * @param hashedPassword The encoded Argon2 hash string from the database.
     * @return true if the password matches the hash, false otherwise.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.isEmpty() || hashedPassword == null || hashedPassword.isEmpty()) {
            System.out.println("[DEBUG checkPassword] Check failed: Plain password or hash is null/empty."); // Debug
            return false;
        }
        char[] passwordChars = plainPassword.toCharArray();
        System.out.println("[DEBUG checkPassword] Verifying password against hash: '" + hashedPassword + "'"); // Debug
        try {
            // Verify method checks the password against the encoded hash string
            boolean result = ARGON2.verify(hashedPassword, passwordChars, StandardCharsets.UTF_8);
            System.out.println("[DEBUG checkPassword] Argon2 verify result: " + result); // Debug
            return result;
        } catch (Exception e) {
            System.err.println("[ERROR checkPassword] Error during Argon2 verification: " + e.getMessage());
            // Log the exception details if needed
            // e.printStackTrace(); // Uncomment for full stack trace if needed
            return false; // Treat verification errors as mismatch
        }
        finally {
            // IMPORTANT: Wipe the password from memory
            ARGON2.wipeArray(passwordChars);
            System.out.println("[DEBUG checkPassword] Password check char array wiped."); // Debug
        }
    }

    // --- User CRUD Operations ---

    /**
     * Retrieve a user by username. Returns the user object including the Argon2 hashed password.
     * @param username The username to search for.
     * @return User object if found, null otherwise.
     */
    public static User getUser(String username) {
        String query = "SELECT username, password, chips, wins, losses, pushes FROM Users WHERE username = ?";
        System.out.println("[DEBUG getUser] Attempting to fetch user: " + username); // DEBUG
        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String retrievedUsername = rs.getString("username");
                String retrievedHashedPassword = rs.getString("password"); // Get Argon2 hash from DB
                int retrievedChips = rs.getInt("chips");
                int retrievedWins = rs.getInt("wins");
                int retrievedLosses = rs.getInt("losses");
                int retrievedPushes = rs.getInt("pushes");

                System.out.println("[DEBUG getUser] User Found: " + retrievedUsername);
                System.out.println("[DEBUG getUser] HASH FROM ResultSet (Argon2): '" + retrievedHashedPassword + "'");
                System.out.println("[DEBUG getUser] Hash Length from ResultSet: " + (retrievedHashedPassword != null ? retrievedHashedPassword.length() : "null"));

                User user = new User(
                        retrievedUsername,
                        retrievedHashedPassword, // Pass the retrieved Argon2 hash
                        retrievedChips,
                        retrievedWins,
                        retrievedLosses,
                        retrievedPushes
                );
                System.out.println("[DEBUG getUser] User object created."); // DEBUG
                return user;
            } else {
                System.out.println("[DEBUG getUser] User NOT Found: " + username); // DEBUG
            }
        } catch (SQLException e) {
            System.err.println("[ERROR getUser] SQLException fetching user '" + username + "': " + e.getMessage());
        }
        return null;
    }

    /**
     * Adds a new user to the database after hashing their password using Argon2.
     * Checks for existing username first.
     * @param user User object containing PLAIN TEXT password initially.
     * @return true if the user was added successfully, false otherwise (e.g., duplicate, DB error, hashing error).
     */
    public static boolean addUser(User user) {
        // Check if user already exists before attempting insert
        if (getUser(user.getUsername()) != null) {
            System.err.println("Attempted to add duplicate user: " + user.getUsername());
            return false; // User already exists
        }

        String plainPasswordInput = user.getPassword();
        String hashedPassword;
        try {
            // Use Argon2 hashPassword method
            hashedPassword = hashPassword(plainPasswordInput);
        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR addUser] Error hashing password for " + user.getUsername() + ": " + e.getMessage());
            return false; // Return false if hashing fails (e.g., empty password)
        }
        // Debug log moved inside hashPassword for clarity

        String query = "INSERT INTO Users (username, password, chips, wins, losses, pushes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword); // Store the Argon2 HASHED password
            stmt.setInt(3, user.getChips());
            stmt.setInt(4, user.getWins());
            stmt.setInt(5, user.getLosses());
            stmt.setInt(6, user.getPushes());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("[DEBUG addUser] Rows affected by insert: " + rowsAffected); // DEBUG
            return rowsAffected > 0; // Return true if insert was successful

        } catch (SQLException e) {
            System.err.println("[ERROR addUser] SQLException adding user '" + user.getUsername() + "': " + e.getMessage());
            return false; // Indicate failure
        }
    }

    /**
     * Updates an existing user's game statistics (chips, wins, losses, pushes).
     * DOES NOT update the password here. Use a separate method for password changes.
     * @param user User object containing updated stats. Username identifies the user.
     * @return true if the update was successful, false otherwise.
     */
    public static boolean updateUser(User user) {
        // Note: We EXCLUDE the password from this general update for security.
        String query = "UPDATE Users SET chips = ?, wins = ?, losses = ?, pushes = ? WHERE username = ?";
        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, user.getChips());
            stmt.setInt(2, user.getWins());
            stmt.setInt(3, user.getLosses());
            stmt.setInt(4, user.getPushes());
            stmt.setString(5, user.getUsername()); // WHERE clause parameter

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("Warning: Attempted to update non-existent user: " + user.getUsername());
            }
            return rowsAffected > 0; // Return true if update affected at least one row

        } catch (SQLException e) {
            System.err.println("[ERROR updateUser] SQLException updating user '" + user.getUsername() + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates a plain text password attempt against the stored Argon2 hash for a user.
     * @param username The username attempting to log in.
     * @param plainPasswordAttempt The plain text password entered by the user.
     * @return true if the password is correct, false otherwise (or if user doesn't exist).
     */
    public static boolean validatePassword(String username, String plainPasswordAttempt) {
        System.out.println("\n--- [DEBUG validatePassword] Start Validation (Argon2) ---"); // DEBUG 구분선
        System.out.println("[DEBUG validatePassword] User attempting login: " + username);
        System.out.println("[DEBUG validatePassword] Plain password provided: '" + plainPasswordAttempt + "'");

        User user = getUser(username); // This now has internal logging

        if (user == null) {
            System.out.println("[DEBUG validatePassword] User object is NULL after getUser call."); // DEBUG
            System.out.println("--- [DEBUG validatePassword] End Validation (User Not Found) ---\n"); // DEBUG
            return false; // User not found
        }

        String hashFromUserObject = user.getPassword(); // Get Argon2 hash from the User object
        System.out.println("[DEBUG validatePassword] HASH retrieved from User Object (Argon2): '" + hashFromUserObject + "'");
        System.out.println("[DEBUG validatePassword] Hash length from User Object: " + (hashFromUserObject != null ? hashFromUserObject.length() : "null"));

        // Perform the check using the Argon2 checkPassword method
        boolean result = checkPassword(plainPasswordAttempt, hashFromUserObject);

        // Debug log for result is now inside checkPassword
        System.out.println("--- [DEBUG validatePassword] End Validation --- \n"); // DEBUG
        return result;
    }

    // --- Other Methods ---

    /**
     * Retrieve all users (primarily for debugging/admin).
     * @return A list of all User objects.
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT username, password, chips, wins, losses, pushes FROM Users"; // Explicit columns are slightly better
        try (Connection conn = DbConnectService.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("username"),
                        rs.getString("password"), // Argon2 hash
                        rs.getInt("chips"),
                        rs.getInt("wins"),
                        rs.getInt("losses"),
                        rs.getInt("pushes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[ERROR getAllUsers] Error fetching all users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Deletes a user from the database.
     * @param username The username of the user to delete.
     * @return true if the user was deleted successfully, false otherwise.
     */
    public static boolean deleteUser(String username) {
        String query = "DELETE FROM Users WHERE username = ?";
        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("[ERROR deleteUser] Error deleting user '" + username + "': " + e.getMessage());
            return false;
        }
    }
}