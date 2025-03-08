package services;
import Classes.User;
import Classes.Main;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserService implements DatabaseService {

    // Retrieve a user by username
    public  User getUser(String username) {
        String query = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DbConnectService.connect(); // Use DbConnectService's connect method
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("chips"),
                        rs.getInt("wins"),
                        rs.getInt("losses"),
                        rs.getInt("pushes")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    // Add a new user to the database (if not already existing)
    public void addUser(User user) {
        if (getUser(user.getUsername()) == null) { // Prevent duplicates
            String query = "INSERT INTO Users (username, password, chips, wins, losses, pushes) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DbConnectService.connect(); // Use DbConnectService's connect method
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());
                stmt.setInt(3, user.getChips());
                stmt.setInt(4, user.getWins());
                stmt.setInt(5, user.getLosses());
                stmt.setInt(6, user.getPushes());

                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error adding user: " + e.getMessage());
            }
        }
    }

    // Update an existing user's data
    public void updateUser(User user) {
        String query = "UPDATE Users SET password = ?, chips = ?, wins = ?, losses = ?, pushes = ? WHERE username = ?";
        try (Connection conn = DbConnectService.connect(); // Use DbConnectService's connect method
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getPassword());
            stmt.setInt(2, user.getChips());
            stmt.setInt(3, user.getWins());
            stmt.setInt(4, user.getLosses());
            stmt.setInt(5, user.getPushes());
            stmt.setString(6, user.getUsername());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    // Validate password for the given username
    public boolean validatePassword(String username, String password) {
        User user = getUser(username);
        return user != null && user.getPassword().equals(password);
    }

    // Retrieve all users (for debugging purposes)
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (Connection conn = DbConnectService.connect(); // Use DbConnectService's connect method
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("chips"),
                        rs.getInt("wins"),
                        rs.getInt("losses"),
                        rs.getInt("pushes")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all users: " + e.getMessage());
        }
        return users;
    }

    // Delete a user from the database
    public void deleteUser(String username) {
        String query = "DELETE FROM Users WHERE username = ?";
        try (Connection conn = DbConnectService.connect(); // Use DbConnectService's connect method
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }
}
