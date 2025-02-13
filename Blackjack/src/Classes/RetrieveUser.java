package Classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RetrieveUser {
    private static final String URL = "jdbc:sqlite:blackjack.db";

    public static void getUser(String username) {
        String sql = "SELECT * FROM accounts WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Wins: " + rs.getInt("wins"));
                System.out.println("Chip Count: " + rs.getInt("chip_count"));
            } else {
                System.out.println("User not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getUser("player1");
    }
}
