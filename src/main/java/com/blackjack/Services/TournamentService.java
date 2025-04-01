package com.blackjack.Services;

import com.blackjack.Models.Tournament;
import com.blackjack.Services.DbConnectService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentService {

    public static List<Tournament> getAllActiveTournaments() {
        List<Tournament> activeTournaments = new ArrayList<>();
        String query = "SELECT * FROM tournaments WHERE status = 'active'";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tournament tournament = new Tournament(
                        rs.getInt("tournament_id"),
                        rs.getString("name"),
                        rs.getInt("target_chips"),
                        rs.getInt("active_players"),
                        rs.getString("status"),
                        rs.getInt("won_by"), // Fetch the winner
                        rs.getTimestamp("created_at")
                );
                activeTournaments.add(tournament);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activeTournaments;
    }

    public static boolean createTournament(String name, int targetChips) {
        if (getTournamentFromName(name) != null) {
            return false; // Tournament name already exists
        }
        String query = "INSERT INTO tournaments (name, target_chips, active_players, status, created_at) VALUES (?, ?, ?, 'active', NOW())";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setInt(2, targetChips);
            stmt.setInt(3, 0); // Initial active players is 0
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void updateTournamentStatus(int tournamentId, String newStatus) {
        String query = "UPDATE tournaments SET status = ? WHERE tournament_id = ?";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, tournamentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
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
            e.printStackTrace();
        }
        return false; // Default to active if an error occurs
    }

    public static void setTournamentInactive(int tournamentId) {
        String query = "UPDATE tournaments SET status = 'inactive' WHERE tournament_id = ?";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tournamentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setTournamentWinner(int tournamentId, int userId) {
        String query = "UPDATE tournaments SET won_by = ? WHERE tournament_id = ?";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, tournamentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static Tournament getTournamentFromName(String tournamentName) {
        String query = "SELECT * FROM tournaments WHERE name = ?";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tournamentName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {  // Use if instead of while
                    return new Tournament(
                            rs.getInt("tournament_id"),
                            rs.getString("name"),
                            rs.getInt("target_chips"),
                            rs.getInt("active_players"),
                            rs.getString("status"),
                            rs.getInt("won_by"), // Ensure null safety in Tournament class
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching tournament: " + tournamentName, e);
        }
        return null; // Return null if no tournament is found
    }


    public static void updateTournamentWinner(int tournamentId, String winnerUsername) {
        String query = "UPDATE tournaments SET won_by = ?, status = 'inactive' WHERE tournament_id = ?";

        try (Connection conn = DbConnectService.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, winnerUsername);
            stmt.setInt(2, tournamentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}