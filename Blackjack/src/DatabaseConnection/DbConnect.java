package DatabaseConnection;

import Services.UserService;

import java.sql.*;

public class DbConnect {
    // Supabase connection details
    private static final String URL = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.ojjqpawdgbwhktsbptya";
    private static final String PASSWORD = "eecs2311blackjack";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println(" Connected to Supabase PostgreSQL database!");
            // Step 1: Create Tables
            createTables(connection);

            // Step 2: Insert Sample Data
            insertData(connection);

            fetchUsers(connection);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createTables(Connection connection) {
        String query = "CREATE TABLE IF NOT EXISTS Users (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
            System.out.println("Users table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertData(Connection connection) {
        UserService userService = new UserService(connection);
        if (userService.addUser("admin1", "admin1234")) {
            System.out.println("Sample data inserted successfully.");
        } else {
            System.out.println("Failed to insert sample data or user already exists.");
        }
    }

    public static void fetchUsers(Connection connection){
        UserService userService = new UserService(connection);
        userService.fetchAllUsers();
    }

}
