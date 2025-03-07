package DatabaseConnection;

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

            // Step 3: Display Appointments
            displayAppointments(connection);

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to create tables
    private static void createTables(Connection connection) throws SQLException {
        String createPatientTable = "CREATE TABLE IF NOT EXISTS Patient (" +
                "patientId INT PRIMARY KEY NOT NULL, " +
                "name VARCHAR(255) NOT NULL, " +
                "dob DATE NOT NULL)";

        String createDoctorTable = "CREATE TABLE IF NOT EXISTS Doctor (" +
                "doctorId INT PRIMARY KEY NOT NULL, " +
                "name VARCHAR(255) NOT NULL, " +
                "specialty VARCHAR(255) NOT NULL)";

        String createAppointmentTable = "CREATE TABLE IF NOT EXISTS Appointment (" +
                "appointmentId INT PRIMARY KEY NOT NULL, " +
                "patientId INT NOT NULL, " +
                "doctorId INT NOT NULL, " +
                "FOREIGN KEY (patientId) REFERENCES Patient(patientId), " +
                "FOREIGN KEY (doctorId) REFERENCES Doctor(doctorId))";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPatientTable);
            stmt.execute(createDoctorTable);
            stmt.execute(createAppointmentTable);
            System.out.println(" Tables created successfully (if they didn't exist).");
        }
    }

    // Method to insert data into tables
    private static void insertData(Connection connection) throws SQLException {
        // Insert Patient Data
        String insertPatient = "INSERT INTO Patient (patientId, name, dob) VALUES (1, 'John', '1980-05-12') " +
                "ON CONFLICT (patientId) DO NOTHING";

        // Insert Doctor Data
        String insertDoctor = "INSERT INTO Doctor (doctorId, name, specialty) VALUES (1, 'Dr. Smith', 'Cardiology') " +
                "ON CONFLICT (doctorId) DO NOTHING";

        // Insert Appointment Data
        String insertAppointment = "INSERT INTO Appointment (appointmentId, patientId, doctorId) VALUES (1, 1, 1) " +
                "ON CONFLICT (appointmentId) DO NOTHING";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(insertPatient);
            stmt.execute(insertDoctor);
            stmt.execute(insertAppointment);
            System.out.println(" Sample data inserted successfully.");
        }
    }

    // Method to display all records from Appointment table
    private static void displayAppointments(Connection connection) throws SQLException {
        String query = "SELECT * FROM Appointment";

        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            System.out.println("\n Appointments:");
            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointmentId");
                int patientId = resultSet.getInt("patientId");
                int doctorId = resultSet.getInt("doctorId");

                System.out.println(" Appointment ID: " + appointmentId +
                        " |  Patient ID: " + patientId +
                        " |  Doctor ID: " + doctorId);
            }
        }
    }
}
