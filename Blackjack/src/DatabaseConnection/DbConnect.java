package DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnect {
    public static void main(String[] args) {
        // Supabase connection details
        String url = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres?user=postgres.ojjqpawdgbwhktsbptya&password=eecs2311blackjack";
//        host:
//        aws-0-us-west-1.pooler.supabase.com
//
//        port:
//        6543
//
//        database:
//        postgres
//
//        user:
//        postgres.ojjqpawdgbwhktsbptya
//
//        pool_mode:
//        transaction



        try {
            // Establish connection
            Connection connection = DriverManager.getConnection(url);
            System.out.println("✅ Connected to Supabase PostgreSQL database!");

            // Close connection
            connection.close();
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage() );
            e.printStackTrace();
        }
    }
}
