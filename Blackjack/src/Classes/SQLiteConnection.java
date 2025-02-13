package Classes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:database.db"; // SQLite database file (in project folder)

        try {
        	Connection connection = DriverManager.getConnection(url);
            System.out.println("Connected to SQLite database!");
            connection.close(); // closing the connection when done
        } catch (SQLException e) {
          System.out.println("Error" + e.getMessage());
        }
    }
}
