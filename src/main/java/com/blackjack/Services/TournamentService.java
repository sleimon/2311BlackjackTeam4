package com.blackjack.Services;

import com.blackjack.Models.Tournament;
// Assuming DbConnectService and UserService are in the same package or imported correctly
// import com.blackjack.Services.DbConnectService;
// import com.blackjack.Services.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentService {

    /**
     * Fetches all tournaments currently marked as 'active'.
     * @return A list of active Tournament objects.
     */
    public static List<Tournament> getAllActiveTournaments() {
        List<Tournament> activeTournaments = new ArrayList<>();
        String query = "SELECT * FROM tournaments WHERE status = 'active'";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tournament tournament = mapResultSetToTournament(rs);
                activeTournaments.add(tournament);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching active tournaments: " + e.getMessage());
            // Consider logging the stack trace instead of printing directly in a service
            // e.printStackTrace();
        }
        return activeTournaments;
    }

    /**
     * Creates a new tournament if the name doesn't already exist.
     * @param name The desired name for the tournament.
     * @param targetChips The number of chips required to win.
     * @return true if the tournament was created successfully, false otherwise (e.g., name exists, DB error).
     */
    public static boolean createTournament(String name, int targetChips) {
        if (getTournamentFromName(name) != null) {
            System.err.println("Tournament creation failed: Name '" + name + "' already exists.");
            return false; // Tournament name already exists
        }
        String query = "INSERT INTO tournaments (name, target_chips, active_players, status, won_by, created_at) VALUES (?, ?, ?, 'active', NULL, NOW())";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setInt(2, targetChips);
            stmt.setInt(3, 0); // Initial active players is 0
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error creating tournament '" + name + "': " + e.getMessage());
            // e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the status of a specific tournament.
     * @param tournamentId The ID of the tournament to update.
     * @param newStatus The new status ('active', 'inactive', etc.).
     * @return true if the update was successful, false otherwise.
     */
    public static boolean updateTournamentStatus(int tournamentId, String newStatus) {
        String query = "UPDATE tournaments SET status = ? WHERE tournament_id = ?";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, tournamentId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating status for tournament ID " + tournamentId + ": " + e.getMessage());
            // e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a specific tournament is currently marked as 'inactive'.
     * @param tournamentId The ID of the tournament to check.
     * @return true if the tournament status is 'inactive', false otherwise (including errors or not found).
     */
    public static boolean isTournamentInactive(int tournamentId) {
        String query = "SELECT status FROM tournaments WHERE tournament_id = ?";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tournamentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return "inactive".equalsIgnoreCase(rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking status for tournament ID " + tournamentId + ": " + e.getMessage());
            // e.printStackTrace();
        }
        // If not found or error, assume it's not inactive for safety
        return false;
    }

    /**
     * Sets the winner of a tournament and marks it as 'inactive'.
     * This attempts to prevent setting a winner if one is already set (won_by IS NULL check).
     * @param tournamentId The ID of the tournament.
     * @param userId The ID of the winning user.
     * @return true if the winner was successfully set (row updated), false otherwise (e.g., already won, DB error).
     */
    public static boolean setTournamentWinner(int tournamentId, int userId) {
        // Update only if no winner is set yet to prevent race conditions overwriting winner
        String query = "UPDATE tournaments SET won_by = ?, status = 'inactive' WHERE tournament_id = ? AND won_by IS NULL";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, tournamentId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Tournament " + tournamentId + " winner set to user " + userId + " and status inactive.");
                return true;
            } else {
                System.out.println("Tournament " + tournamentId + " winner NOT set (possibly already won or ID not found).");
                return false; // No rows updated, maybe already won?
            }
        } catch (SQLException e) {
            System.err.println("Error setting winner for tournament ID " + tournamentId + ": " + e.getMessage());
            // e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a specific tournament by its unique name.
     * @param tournamentName The name of the tournament.
     * @return The Tournament object if found, null otherwise.
     */
    public static Tournament getTournamentFromName(String tournamentName) {
        String query = "SELECT * FROM tournaments WHERE name = ?";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tournamentName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTournament(rs);
                }
            }
        } catch (SQLException e) {
            // Instead of throwing RuntimeException, log and return null for robustness
            System.err.println("Error fetching tournament by name '" + tournamentName + "': " + e.getMessage());
            // e.printStackTrace();
            // Consider throwing a custom checked exception if callers should handle this
            // throw new TournamentAccessException("Error fetching tournament: " + tournamentName, e);
        }
        return null; // Return null if no tournament is found or an error occurs
    }

    /**
     * Helper method to map a ResultSet row to a Tournament object.
     * @param rs The ResultSet, positioned at the row to map.
     * @return A Tournament object.
     * @throws SQLException If a database access error occurs.
     */
    private static Tournament mapResultSetToTournament(ResultSet rs) throws SQLException {
        // Handle potential null for won_by (use getInt which returns 0 if SQL NULL, or check wasNull)
        int wonById = rs.getInt("won_by");
        if (rs.wasNull()) {
            wonById = 0; // Or use Integer wrapper and set to null if Tournament class supports it
        }

        return new Tournament(
                rs.getInt("tournament_id"),
                rs.getString("name"),
                rs.getInt("target_chips"),
                rs.getInt("active_players"),
                rs.getString("status"),
                wonById, // Use the potentially zero value
                rs.getTimestamp("created_at")
        );
    }
}